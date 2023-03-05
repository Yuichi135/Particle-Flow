import org.jfree.fx.FXGraphics2D;

public class TraversableTile implements Tile {
    private Vector2D position;
    private int size;
    private double distance;

    public TraversableTile(Vector2D position, int size) {
        this.position = position;
        this.size = size;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void draw(FXGraphics2D graphics) {

    }
}
