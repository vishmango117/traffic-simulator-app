package trafficsimulator;

/**
 * Represents a traffic light placed at a specific tile on the grid.
 *
 * <p>A traffic light is rendered as a {@code "TL1"} token in
 * {@link Grid#mygrid_roads} and drawn green or red by the UI.  Its position
 * is offset from the centre of the intersection it controls — see
 * {@link Grid#checkTrafficLight} for the exact offset convention used to match
 * a car's look-ahead tile to the correct light.
 *
 * <p>The light cycles between two states:
 * <ul>
 *   <li>{@code "G"} — green: cars approaching the intersection are permitted
 *       to enter.</li>
 *   <li>{@code "R"} — red: cars must wait and will not advance into the
 *       intersection tile.</li>
 * </ul>
 *
 * <p>{@link #operate()} toggles between these two states and is called by the
 * simulation loop every five ticks (≈ 2.5 seconds at the default 500 ms tick
 * rate).
 *
 * <p><b>Known limitation:</b> colour comparisons inside {@link #operate()} and
 * {@link Grid#checkTrafficLight} use {@code ==} on string literals, which
 * relies on JVM string interning.  This works in practice because the values
 * are always assigned from string literals, but should be replaced with
 * {@code .equals()} for correctness.
 */
public class TrafficLight {

    /**
     * Current state of the light: {@code "G"} (green) or {@code "R"} (red).
     */
    private String color;

    /**
     * Tile on the grid occupied by this traffic light.
     * Positioned one tile away from the intersection centre, in the direction
     * the approaching car comes from.
     */
    private Coordinates Position;

    /**
     * Creates a traffic light with an explicit initial colour.
     *
     * @param color    initial state — {@code "G"} or {@code "R"}
     * @param position tile coordinate where this light is placed on the grid
     */
    public TrafficLight(String color, Coordinates position) {
        this.color = color;
        Position = position;
    }

    /**
     * Creates an uninitialised traffic light (no colour or position set).
     * Used as a sentinel/placeholder inside {@link Grid#checkTrafficLight}
     * before the matching light is found.
     */
    public TrafficLight() {
    }

    /** @return current colour: {@code "G"} or {@code "R"} */
    public String getColor() { return color; }

    /** @return grid tile occupied by this light */
    public Coordinates getPosition() { return Position; }

    /**
     * Toggles the light between green and red.
     *
     * <p>Called by the simulation loop in {@link backup#process_run} every
     * five ticks so that all lights at an intersection switch simultaneously.
     * Green becomes red, red becomes green.
     */
    public void operate() {
        if (this.color == "G") {
            this.color = "R";
        } else if (this.color == "R") {
            this.color = "G";
        }
    }
}
