package trafficsimulator;

/**
 * Base class for all intersection types in the simulation.
 *
 * <p>An intersection occupies a single tile on the grid.  That tile is
 * represented by a {@link Road} whose {@code start_pos} and {@code end_pos}
 * are identical — it is a degenerate zero-length road used purely as a
 * position holder.
 *
 * <p>{@link Grid#drawGrid()} writes a type-specific token to the grid array
 * for each concrete subclass:
 * <ul>
 *   <li>{@code "ST1"} — {@link Straight} (pass-through / lane change)</li>
 *   <li>{@code "TW1"} — {@link Threeway} (T-junction, 2 exit roads)</li>
 *   <li>{@code "FW1"} — {@link Fourway} (full crossroads, 3 exit roads sampled
 *       randomly)</li>
 * </ul>
 *
 * <p>Subclasses add an {@code ArrayList<Road>} of exit roads that
 * {@link Grid#updateCarPos} consults when a car leaves the intersection tile.
 */
public class Intersection {

    /**
     * Zero-length {@link Road} whose {@code start_pos} (== {@code end_pos})
     * is the tile coordinate of this intersection on the grid.
     */
    private Road int_road;

    /**
     * Creates an intersection at the tile defined by {@code int_road}.
     *
     * @param int_road a zero-length road whose position marks this intersection
     */
    public Intersection(Road int_road) {
        this.int_road = int_road;
    }

    /** Creates an uninitialised intersection (position not yet set). */
    public Intersection() {
    }

    /**
     * Returns the zero-length road that identifies the intersection's tile.
     * Use {@code getInt_road().getStart_pos()} to obtain the grid coordinate.
     *
     * @return position-holder road for this intersection
     */
    public Road getInt_road() { return int_road; }

    /**
     * Sets the position-holder road for this intersection.
     *
     * @param int_road zero-length road identifying the intersection tile
     */
    public void setInt_road(Road int_road) { this.int_road = int_road; }
}
