package trafficsimulator;


import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Central state container for the traffic simulation.
 *
 * <p>The grid is a 51 × 51 tile map backed by two parallel 2-D string arrays:
 * <ul>
 *   <li>{@link #mygrid_roads} — static road layer.  Indexed as
 *       {@code mygrid_roads[Y][X]}.  Possible cell values:
 *       <table>
 *         <tr><td>{@code "   "}</td><td>empty ground</td></tr>
 *         <tr><td>{@code "RD1"}</td><td>road tile</td></tr>
 *         <tr><td>{@code "SP1"}</td><td>spawn/despawn boundary tile</td></tr>
 *         <tr><td>{@code "ST1"}</td><td>straight-through intersection</td></tr>
 *         <tr><td>{@code "TW1"}</td><td>three-way (T) intersection</td></tr>
 *         <tr><td>{@code "FW1"}</td><td>four-way (crossroads) intersection</td></tr>
 *         <tr><td>{@code "TL1"}</td><td>traffic light</td></tr>
 *       </table>
 *   </li>
 *   <li>{@link #mygrid_vehicles} — vehicle layer.  Same indexing.
 *       {@code "CR1"} marks a tile occupied by a car; all other tiles are
 *       {@code "   "}.</li>
 * </ul>
 *
 * <p>The arrays are rebuilt from scratch on every call to {@link #drawGrid()}
 * so they always reflect the authoritative object lists ({@link #myroads},
 * {@link #mycars}, etc.).  The UI repaints after each rebuild.
 *
 * <p><b>Coordinate convention:</b> {@code Coordinates(X, Y)} but the arrays
 * are indexed {@code [Y][X]}.  Always pass Y as the first array index.
 */
class Grid {

    /**
     * Road layer of the tile map.  Indexed {@code [Y][X]}.
     * Rebuilt by {@link #drawGrid()} on every simulation tick.
     */
    public String[][] mygrid_roads;

    /**
     * Vehicle layer of the tile map.  Indexed {@code [Y][X]}.
     * Rebuilt by {@link #drawGrid()} on every simulation tick.
     */
    public String[][] mygrid_vehicles;

    /** Number of tile rows in the grid (Y dimension). */
    private int height;

    /** Number of tile columns in the grid (X dimension). */
    private int width;

    /** All road segments currently active in the simulation. */
    public ArrayList<Road> myroads;

    /** All vehicles currently active in the simulation. */
    public ArrayList<Car> mycars;

    /** All traffic lights currently placed on the grid. */
    public ArrayList<TrafficLight> mytrafficlights;

    /** All straight-through intersections (token {@code "ST1"}). */
    public ArrayList<Straight> my_straight_intersections;

    /** All three-way (T) intersections (token {@code "TW1"}). */
    public ArrayList<Threeway> my_threeway_intersections;

    /** All four-way (crossroads) intersections (token {@code "FW1"}). */
    public ArrayList<Fourway> my_fourway_intersections;

    /**
     * Creates a grid of the given dimensions and initialises the tile arrays.
     * The object lists (roads, cars, etc.) are initialised to empty
     * {@code ArrayList}s; populate them before calling {@link #drawGrid()}.
     *
     * @param height number of tile rows    (Y dimension, typically 51)
     * @param width  number of tile columns (X dimension, typically 51)
     */
    public Grid(int height, int width) {
        this.mygrid_roads    = new String[height][width];
        this.mygrid_vehicles = new String[height][width];
        this.height = height;
        this.width  = width;
        this.myroads = new ArrayList<>();
        this.mycars  = new ArrayList<>();
    }

    /** @return number of tile rows */
    public int getHeight() { return height; }

    /** @return number of tile columns */
    public int getWidth() { return width; }

    /**
     * Rebuilds both tile arrays from the current object lists.
     *
     * <p>Render order (each layer overwrites the previous for the same tile):
     * <ol>
     *   <li>Fill every cell with {@code "   "} (empty ground).</li>
     *   <li>Write {@code "RD1"} for every tile covered by a {@link Road}.</li>
     *   <li>Mark edge-touching road endpoints as {@code "SP1"} (spawn points).</li>
     *   <li>Overwrite intersection tiles: {@code "ST1"}, {@code "TW1"},
     *       {@code "FW1"}.</li>
     *   <li>Write {@code "CR1"} to {@link #mygrid_vehicles} for every car.</li>
     *   <li>Overwrite traffic-light tiles with {@code "TL1"}.</li>
     * </ol>
     *
     * <p>Because intersections overwrite road tiles and traffic lights overwrite
     * intersection tiles, the draw order matters: always call this method before
     * rendering the UI or evaluating movement logic.
     */
    public void drawGrid() {
        // 1. Reset both layers to empty ground
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                this.mygrid_roads[i][j]    = "   ";
                this.mygrid_vehicles[i][j] = "   ";
            }
        }

        // 2. Paint road tiles.  Roads are axis-aligned rectangles, so iterating
        //    Y from start to end and X from start to end fills every tile.
        for (Road currentroad : this.myroads) {
            for (int i = currentroad.getStart_pos().getY_Pos(); i <= currentroad.getEnd_pos().getY_Pos(); i++) {
                for (int j = currentroad.getStart_pos().getX_Pos(); j <= currentroad.getEnd_pos().getX_Pos(); j++) {
                    this.mygrid_roads[i][j] = "RD1";
                }
            }
            // Mark the top/bottom boundary tile as a spawn point
            if (currentroad.getStart_pos().getY_Pos() == 0 || currentroad.getStart_pos().getY_Pos() == 49) {
                System.out.println("yes");
                this.mygrid_roads[currentroad.getStart_pos().getY_Pos()][currentroad.getStart_pos().getX_Pos()] = "SP1";
            }
            // Mark the left/right boundary tile as a spawn point
            if (currentroad.getEnd_pos().getX_Pos() == 0 || currentroad.getStart_pos().getX_Pos() == 49) {
                this.mygrid_roads[currentroad.getStart_pos().getY_Pos()][currentroad.getStart_pos().getX_Pos()] = "SP1";
            }
        }

        // 3. Overwrite intersection tiles (they sit on top of road tiles)
        for (Straight s : my_straight_intersections) {
            this.mygrid_roads[s.getInt_road().getStart_pos().getY_Pos()][s.getInt_road().getStart_pos().getX_Pos()] = "ST1";
        }
        for (Threeway tw : my_threeway_intersections) {
            this.mygrid_roads[tw.getInt_road().getStart_pos().getY_Pos()][tw.getInt_road().getStart_pos().getX_Pos()] = "TW1";
        }
        for (Fourway fw : my_fourway_intersections) {
            this.mygrid_roads[fw.getInt_road().getStart_pos().getY_Pos()][fw.getInt_road().getStart_pos().getX_Pos()] = "FW1";
        }

        // 4. Place cars on the vehicle layer
        for (Car currentcar : this.mycars) {
            this.mygrid_vehicles[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] = "CR1";
        }

        // 5. Traffic lights overwrite whatever was on the road layer at their tile
        for (TrafficLight tl : mytrafficlights) {
            this.mygrid_roads[tl.getPosition().getY_Pos()][tl.getPosition().getX_Pos()] = "TL1";
        }
    }

    /**
     * Prints both tile layers to stdout as a 51-column ASCII table.
     * Car tiles ({@code "CR1"}) take precedence over road tokens.
     * Intended for debugging only — this floods the console at 500 ms intervals
     * when the simulation is running.
     */
    public void printGrid() {
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                if (this.mygrid_vehicles[i][j] == "CR1") {
                    System.out.printf("|%s|", this.mygrid_vehicles[i][j]);
                } else {
                    System.out.printf("|%s|", this.mygrid_roads[i][j]);
                }
            }
            System.out.println();
        }
    }

    /*
    public void displayGrid() {
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                //if(this.mygrid[i][j].equals("TB") || this.mygrid[i][j].equals("BB"))
                //   System.out.printf("-----");
                if (this.mygrid[i][j].equals("SB")) {
                    System.out.printf("|");
                } else if (this.mygrid[i][j].startsWith("R")) {
                    System.out.printf("|---|");
                } else if (this.mygrid[i][j].startsWith("C")) {
                    System.out.printf("|-C-|");
                } else if (this.mygrid[i][j].startsWith("I")) {
                    System.out.printf("|-X-|");
                } else if (this.mygrid[i][j].startsWith("T")) {
                    System.out.println("|-:-|");
                } else {
                    System.out.printf("|   |");
                }
            }
            System.out.println();
        }
    }*/

    /**
     * Returns {@code true} when no car is currently sitting on an intersection
     * tile ({@code "ST1"}, {@code "TW1"}, or {@code "FW1"}).
     *
     * <p>This is used as a simple mutual-exclusion guard: a car approaching an
     * intersection is only allowed to enter if the intersection tile is clear.
     * The check is global (any car on any intersection blocks all others), which
     * is conservative but avoids collisions in the current model.
     *
     * @return {@code true} if all intersection tiles are car-free
     */
    public boolean checkIntersection() {
        for (Car currentcar : mycars) {
            String tile = this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()];
            if (tile == "ST1" || tile == "TW1" || tile == "FW1") {
                return false;
            }
        }
        return true;
    }

    /**
     * Toggles every traffic light on the grid.
     * Delegates to {@link TrafficLight#operate()} for each light.
     * Currently unused — the simulation loop in {@link backup#process_run}
     * calls {@code operate()} directly on each light.
     */
    public void updateTrafficLight() {
        for (TrafficLight tl : mytrafficlights) {
            tl.operate();
        }
    }

    /**
     * Returns {@code true} if the traffic light governing the car's next move
     * is green, or if no matching light is found (open road assumed passable).
     *
     * <p>The look-ahead tile — where the light is expected to be — is computed
     * from the car's current position and direction using the following offsets:
     * <pre>
     *   Direction E → look at (car.X,     car.Y - 1)
     *   Direction W → look at (car.X,     car.Y + 1)
     *   Direction N → look at (car.X - 1, car.Y - 1)
     *   Direction S → look at (car.X + 1, car.Y    )
     * </pre>
     * These offsets are designed to match the positions set when building
     * preset layouts (e.g., offset {@code (-1, -1)} from intersection centre
     * for the eastbound approach light).
     *
     * <p><b>Known limitation:</b> the colour comparison uses {@code ==} on
     * string literals rather than {@code .equals()}.  This works due to JVM
     * string interning but is not safe in general.
     *
     * @param currentcar the car that is about to enter an intersection
     * @return {@code true} if the governing light is green (or absent)
     */
    public boolean checkTrafficLight(Car currentcar) {
        Coordinates mytlposition = new Coordinates();
        TrafficLight mytrafficlight = new TrafficLight();

        // Determine which grid tile the relevant traffic light occupies.
        // The offset is relative to the car's current position, not the
        // intersection centre.
        if (currentcar.getDirection() == 'E') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos(),     currentcar.getCar_pos().getY_Pos() - 1);
        } else if (currentcar.getDirection() == 'W') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos(),     currentcar.getCar_pos().getY_Pos() + 1);
        } else if (currentcar.getDirection() == 'N') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos() - 1, currentcar.getCar_pos().getY_Pos() - 1);
        } else if (currentcar.getDirection() == 'S') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos() + 1, currentcar.getCar_pos().getY_Pos());
        }

        // Find the light that occupies the computed look-ahead tile
        for (TrafficLight tl : mytrafficlights) {
            if (tl.getPosition().getY_Pos() == mytlposition.getY_Pos() &&
                    tl.getPosition().getX_Pos() == mytlposition.getX_Pos()) {
                mytrafficlight = tl;
            }
        }

        // No matching light found → treat as green (no restriction)
        return mytrafficlight.getColor() == "G";
    }

    /**
     * Advances every car by one tile according to the road network and
     * traffic-light state.
     *
     * <p>For each car the method computes {@code next_pos} (one step ahead in
     * the car's direction) and applies the first matching rule:
     *
     * <ol>
     *   <li><b>Approaching an intersection</b> ({@code next_pos} is ST1/TW1/FW1):
     *       enter only if {@link #checkIntersection()} AND
     *       {@link #checkTrafficLight(Car)} both return true.</li>
     *   <li><b>Road-to-road move</b> (current = RD1, next = RD1):
     *       advance unconditionally.</li>
     *   <li><b>Exiting a straight intersection</b> (current = ST1):
     *       teleport to the start of the matching {@link Straight#getNext_road()}.</li>
     *   <li><b>Exiting a three-way intersection</b> (current = TW1, next = RD1):
     *       pick one of two exit roads at random; set the car's new direction and
     *       position.  Westbound exit roads place the car at {@code end_pos};
     *       all others at {@code start_pos}.</li>
     *   <li><b>Exiting a four-way intersection</b> (current = FW1, next = RD1):
     *       same as three-way but chooses among three exit roads.</li>
     * </ol>
     *
     * <p>Cars that do not match any rule (e.g., sitting at a red light, or at a
     * dead end) remain stationary for this tick.
     *
     * @param mycars the list of cars to update (same reference as {@link #mycars})
     */
    public void updateCarPos(ArrayList<Car> mycars) {
        for (Car currentcar : mycars) {
            Coordinates current_pos = currentcar.getCar_pos();
            Coordinates next_pos    = Coordinates.add_coordinate(current_pos,
                    current_pos.direction_to_coords(currentcar.getDirection()));

            // Rule 1: next tile is an intersection — enter only when clear and light is green
            if (this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("ST1") ||
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("TW1") ||
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("FW1")) {
                if (checkIntersection() && checkTrafficLight(currentcar)) {
                    currentcar.setCar_pos(next_pos);
                }

            // Rule 2: open road — advance freely
            } else if (this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("RD1") &&
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {
                currentcar.setCar_pos(next_pos);

            // Rule 3: leaving a straight intersection — teleport to the linked road
            } else if (this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("ST1")) {
                for (Straight s : this.my_straight_intersections) {
                    if (s.getInt_road().equals(currentcar.getCar_pos())) {
                        currentcar.setCar_pos(s.getNext_road().getStart_pos());
                    }
                }

            // Rule 4: leaving a three-way intersection — random exit among two roads
            } else if (this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("TW1") &&
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {

                System.out.println("Yes");

                // Find the Threeway object whose tile matches the car's current position
                Threeway myintersection = new Threeway();
                for (Threeway tw : this.my_threeway_intersections) {
                    if (tw.getInt_road().getStart_pos().getX_Pos() == currentcar.getCar_pos().getX_Pos() &&
                            tw.getInt_road().getStart_pos().getY_Pos() == currentcar.getCar_pos().getY_Pos()) {
                        myintersection = tw;
                    }
                }

                // Choose one of the two exit roads at random
                int mynumber = new Random().nextInt(2);

                // Place the car at start_pos or end_pos depending on the exit road's
                // direction — westbound roads run right-to-left so the car enters at end_pos
                if (currentcar.getDirection() == 'N') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'W') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                } else if (currentcar.getDirection() == 'S') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                } else if (currentcar.getDirection() == 'E') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'N') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                } else if (currentcar.getDirection() == 'W') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                }

            // Rule 5: leaving a four-way intersection — random exit among three roads
            } else if (this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("FW1") &&
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {

                System.out.println("Yes");

                // Find the Fourway object whose tile matches the car's current position
                Fourway myintersection = new Fourway();
                for (Fourway fw : this.my_fourway_intersections) {
                    if (fw.getInt_road().getStart_pos().getX_Pos() == currentcar.getCar_pos().getX_Pos() &&
                            fw.getInt_road().getStart_pos().getY_Pos() == currentcar.getCar_pos().getY_Pos()) {
                        myintersection = fw;
                    }
                }

                // Choose one of the first three exit roads at random (index 3 is never chosen)
                int mynumber = new Random().nextInt(3);

                if (currentcar.getDirection() == 'N') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'W') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                } else if (currentcar.getDirection() == 'S') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'W') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                } else if (currentcar.getDirection() == 'E') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'N') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                } else if (currentcar.getDirection() == 'W') {
                    if (myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    } else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                }
            }
        }
    }

    /**
     * Opens a Swing dialog that collects road parameters and adds the new road
     * to {@link #myroads}.
     *
     * <p>The dialog loops until the user enters a valid cardinal direction
     * ({@code N}, {@code S}, {@code E}, or {@code W}).  On cancel the loop
     * exits but the method may throw a {@code NumberFormatException} if the
     * text fields are empty — no input validation is performed beyond direction
     * checking.
     *
     * <p>Coordinate semantics per direction:
     * <ul>
     *   <li>{@code N} — start = (x, y−size), end = (x, y)</li>
     *   <li>{@code S} — start = (x, y),       end = (x, y+size)</li>
     *   <li>{@code E} — start = (x, y),       end = (x+size, y)</li>
     *   <li>{@code W} — start = (x−size, y), end = (x, y)</li>
     * </ul>
     */
    public void create_road() {
        JTextField road_startpos  = new JTextField(10);
        JTextField road_endpos    = new JTextField(10);
        JTextField road_direction = new JTextField(10);
        JTextField road_size      = new JTextField(10);
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Road Start Pos (X-Value)"));
        myPanel.add(road_startpos);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("Road Start Pos (Y-Value)"));
        myPanel.add(road_endpos);
        myPanel.add(new JSeparator());
        myPanel.add(new JLabel("Direction"));
        myPanel.add(road_direction);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("Size"));
        myPanel.add(road_size);

        // Keep asking until the user supplies a valid direction or cancels
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Please Enter Road Length and Direction", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                System.out.println("Road Start Position(X): " + Integer.parseInt(road_startpos.getText()));
                System.out.println("Road Start Position(Y):  " + Integer.parseInt(road_endpos.getText()));
                System.out.println("Road Direction: " + road_direction.getText());
            }
            if (road_direction.getText().equals("N") || road_direction.getText().equals("S") ||
                    road_direction.getText().equals("W") || road_direction.getText().equals("E")) {
                break;
            }
        }

        int x_value = Integer.parseInt(road_startpos.getText());
        int y_value = Integer.parseInt(road_endpos.getText());
        int size     = Integer.parseInt(road_size.getText());
        char direction = road_direction.getText().charAt(0);

        if (direction == 'N') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value, y_value - size), new Coordinates(x_value, y_value), 'N'));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        } else if (direction == 'S') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value, y_value), new Coordinates(x_value, y_value + size), 'S'));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        } else if (direction == 'E') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value, y_value), new Coordinates(x_value + size, y_value), 'E'));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        } else if (direction == 'W') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value - size, y_value), new Coordinates(x_value, y_value), 'W'));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        }
    }

    /**
     * Opens a Swing dialog that collects intersection parameters and wires the
     * new intersection to adjacent roads.
     *
     * <p>The dialog accepts an (X, Y) tile coordinate and type string
     * ({@code "three-way"} or {@code "four-way"}).  It then scans all four
     * cardinal neighbours of the supplied position and attaches any road whose
     * start or end point touches that neighbour.
     *
     * <p><b>Current limitation:</b> regardless of the type field, the result is
     * always stored as a {@link Threeway} in {@link #my_threeway_intersections}.
     * Four-way support is not yet implemented here (use the preset layouts for
     * four-way intersections instead).
     */
    public void create_intersection() {
        char[] coords = new char[]{'N', 'S', 'E', 'W'};
        JTextField intersection_xvalue = new JTextField(10);
        JTextField intersection_yvalue = new JTextField(10);
        JTextField type = new JTextField(10);
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Road Start Pos (X-Value)"));
        myPanel.add(intersection_xvalue);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("Road Start Pos (Y-Value)"));
        myPanel.add(intersection_yvalue);
        myPanel.add(new JSeparator());
        myPanel.add(new JLabel("Type"));
        myPanel.add(type);
        myPanel.add(Box.createHorizontalStrut(15));

        // Loop until a valid type is entered or the user cancels
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Please Enter Road Length and Direction", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                System.out.println("Road Start Position(X): " + Integer.parseInt(intersection_xvalue.getText()));
                System.out.println("Road Start Position(Y):  " + Integer.parseInt(intersection_yvalue.getText()));
                System.out.println("Int type: " + type.getText());
            }
            if (type.getText().equals("three-way") || type.getText().equals("four-way")) {
                break;
            }
            if (result == JOptionPane.CANCEL_OPTION) {
                break;
            }
        }

        int x_value = Integer.parseInt(intersection_xvalue.getText());
        int y_value = Integer.parseInt(intersection_yvalue.getText());
        Threeway myintersection = new Threeway();
        Coordinates intersection_pos = new Coordinates(x_value, y_value);

        // Check each of the four neighbouring tiles; if a road endpoint lies
        // there, attach it to the intersection as an exit road
        for (int i = 0; i < coords.length; i++) {
            Coordinates next_pos = Coordinates.add_coordinate(intersection_pos,
                    intersection_pos.direction_to_coords(coords[i]));
            for (Road current_road : this.myroads) {
                if ((current_road.getStart_pos().getX_Pos() == next_pos.getX_Pos() ||
                        current_road.getEnd_pos().getX_Pos() == next_pos.getX_Pos()) &&
                        (current_road.getStart_pos().getY_Pos() == next_pos.getY_Pos() ||
                                current_road.getEnd_pos().getY_Pos() == next_pos.getY_Pos())) {
                    myintersection = new Threeway(new Road(140, intersection_pos, intersection_pos, 'E'), new ArrayList<>());
                    myintersection.getNext_position().add(current_road);
                }
            }
            this.my_threeway_intersections.add(myintersection);
        }
    }

    /**
     * Loads preset layout 1: an asymmetric T-junction near the top of the grid
     * connected to a four-way crossroads further down.
     *
     * <p>Road topology (all coordinates are tile positions):
     * <pre>
     *   Horizontal west arm:  y=2,  x=0→17   (road index 0)
     *   Horizontal east arm:  y=2,  x=19→49  (road index 1)
     *   Vertical south arm:   x=18, y=3→24   (road index 2)
     *   Vertical lower arm:   x=18, y=26→49  (road index 3)
     *   Horizontal east stub: y=25, x=19→49  (road index 4)
     *   Horizontal west stub: y=25, x=0→17   (road index 5)
     * </pre>
     * T-intersection at (18, 2); four-way at (18, 25).
     * Traffic lights: east/west approach green, south/north approach red.
     */
    public void layout1() {
        // Roads feeding the three-way intersection at (18,2)
        this.myroads.add(new Road(1, new Coordinates(0,  2), new Coordinates(17, 2), 'E')); // 0 west arm
        this.myroads.add(new Road(2, new Coordinates(19, 2), new Coordinates(49, 2), 'E')); // 1 east arm
        this.myroads.add(new Road(3, new Coordinates(18, 3), new Coordinates(18, 24), 'S')); // 2 south arm

        // Roads feeding the four-way intersection at (18,25)
        this.myroads.add(new Road(5, new Coordinates(18, 26), new Coordinates(18, 49), 'S')); // 3 lower arm
        this.myroads.add(new Road(6, new Coordinates(19, 25), new Coordinates(49, 25), 'E')); // 4 east stub
        this.myroads.add(new Road(7, new Coordinates(0,  25), new Coordinates(17, 25), 'E')); // 5 west stub

        // Three-way intersection: cars arriving via road 0 (west arm) can exit onto roads 1 or 2
        Threeway intersection3 = new Threeway(
                new Road(250, new Coordinates(18, 2), new Coordinates(18, 2), 'E'),
                new ArrayList<Road>());
        intersection3.getNext_position().add(this.myroads.get(1)); // east
        intersection3.getNext_position().add(this.myroads.get(2)); // south
        this.my_threeway_intersections.add(intersection3);

        // Traffic lights for the three-way intersection
        Coordinates tw = this.my_threeway_intersections.get(0).getInt_road().getStart_pos();
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(tw, new Coordinates(-1, -1)))); // eastbound approach
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(tw, new Coordinates( 1, -1)))); // northbound approach
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(tw, new Coordinates(-1,  1)))); // westbound approach

        // Four-way intersection: exits onto roads 3, 4, 5 (road 2 is the 4th entry and unreachable at runtime)
        Fourway intersection4 = new Fourway(
                new Road(250, new Coordinates(18, 25), new Coordinates(18, 25), 'E'),
                new ArrayList<Road>());
        intersection4.getNext_position().add(this.myroads.get(3)); // lower arm
        intersection4.getNext_position().add(this.myroads.get(4)); // east stub
        intersection4.getNext_position().add(this.myroads.get(5)); // west stub
        intersection4.getNext_position().add(this.myroads.get(2)); // north arm (unreachable — see Fourway note)
        this.my_fourway_intersections.add(intersection4);

        // Traffic lights for the four-way intersection
        Coordinates fw = this.my_fourway_intersections.get(0).getInt_road().getStart_pos();
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(fw, new Coordinates(-1, -1)))); // eastbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(fw, new Coordinates( 1,  1)))); // southbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(fw, new Coordinates(-1,  1)))); // westbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(fw, new Coordinates( 1, -1)))); // northbound
    }

    /**
     * Loads preset layout 2: a single four-way crossroads at the exact centre
     * of the 51 × 51 grid (tile 25, 25).
     *
     * <p>Road topology:
     * <pre>
     *   West arm:   y=25, x=0→24   (index 0)
     *   East arm:   y=25, x=26→49  (index 1)
     *   North arm:  x=25, y=0→24   (index 2)
     *   South arm:  x=25, y=26→49  (index 3)
     * </pre>
     * One four-way intersection at (25, 25).
     * Traffic lights: east/north approach green, south/west approach red.
     */
    public void layout2() {
        this.myroads.add(new Road(1, new Coordinates(0,  25), new Coordinates(24, 25), 'E')); // 0 west arm
        this.myroads.add(new Road(2, new Coordinates(26, 25), new Coordinates(49, 25), 'E')); // 1 east arm
        this.myroads.add(new Road(3, new Coordinates(25,  0), new Coordinates(25, 24), 'S')); // 2 north arm
        this.myroads.add(new Road(4, new Coordinates(25, 26), new Coordinates(25, 49), 'S')); // 3 south arm

        Fourway intersection = new Fourway(
                new Road(250, new Coordinates(25, 25), new Coordinates(25, 25), 'E'),
                new ArrayList<Road>());
        intersection.getNext_position().add(this.myroads.get(1)); // east
        intersection.getNext_position().add(this.myroads.get(3)); // south
        intersection.getNext_position().add(this.myroads.get(0)); // west
        intersection.getNext_position().add(this.myroads.get(2)); // north (unreachable — see Fourway note)
        this.my_fourway_intersections.add(intersection);

        Coordinates c = this.my_fourway_intersections.get(0).getInt_road().getStart_pos();
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(c, new Coordinates(-1, -1)))); // eastbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(c, new Coordinates( 1,  1)))); // southbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(c, new Coordinates(-1,  1)))); // westbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(c, new Coordinates( 1, -1)))); // northbound
    }

    /**
     * Loads preset layout 3: an H-shaped road network with two vertical roads
     * (at x=15 and x=35) joined by a horizontal connector at y=25, giving two
     * four-way intersections.
     *
     * <p>Road topology:
     * <pre>
     *   Left north arm:   x=15, y=0→24   (index 0)
     *   Left south arm:   x=15, y=26→49  (index 1)
     *   Right north arm:  x=35, y=0→24   (index 2)
     *   Right south arm:  x=35, y=26→49  (index 3)
     *   West stub:        y=25, x=0→14   (index 4)
     *   Middle connector: y=25, x=16→34  (index 5)
     *   East stub:        y=25, x=36→49  (index 6)
     * </pre>
     * Left four-way at (15, 25); right four-way at (35, 25).
     * Both intersections: east/north approach green, south/west approach red.
     */
    public void layout3() {
        this.myroads.add(new Road(1, new Coordinates(15,  0), new Coordinates(15, 24), 'S')); // 0 left north arm
        this.myroads.add(new Road(2, new Coordinates(15, 26), new Coordinates(15, 49), 'S')); // 1 left south arm
        this.myroads.add(new Road(3, new Coordinates(35,  0), new Coordinates(35, 24), 'S')); // 2 right north arm
        this.myroads.add(new Road(4, new Coordinates(35, 26), new Coordinates(35, 49), 'S')); // 3 right south arm
        this.myroads.add(new Road(5, new Coordinates( 0, 25), new Coordinates(14, 25), 'E')); // 4 west stub
        this.myroads.add(new Road(6, new Coordinates(16, 25), new Coordinates(34, 25), 'E')); // 5 middle connector
        this.myroads.add(new Road(7, new Coordinates(36, 25), new Coordinates(49, 25), 'E')); // 6 east stub

        // Left intersection — connects the left vertical road to the horizontal connector
        Fourway left4 = new Fourway(
                new Road(251, new Coordinates(15, 25), new Coordinates(15, 25), 'E'),
                new ArrayList<Road>());
        left4.getNext_position().add(this.myroads.get(0)); // north arm
        left4.getNext_position().add(this.myroads.get(1)); // south arm
        left4.getNext_position().add(this.myroads.get(4)); // west stub
        left4.getNext_position().add(this.myroads.get(5)); // middle connector (unreachable)
        this.my_fourway_intersections.add(left4);

        // Right intersection — connects the right vertical road to the horizontal connector
        Fourway right4 = new Fourway(
                new Road(252, new Coordinates(35, 25), new Coordinates(35, 25), 'E'),
                new ArrayList<Road>());
        right4.getNext_position().add(this.myroads.get(2)); // north arm
        right4.getNext_position().add(this.myroads.get(3)); // south arm
        right4.getNext_position().add(this.myroads.get(5)); // middle connector
        right4.getNext_position().add(this.myroads.get(6)); // east stub (unreachable)
        this.my_fourway_intersections.add(right4);

        // Traffic lights — left intersection (index 0)
        Coordinates cl = this.my_fourway_intersections.get(0).getInt_road().getStart_pos();
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(cl, new Coordinates(-1, -1))));
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(cl, new Coordinates( 1,  1))));
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(cl, new Coordinates(-1,  1))));
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(cl, new Coordinates( 1, -1))));

        // Traffic lights — right intersection (index 1)
        Coordinates cr = this.my_fourway_intersections.get(1).getInt_road().getStart_pos();
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(cr, new Coordinates(-1, -1))));
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(cr, new Coordinates( 1,  1))));
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(cr, new Coordinates(-1,  1))));
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(cr, new Coordinates( 1, -1))));
    }

    /**
     * Removes all roads, cars, intersections, and traffic lights from the grid.
     *
     * <p>Call this before loading a new preset layout so that the object lists
     * start from index 0.  The preset methods ({@link #layout1()}, etc.) rely on
     * list indices when wiring intersection exit roads, so leftover entries from
     * a previous layout would shift all indices and corrupt the routing graph.
     */
    public void clearLayout() {
        this.myroads.clear();
        this.mycars.clear();
        this.my_straight_intersections.clear();
        this.my_threeway_intersections.clear();
        this.my_fourway_intersections.clear();
        this.mytrafficlights.clear();
    }
}


