import java.util.Comparator;

public class TileComparator implements Comparator<Tile> {
    @Override
    public int compare(Tile t1, Tile t2) {
        return (t1.getDistance() > t2.getDistance()) ? 1 : -1;
    }
}
