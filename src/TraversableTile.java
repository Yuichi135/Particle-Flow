import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

public class TraversableTile implements Tile {
    private Point position;
    private int size;
    private double distance;
    private Vector2D directionVector;

    public TraversableTile(Point position, int size) {
        this.position = position;
        this.size = size;
        this.directionVector = new Vector2D();
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Vector2D getDirectionVector() {
        if (distance == Double.POSITIVE_INFINITY)
            return null;
        return directionVector;
    }

    @Override
    public void setDirectionVector(Vector2D vector) {
        this.directionVector = vector;
    }

    private Color getColor() {
        return Color.getHSBColor((float) (distance / 75) % 1, 1f, 1f);
    }

    @Override
    public void draw(FXGraphics2D graphics, int mode) {
        if (distance == Double.POSITIVE_INFINITY)
            return;

        if ((mode & 0b001) == 0b001) {
            graphics.setColor(getColor());
            graphics.fill(new Rectangle2D.Double(position.x * size, position.y * size, size, size));
            graphics.setColor(Color.WHITE);
        }
        if ((mode & 0b010) == 0b010) {
            double x = position.getX() * size + size / 2.0;
            double y = position.getY() * size + size / 2.0;

            graphics.draw(new Rectangle2D.Double(x - 1, y - 1, 2, 2));
            graphics.draw(new Line2D.Double(x, y, x + directionVector.getX(), y + directionVector.getY()));
        }
        if (((mode & 0b100) == 0b100)) {
            graphics.drawString(Math.round(distance * 10) / 10.0 + "", position.x * size, (position.y * size + graphics.getFont().getSize()));
        }
    }

    @Override
    public Shape getShape() {
        return new Rectangle2D.Double(position.x * size, position.y * size, size, size);
    }
}
