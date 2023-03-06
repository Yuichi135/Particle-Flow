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
    public void setDirectionVector(Vector2D vector) {
        this.directionVector = vector;
    }

    @Override
    public void draw(FXGraphics2D graphics) {
        if (distance == Double.POSITIVE_INFINITY)
            return;

        graphics.setColor(Color.getHSBColor((float) (distance / 100), 1,1));
        graphics.fill(new Rectangle2D.Double(position.x * size, position.y * size, size, size));
        graphics.setColor(Color.WHITE);
        graphics.drawString(Math.round(distance * 100) / 100.0 + "", position.x * size, (position.y    * size + graphics.getFont().getSize()));

//        if (directionVector.getY() == 0.0 && directionVector.getX() == 0.0)
//            return;

        double x = position.getX() * size + size/2.0;
        double y = position.getY() * size + size/2.0;

//        System.out.println(directionVector);

        graphics.draw(new Line2D.Double(x, y, x + directionVector.getX(), y + directionVector.getY()));
    }
}
