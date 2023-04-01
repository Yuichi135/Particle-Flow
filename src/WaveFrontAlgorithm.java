import java.awt.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class WaveFrontAlgorithm {
    private final int width;
    private final int height;

    public WaveFrontAlgorithm(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void updateGrid(Tile[] grid, Point goalPoint) {
        resetGrid(grid);
        Tile goal = grid[goalPoint.x * height + goalPoint.y];
        goal.setDistance(0);

        PriorityQueue<Tile> queue = new PriorityQueue<>(1, new TileComparator());
        queue.add(goal);

        while (!queue.isEmpty()) {
            Tile activeTile = queue.poll();
            getDirectNeighbours(grid, activeTile);

            // Direct heeft een distance van 1
            for (Tile directNeighbour : getDirectNeighbours(grid, activeTile)) {
                if (directNeighbour.getDistance() != Double.POSITIVE_INFINITY || directNeighbour.getDistance() == -1)
                    continue;
                directNeighbour.setDistance(activeTile.getDistance() + 1);
                queue.add(directNeighbour);
            }

            // Direct heeft een distance van sqrt 2
            for (Tile diagonalNeighbour : getDiagonalNeighbours(grid, activeTile)) {
                if (diagonalNeighbour.getDistance() != Double.POSITIVE_INFINITY || diagonalNeighbour.getDistance() == -1)
                    continue;
                diagonalNeighbour.setDistance(activeTile.getDistance() + Math.sqrt(2));
                queue.add(diagonalNeighbour);
            }
        }

        for (Tile tile : grid) {
            calculateDirectionVectors(grid, tile);
        }
    }

    private ArrayList<Tile> getDirectNeighbours(Tile[] grid, Tile tile) {
        ArrayList<Tile> neighbours = new ArrayList<>(4);

        for (int relativeX = -1; relativeX <= 1; relativeX += 2) {
            int x = relativeX + tile.getPosition().x;
            int y = tile.getPosition().y;

            if (!isOutOfBounds(x, y))
                neighbours.add(grid[x * height + y]);
        }

        for (int relativeY = -1; relativeY <= 1; relativeY += 2) {
            int x = tile.getPosition().x;
            int y = relativeY + tile.getPosition().y;

            if (!isOutOfBounds(x, y))
                neighbours.add(grid[x * height + y]);
        }

        return neighbours;
    }

    private ArrayList<Tile> getDiagonalNeighbours(Tile[] grid, Tile tile) {
        ArrayList<Tile> neighbours = new ArrayList<>(4);

        for (int relativeX = -1; relativeX <= 1; relativeX += 2) {
            for (int relativeY = -1; relativeY <= 1; relativeY += 2) {
                int x = relativeX + tile.getPosition().x;
                int y = relativeY + tile.getPosition().y;

                if (!isOutOfBounds(x, y) && isAccessible(grid, x, y, relativeX, relativeX))
                    neighbours.add(grid[x * height + y]);
            }
        }

        return neighbours;
    }

    private ArrayList<Tile> getAllNeighbours(Tile[] grid, Tile tile) {
        ArrayList<Tile> neighbours = new ArrayList<>();

        neighbours.addAll(getDirectNeighbours(grid, tile));
        neighbours.addAll(getDiagonalNeighbours(grid, tile));

        return neighbours;
    }

    private void calculateDirectionVectors(Tile[] grid, Tile tile) {
        if (tile.getDistance() == -1)
            return;

        ArrayList<Tile> directNeighbours = (ArrayList<Tile>) getDirectNeighbours(grid, tile).stream().filter(neighbour -> neighbour.getDistance() != -1).collect(Collectors.toList());

        Vector2D directionVector = new Vector2D();
        Vector2D diagonalDirectionVector = new Vector2D();

        if (tile.getDistance() == 0) {
            tile.setDirectionVector(directionVector);
            return;
        }

        if (directNeighbours.size() == 4) {
            directionVector.setX(directNeighbours.get(0).getDistance() - directNeighbours.get(1).getDistance());
            directionVector.setY(directNeighbours.get(2).getDistance() - directNeighbours.get(3).getDistance());

            ArrayList<Tile> diagonalNeighbours = (ArrayList<Tile>) getDiagonalNeighbours(grid, tile).stream().filter(neighbour -> neighbour.getDistance() != -1).collect(Collectors.toList());
            if (diagonalNeighbours.size() == 4) {
                double forceTop = diagonalNeighbours.get(0).getDistance() + diagonalNeighbours.get(2).getDistance();
                double forceBottom = diagonalNeighbours.get(1).getDistance() + diagonalNeighbours.get(3).getDistance();
                double forceLeft = diagonalNeighbours.get(0).getDistance() + diagonalNeighbours.get(1).getDistance();
                double forceRight = diagonalNeighbours.get(2).getDistance() + diagonalNeighbours.get(3).getDistance();

                diagonalDirectionVector.setX(forceBottom - forceTop);
                diagonalDirectionVector.setY(forceLeft - forceRight);
                diagonalDirectionVector.rotate(-90);
            }
        } else {
            ArrayList<Tile> allNeighbours = (ArrayList<Tile>) getAllNeighbours(grid, tile).stream().filter(neighbour -> neighbour.getDistance() != -1).collect(Collectors.toList());

            if (allNeighbours.size() == 0)
                return;

            Tile closest = allNeighbours.get(0);
            for (Tile neighbour : allNeighbours) {
                if (closest.getDistance() > neighbour.getDistance())
                    closest = neighbour;
            }

            directionVector.setLocation(tile.getPosition());
            directionVector.subtract(closest.getPosition());
            directionVector.scale(-1);
        }

        directionVector.add(diagonalDirectionVector);
        directionVector.setLength(10);
        tile.setDirectionVector(directionVector);
    }

    private boolean isAccessible(Tile[] grid, int x, int y, int relativeX, int relativeY) {
        return (grid[(x - relativeX) * height + y].getDistance() != -1 && grid[x * height + y - relativeY].getDistance() != -1);
    }

    public boolean isOutOfBounds(int x, int y) {
        return (x < 0 || y < 0 || x >= width || y >= height
                || x * y > (width * height) - 1 || x * height + y > (width * height) - 1);
    }

    private void resetGrid(Tile[] grid) {
        for (Tile tile : grid) {
            tile.setDistance(Double.POSITIVE_INFINITY);
        }
    }
}
