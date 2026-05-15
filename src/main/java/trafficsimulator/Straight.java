package trafficsimulator;

/**
 * A straight-through (pass-through) intersection.
 *
 * <p>When a car enters a {@code "ST1"} tile it is immediately teleported to
 * the start of {@code next_road}.  This models a lane change, a merge point,
 * or any scenario where two road segments are logically connected but have a
 * gap on the grid.
 *
 * <p>Unlike {@link Threeway} and {@link Fourway}, there is no random routing:
 * cars always exit onto the single {@code next_road}.
 */
public class Straight extends Intersection {

    /**
     * The road segment a car is placed onto after passing through this
     * intersection.  The car is positioned at {@code next_road.getStart_pos()}.
     */
    private Road next_road;

    /**
     * Creates a straight-through intersection.
     *
     * @param int_road  zero-length road identifying the intersection tile
     * @param next_road the road a car is teleported to on exit
     */
    public Straight(Road int_road, Road next_road) {
        super(int_road);
        this.next_road = next_road;
    }

    /** Creates an uninitialised straight intersection. */
    public Straight() {
        super();
    }

    /**
     * Returns the road a car enters after passing through this intersection.
     *
     * @return exit road (car is placed at {@code next_road.getStart_pos()})
     */
    public Road getNext_road() { return next_road; }

    /**
     * Sets the exit road for this intersection.
     *
     * @param next_road road to place the car onto on exit
     */
    public void setNext_road(Road next_road) { this.next_road = next_road; }
}
