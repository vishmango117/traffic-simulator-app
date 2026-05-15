package trafficsimulator;

/**
 * Represents a vehicle moving along the road network.
 *
 * <p>Each car has a grid position, a cardinal direction it is travelling in,
 * and a speed flag (0 = stopped, 1 = moving).  Movement is discrete: every
 * simulation tick {@link Grid#updateCarPos} advances the car exactly one tile
 * in its current direction, subject to intersection and traffic-light checks.
 *
 * <p>Direction is encoded as a single character:
 * <ul>
 *   <li>{@code 'N'} — northbound (Y decreases)</li>
 *   <li>{@code 'S'} — southbound (Y increases)</li>
 *   <li>{@code 'E'} — eastbound  (X increases)</li>
 *   <li>{@code 'W'} — westbound  (X decreases)</li>
 * </ul>
 *
 * <p>When a car enters a three-way or four-way intersection its direction may
 * change: the routing logic in {@link Grid#updateCarPos} picks an exit road at
 * random from the intersection's {@code next_position} list and updates both
 * the car's position and direction accordingly.
 */
public class Car {

    /** Unique index used to identify this car within the simulation. */
    private int car_no;

    /** Current tile position on the grid. */
    private Coordinates car_pos;

    /**
     * Speed of the car.  {@code 0} = stopped, {@code 1} = moving one tile per
     * simulation tick.  Call {@link #Move()} or {@link #Stop()} to change.
     */
    private int car_speed;

    /** Current direction of travel: {@code 'N'}, {@code 'S'}, {@code 'E'}, or {@code 'W'}. */
    private char direction;

    /**
     * Creates a car at the given position, heading in the given direction.
     * The car starts stopped ({@code car_speed = 0}); call {@link #Move()} to
     * set it in motion.
     *
     * @param car_no    unique identifier
     * @param car_pos   starting tile on the grid
     * @param direction initial direction of travel
     */
    public Car(int car_no, Coordinates car_pos, char direction) {
        this.car_no = car_no;
        this.car_pos = car_pos;
        this.car_speed = 0;
        this.direction = direction;
    }

    /** Sets the car's speed to 1 (one tile per simulation tick). */
    public void Move() { this.car_speed = 1; }

    /** Sets the car's speed to 0 (stationary). */
    public void Stop() { this.car_speed = 0; }

    /** @return current tile position */
    public Coordinates getCar_pos() { return car_pos; }

    /** @param car_pos new tile position */
    public void setCar_pos(Coordinates car_pos) { this.car_pos = car_pos; }

    /** @return current speed (0 = stopped, 1 = moving) */
    public int getCar_speed() { return car_speed; }

    /** @param car_speed new speed value */
    public void setCar_speed(int car_speed) { this.car_speed = car_speed; }

    /** @return current direction of travel */
    public char getDirection() { return direction; }

    /**
     * Updates the car's direction.  Called by {@link Grid#updateCarPos} when
     * the car exits through an intersection and is assigned a new exit road.
     *
     * @param direction new direction of travel
     */
    public void setDirection(char direction) { this.direction = direction; }

    /** @return unique car index */
    public int getCar_no() { return car_no; }

    /** @param car_no new identifier */
    public void setCar_no(int car_no) { this.car_no = car_no; }
}
