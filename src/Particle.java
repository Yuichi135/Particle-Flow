import javafx.scene.transform.Affine;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.attribute.AclFileAttributeView;

public class Particle {
    private Vector2D position;
    private Vector2D positionOld;
    private Vector2D acceleration;
    private double size;
    private BufferedImage texture;
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

    public void setTexture(BufferedImage image) {
        this.texture = image;
    }

    public void unsetTexture() {
        this.texture = null;
    }

    public void setColor(Color color) {
        this.color = color;
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
        if (texture == null) {
            graphics.setComposite(opacity);
            graphics.setColor(color);
            graphics.fill(getShape());
            graphics.setColor(Color.WHITE);
        } else {
            AffineTransform tx = new AffineTransform();
            tx.translate(position.getX() - size / 2, position.getY() - size / 2);
            tx.scale(size / texture.getWidth(), size / texture.getHeight());
            graphics.drawImage(texture, tx, null);
        }

        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    public Shape getShape() {
        return new Rectangle2D.Double(position.getX() - size / 2, position.getY() - size / 2, size, size);
    }
}
