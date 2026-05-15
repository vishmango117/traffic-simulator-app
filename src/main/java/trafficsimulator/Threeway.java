package trafficsimulator;

import java.util.ArrayList;

/**
 * A T-junction (three-way intersection) where a car may exit onto one of two
 * possible roads, chosen at random.
 *
 * <p>The tile is marked {@code "TW1"} in {@link Grid#mygrid_roads}.  When
 * {@link Grid#updateCarPos} detects that the car's current tile is {@code "TW1"}
 * and its next tile is {@code "RD1"}, it:
 * <ol>
 *   <li>Finds the {@code Threeway} object whose position matches the car.</li>
 *   <li>Picks a random index in {@code [0, 1]} from {@code next_position}.</li>
 *   <li>Updates the car's direction and position to the chosen exit road.</li>
 * </ol>
 *
 * <p>Exit road placement (start vs end) depends on the chosen road's
 * direction: westbound roads use {@code end_pos}, all others use
 * {@code start_pos}.  This is a simplification — correct behaviour for all
 * direction combinations would require per-entry-direction routing tables.
 *
 * <p><b>Known bug:</b> the single-argument constructor
 * {@code Threeway(ArrayList)} assigns {@code this.next_position = next_position}
 * where {@code next_position} on the right-hand side refers to the uninitialised
 * field, not the parameter.  Always use the two-argument constructor.
 */
public class Threeway extends Intersection {

    /**
     * The two road segments a car can exit onto from this junction.
     * {@link Grid#updateCarPos} selects among indices {@code [0, 1]} using
     * {@code Random.nextInt(2)}.
     */
    private ArrayList<Road> next_position;

    /**
     * Creates a three-way intersection with the given exit roads.
     *
     * @param int_road      zero-length road identifying the junction tile
     * @param next_position list of exactly two exit roads
     */
    public Threeway(Road int_road, ArrayList<Road> next_position) {
        super(int_road);
        this.next_position = next_position;
    }

    /**
     * @deprecated Use {@link #Threeway(Road, ArrayList)} instead.
     *             This constructor has a bug: the {@code next_position} field
     *             is not set from the parameter.
     */
    @Deprecated
    public Threeway(ArrayList<Road> next_positions) {
        this.next_position = next_position; // bug: reads uninitialized field
    }

    /** Creates an uninitialised three-way intersection. */
    public Threeway() {
    }

    /**
     * Returns the list of exit roads for this junction.
     * Callers in {@link Grid#updateCarPos} access indices {@code 0} and
     * {@code 1} only — ensure the list contains at least two elements.
     *
     * @return mutable list of exit roads
     */
    public ArrayList<Road> getNext_position() { return next_position; }
}
