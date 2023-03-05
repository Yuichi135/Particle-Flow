import org.jfree.fx.FXGraphics2D;

public interface Tile {
    double getDistance();
    void draw(FXGraphics2D graphics);
}
