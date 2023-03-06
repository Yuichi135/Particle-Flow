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
    public void draw(FXGraphics2D graphics) {
        graphics.fill(getShape());
    }

    private Shape getShape() {
        return new Rectangle2D.Double(position.x * size, position.y * size, size, size);
    }
}
