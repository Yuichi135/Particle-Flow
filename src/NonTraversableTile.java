import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

public class NonTraversableTile implements Tile {
    private Point position;
    private int size;

    public NonTraversableTile(Point position, int size) {
        this.position = position;
        this.size = size;
    }

    @Override
    public void setDistance(double distance) {

    }

    @Override
    public double getDistance() {
        return -1;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Vector2D getDirectionVector() {
        return null;
    }

    @Override
    public void setDirectionVector(Vector2D vector) {

    }

    @Override
    public void draw(FXGraphics2D graphics, int mode) {
        graphics.fill(getShape());

        if (mode == 1) {
            graphics.setColor(Color.GREEN);
            graphics.draw(getShape());
            graphics.setColor(Color.WHITE);
        }

    }

    @Override
    public Shape getShape() {
        return new Rectangle2D.Double(position.x * size, position.y * size, size, size);
    }
}
