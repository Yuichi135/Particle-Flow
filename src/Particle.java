import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Particle {
    private Vector2D position;
    private Vector2D positionOld;
    private Vector2D acceleration;
    private double size;
    private Color color;
    private AlphaComposite opacity;

    public Particle(Vector2D position, double size, Color color, AlphaComposite opacity) {
        this.position = position;
        this.positionOld = (Vector2D) position.clone();
        this.size = size;
        this.color = color;
        this.opacity = opacity;

        this.acceleration = new Vector2D();
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
        this.positionOld = (Vector2D) position;
    }

    public void setPositionX(double newX) {
        position.setX(newX);
    }

    public void setPositionY(double newY) {
        position.setY(newY);
    }

    public void update(double deltaTime) {
        acceleration.scale(deltaTime * deltaTime);

        Vector2D velocity = VectorMath.subtract(position, positionOld);
        if (velocity.getLength() > 5)
            velocity.setLength(5);

        positionOld = position;
        position = VectorMath.sum(position, velocity, acceleration);

        acceleration.setLocation(0, 0);
    }

    public void applyForce(Vector2D force) {
        if (force != null)
            acceleration.add(VectorMath.scale(force, 100));
    }

    public void draw(FXGraphics2D graphics) {
        graphics.setComposite(opacity);
        graphics.setColor(color);
        graphics.fill(getShape());
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        graphics.setColor(Color.WHITE);
    }

    public Shape getShape() {
        return new Rectangle2D.Double(position.getX() - size / 2, position.getY() - size / 2, size, size);
    }
}
