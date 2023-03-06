import java.awt.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class WaveFrontAlgorithm {
    private final int width;
    private final int height;

    public WaveFrontAlgorithm(int width, int height) {
        this.width = width;
        this.height = height;

        System.out.println("Init WFA");
    }

    public void updateGrid(Tile[] grid, Point goalPoint) {
        System.out.println("Update WFM");
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

    private boolean isAccessible(Tile[] grid, int x, int y, int relativeX, int relativeY) {
        return (grid[(x - relativeX) * height + y].getDistance() != -1 && grid[x * height + y - relativeY].getDistance() != -1);
//        return (grid[(/x - relativeX) * height + y].getDistance() != -1);
    }

    private boolean isOutOfBounds(int x, int y) {
        return (x < 0 || y < 0 || x > width || y > height);
    }

    private void resetGrid(Tile[] grid) {
        for (Tile tile : grid) {
            tile.setDistance(Double.POSITIVE_INFINITY);
        }
    }
}
