import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Obstacle {
    private Vector2D position;
    private int size;

    public Obstacle(Vector2D position, int size) {
        this.position = position;
        this.size = size;
    }

    public void draw(FXGraphics2D graphics) {
        graphics.fill(getShape());
    }

    private Shape getShape() {
        return new Rectangle2D.Double(position.getX() * size, position.getY() * size, size, size);
    }
}
