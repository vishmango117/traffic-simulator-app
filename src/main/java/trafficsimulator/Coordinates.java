package trafficsimulator;

/**
 * Represents a 2-D grid position using integer (X, Y) tile coordinates.
 *
 * <p>The grid is indexed row-major as {@code array[Y][X]}, so when reading or
 * writing the backing arrays in {@link Grid} always pass Y as the first index
 * and X as the second.  {@code Coordinates} stores them as named fields to
 * avoid confusion at call sites.
 *
 * <p>Cardinal directions map to coordinate deltas as follows:
 * <pre>
 *   N → (0, -1)   (decreasing Y = moving up the screen)
 *   S → (0, +1)   (increasing Y = moving down the screen)
 *   E → (+1, 0)   (increasing X = moving right)
 *   W → (-1, 0)   (decreasing X = moving left)
 * </pre>
 */
public class Coordinates {

    /** Column index (horizontal axis, left = 0). */
    private int X_Pos;

    /** Row index (vertical axis, top = 0). */
    private int Y_Pos;

    /**
     * Creates a coordinate at the given tile position.
     *
     * @param X_Pos column (horizontal, 0-based)
     * @param Y_Pos row    (vertical,   0-based)
     */
    public Coordinates(int X_Pos, int Y_Pos) {
        this.X_Pos = X_Pos;
        this.Y_Pos = Y_Pos;
    }

    /**
     * Creates a zero-initialised coordinate {@code (0, 0)}.
     * Used as a placeholder before a real position is computed.
     */
    public Coordinates() {
    }

    /**
     * Returns the component-wise sum of two coordinates.
     *
     * <p>Typical usage is moving one step in a direction:
     * {@code add_coordinate(carPos, carPos.direction_to_coords('N'))}
     *
     * @param coords1 base position
     * @param coords2 delta to add (often produced by {@link #direction_to_coords})
     * @return new {@code Coordinates} whose X = coords1.X + coords2.X,
     *         Y = coords1.Y + coords2.Y
     */
    public static Coordinates add_coordinate(Coordinates coords1, Coordinates coords2) {
        Coordinates result = new Coordinates(0, 0);
        result.setX_Pos(coords1.X_Pos + coords2.X_Pos);
        result.setY_Pos(coords1.Y_Pos + coords2.Y_Pos);
        return result;
    }

    /**
     * Converts a cardinal direction character into a one-tile delta coordinate.
     *
     * <p>Add the returned delta to a position to obtain the neighbouring tile
     * in that direction.  An unrecognised character returns {@code (0, 0)}.
     *
     * @param direction one of {@code 'N'}, {@code 'S'}, {@code 'E'}, {@code 'W'}
     * @return unit-step delta for the requested direction
     */
    public Coordinates direction_to_coords(char direction) {
        if (direction == 'N') return new Coordinates(0, -1);
        if (direction == 'S') return new Coordinates(0,  1);
        if (direction == 'E') return new Coordinates(1,  0);
        if (direction == 'W') return new Coordinates(-1, 0);
        return new Coordinates(0, 0);
    }

    /** @return row index (vertical axis) */
    public int getY_Pos() { return Y_Pos; }

    /** @param y_Pos new row index */
    public void setY_Pos(int y_Pos) { Y_Pos = y_Pos; }

    /** @return column index (horizontal axis) */
    public int getX_Pos() { return X_Pos; }

    /** @param x_Pos new column index */
    public void setX_Pos(int x_Pos) { X_Pos = x_Pos; }
}