/**
 * Application entry point.
 *
 * <p>Creates the 51 × 51 {@link Grid}, initialises all object lists, draws
 * the initial (empty) grid state, then hands control to {@link backup} which
 * builds and displays the Swing UI.
 *
 * <p>No roads or cars are placed at startup — the user selects a preset layout
 * or draws roads manually via the City Mode editor before starting the
 * simulation.
 */
public class Main {

    /**
     * Launches the traffic simulator.
     *
     * @param args command-line arguments (unused)
     * @throws IOException if any I/O error occurs during startup (currently
     *                     none are expected; declared for forward compatibility)
     */
    public static void main(String[] args) throws IOException {

        // Create the simulation grid (51×51 tiles) and initialise all entity lists
        Grid the_grid = new Grid(51, 51);
        the_grid.myroads                  = new ArrayList<>();
        the_grid.mycars                   = new ArrayList<>();
        the_grid.my_straight_intersections = new ArrayList<>();
        the_grid.mytrafficlights          = new ArrayList<>();
        the_grid.my_threeway_intersections = new ArrayList<>();
        the_grid.my_fourway_intersections  = new ArrayList<>();

        // The loop below is effectively a no-op at startup because myroads is
        // empty — car spawning happens later in backup.autogeneratecars()
        for (int i = 0; i < the_grid.myroads.size(); i++) {
            Random mynumber = new Random();
            int myvalue = mynumber.nextInt(the_grid.myroads.size());
            if (the_grid.myroads.get(myvalue).getDirection() == 'E') {
                the_grid.mycars.add(new Car(i, the_grid.myroads.get(myvalue).getStart_pos(),
                        the_grid.myroads.get(myvalue).getDirection()));
                the_grid.mycars.get(i).Move();
            } else {
                the_grid.mycars.add(new Car(i, the_grid.myroads.get(myvalue).getEnd_pos(),
                        the_grid.myroads.get(myvalue).getDirection()));
                the_grid.mycars.get(i).Move();
            }
        }

        // Render the empty grid so the backing arrays are initialised before the UI starts
        the_grid.drawGrid();

        // Build and show the Swing window
        backup myframe = new backup(the_grid);
        myframe.loadscreen();
        myframe.validate();
    }
}
