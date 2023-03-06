import org.jfree.fx.FXGraphics2D;

import java.awt.*;

public interface Tile {
    void setDistance(double distance);
    double getDistance();
    Point getPosition();
    Vector2D getDirectionVector();
    void setDirectionVector(Vector2D vector);
    void draw(FXGraphics2D graphics, int mode);
    Shape getShape();
}
