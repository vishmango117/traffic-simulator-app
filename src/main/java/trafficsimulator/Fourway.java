package trafficsimulator;

import java.util.ArrayList;

/**
 * A four-way intersection (full crossroads) where a car may exit onto one of
 * three roads, chosen at random.
 *
 * <p>The tile is marked {@code "FW1"} in {@link Grid#mygrid_roads}.  When
 * {@link Grid#updateCarPos} detects that the car's current tile is {@code "FW1"}
 * and its next tile is {@code "RD1"}, it:
 * <ol>
 *   <li>Finds the {@code Fourway} object whose position matches the car.</li>
 *   <li>Picks a random index in {@code [0, 2]} from {@code next_position}
 *       (note: only the first three entries are ever selected, so add the most
 *       important exit roads at indices 0–2).</li>
 *   <li>Updates the car's direction and position to the chosen exit road.</li>
 * </ol>
 *
 * <p>Exit road placement (start vs end) follows the same direction-based
 * heuristic as {@link Threeway}: westbound roads use {@code end_pos},
 * northbound roads exiting from eastbound cars use {@code end_pos},
 * and all other cases use {@code start_pos}.
 *
 * <p>Preset layouts (see {@link Grid#layout1()}, {@link Grid#layout2()},
 * {@link Grid#layout3()}) add four roads to {@code next_position} but only
 * three are reachable at runtime due to the {@code nextInt(3)} bound.
 */
public class Fourway extends Intersection {

    /**
     * The road segments a car can exit onto from this intersection.
     * {@link Grid#updateCarPos} selects among indices {@code [0, 2]} using
     * {@code Random.nextInt(3)} — the fourth entry (index 3) is never picked.
     */
    private ArrayList<Road> next_position;

    /**
     * Creates a four-way intersection with the given exit roads.
     *
     * @param current_position zero-length road identifying the intersection tile
     * @param next_position    list of exit roads (at least three required for
     *                         correct simulation; a fourth is accepted but unreachable)
     */
    public Fourway(Road current_position, ArrayList<Road> next_position) {
        super(current_position);
        this.next_position = next_position;
    }

    /** Creates an uninitialised four-way intersection. */
    public Fourway() {
    }

    /**
     * Creates a four-way intersection without a position (position must be set
     * separately via {@link #setInt_road}).
     *
     * @param next_position list of exit roads
     */
    public Fourway(ArrayList<Road> next_position) {
        this.next_position = next_position;
    }

    /**
     * Returns the list of exit roads for this intersection.
     * Add roads at indices 0, 1, 2 for the three randomly chosen exits; a
     * fourth road at index 3 may be added for completeness but will not be
     * selected during simulation.
     *
     * @return mutable list of exit roads
     */
    public ArrayList<Road> getNext_position() { return next_position; }
}
