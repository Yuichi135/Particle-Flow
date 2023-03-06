import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

public class TraversableTile implements Tile {
    private Point position;
    private int size;
    private double distance;

    public TraversableTile(Point position, int size) {
        this.position = position;
        this.size = size;
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
    public void draw(FXGraphics2D graphics) {
        if (distance == Double.POSITIVE_INFINITY)
            return;

        graphics.setColor(Color.getHSBColor((float) (distance / 50), 1,1));
        graphics.fill(new Rectangle2D.Double(position.x * size, position.y * size, size, size));
        graphics.setColor(Color.WHITE);
        graphics.drawString(Math.round(distance * 100) / 100.0 + "", position.x * size, (position.y    * size + graphics.getFont().getSize()));
    }
}
