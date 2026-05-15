package trafficsimulator;

/**
 * Represents a straight road segment on the grid.
 *
 * <p>A road is axis-aligned: either horizontal (direction {@code 'E'}) or
 * vertical (direction {@code 'S'}).  The segment occupies every tile between
 * {@code start_pos} and {@code end_pos} inclusive, so these must share either
 * the same X coordinate (vertical) or the same Y coordinate (horizontal).
 *
 * <p>Convention used throughout the codebase:
 * <ul>
 *   <li>Horizontal roads use direction {@code 'E'} and have
 *       {@code start_pos.X &lt; end_pos.X}.</li>
 *   <li>Vertical roads use direction {@code 'S'} and have
 *       {@code start_pos.Y &lt; end_pos.Y}.</li>
 * </ul>
 *
 * <p>When a road begins or ends at the edge of the 51×51 grid (row 0, row 49,
 * column 0, or column 49) its boundary tile is marked {@code "SP1"} in
 * {@link Grid#drawGrid()} to indicate a vehicle spawn/despawn point.
 */
public class Road {

    /** Unique numeric identifier for this road segment. */
    private int road_no;

    /**
     * Top-left (or leftmost/topmost) tile of the road.
     * For horizontal roads this is the westernmost tile;
     * for vertical roads it is the northernmost tile.
     */
    private Coordinates start_pos;

    /**
     * Bottom-right (or rightmost/bottommost) tile of the road.
     * For horizontal roads this is the easternmost tile;
     * for vertical roads it is the southernmost tile.
     */
    private Coordinates end_pos;

    /** Reserved for future use — not currently populated. */
    private int no_of_segments;

    /**
     * Primary direction of travel along this road.
     * {@code 'E'} for horizontal roads, {@code 'S'} for vertical roads.
     */
    private char Direction;

    /**
     * Creates a road segment.
     *
     * @param road_no   unique identifier
     * @param start_pos topmost/leftmost tile of the segment
     * @param end_pos   bottommost/rightmost tile of the segment
     * @param Direction {@code 'E'} (horizontal) or {@code 'S'} (vertical)
     */
    public Road(int road_no, Coordinates start_pos, Coordinates end_pos, char Direction) {
        this.road_no = road_no;
        this.start_pos = start_pos;
        this.end_pos = end_pos;
        this.Direction = Direction;
    }

    /** @return topmost/leftmost tile coordinate of this segment */
    public Coordinates getStart_pos() { return start_pos; }

    /** @return bottommost/rightmost tile coordinate of this segment */
    public Coordinates getEnd_pos() { return end_pos; }

    /** @return reserved segment count (currently always 0) */
    public int getNo_of_segments() { return no_of_segments; }

    /** @return unique numeric identifier */
    public int getRoad_no() { return road_no; }

    /** @param road_no new identifier */
    public void setRoad_no(int road_no) { this.road_no = road_no; }

    /** @return primary direction: {@code 'E'} or {@code 'S'} */
    public char getDirection() { return Direction; }

    /** @param direction new primary direction */
    public void setDirection(char direction) { Direction = direction; }
}
