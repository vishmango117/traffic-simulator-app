package trafficsimulator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists and retrieves custom traffic-simulation layouts to/from a plain-text
 * data file ({@value #LAYOUTS_FILE}).
 *
 * <h2>File format</h2>
 * The file is a sequence of named sections, each beginning with a header line
 * of the form {@code [Layout Name]} followed by one record per line:
 *
 * <pre>
 * [My Layout]
 * ROAD,startX,startY,endX,endY,direction
 * STRAIGHT,intX,intY,exitRoadIndex
 * THREEWAY,intX,intY,exitRoadIndex0,exitRoadIndex1
 * FOURWAY,intX,intY,exitRoadIndex0,exitRoadIndex1,exitRoadIndex2,exitRoadIndex3
 * TRAFFICLIGHT,color,posX,posY
 * </pre>
 *
 * <p>Road records are written in list order; intersection records reference exit
 * roads by their 0-based index in that same order.  Traffic light colours are
 * {@code G} (green) or {@code R} (red).
 *
 * <h2>Name restrictions</h2>
 * The names {@code "Layout 1"}, {@code "Layout 2"}, and {@code "Layout 3"} are
 * reserved for the built-in presets and cannot be saved to the file.
 */
public class LayoutManager {

    /**
     * Path to the layouts data file, resolved relative to the JVM working
     * directory (the project root when launched via {@code mvn javafx:run}).
     */
    public static final String LAYOUTS_FILE = "layouts.dat";

    /**
     * Returns the names of every custom layout stored in the data file.
     * Built-in preset names ("Layout 1", "Layout 2", "Layout 3") are never
     * written to the file and will not appear in this list.
     *
     * @return ordered list of saved layout names; empty if the file does not
     *         exist or contains no layouts
     */
    public static List<String> getCustomLayoutNames() {
        List<String> names = new ArrayList<>();
        File file = new File(LAYOUTS_FILE);
        if (!file.exists()) return names;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    names.add(line.substring(1, line.length() - 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * Serialises the current state of {@code grid} as a named layout and
     * appends it to the data file.  If a layout with the same name already
     * exists in the file it is replaced in-place.
     *
     * <p>The following grid state is saved:
     * <ul>
     *   <li>All {@link Road} segments in {@link Grid#myroads}.</li>
     *   <li>All {@link Straight} intersections — exit road referenced by list
     *       index.</li>
     *   <li>All {@link Threeway} intersections — each exit road referenced by
     *       list index.</li>
     *   <li>All {@link Fourway} intersections — each exit road referenced by
     *       list index.</li>
     *   <li>All {@link TrafficLight} objects — colour and tile position.</li>
     * </ul>
     *
     * <p>Cars are not saved; they are regenerated when the simulation starts.
     *
     * @param name the layout name (used as the section header in the file);
     *             must not be one of the reserved preset names
     * @param grid the grid whose state should be persisted
     * @throws IOException              if the file cannot be written
     * @throws IllegalArgumentException if {@code name} is a reserved preset name
     */
    public static void saveLayout(String name, Grid grid) throws IOException {
        if (name.equals("Layout 1") || name.equals("Layout 2") || name.equals("Layout 3")) {
            throw new IllegalArgumentException("\"" + name + "\" is a reserved preset name.");
        }

        // Read the existing file, removing any section that matches the name
        // so that a re-save acts as an update rather than a duplicate append
        StringBuilder retained = new StringBuilder();
        File file = new File(LAYOUTS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                boolean skipping = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.equals("[" + name + "]")) {
                        skipping = true; // start skipping the old version of this layout
                    } else if (skipping && trimmed.startsWith("[") && trimmed.endsWith("]")) {
                        skipping = false; // reached the next layout section — stop skipping
                    }
                    if (!skipping) {
                        retained.append(line).append(System.lineSeparator());
                    }
                }
            }
        }

        // Write retained content plus the new layout section
        try (PrintWriter writer = new PrintWriter(new FileWriter(LAYOUTS_FILE))) {
            writer.print(retained);
            writer.println("[" + name + "]");

            // Roads — recorded in list order; indices are implicit (0, 1, 2 …)
            for (Road road : grid.myroads) {
                writer.printf("ROAD,%d,%d,%d,%d,%c%n",
                        road.getStart_pos().getX_Pos(),
                        road.getStart_pos().getY_Pos(),
                        road.getEnd_pos().getX_Pos(),
                        road.getEnd_pos().getY_Pos(),
                        road.getDirection());
            }

            // Straight intersections — single exit road by index
            for (Straight st : grid.my_straight_intersections) {
                int exitIdx = grid.myroads.indexOf(st.getNext_road());
                writer.printf("STRAIGHT,%d,%d,%d%n",
                        st.getInt_road().getStart_pos().getX_Pos(),
                        st.getInt_road().getStart_pos().getY_Pos(),
                        exitIdx);
            }

            // Three-way intersections — two exit roads by index
            for (Threeway tw : grid.my_threeway_intersections) {
                writer.printf("THREEWAY,%d,%d",
                        tw.getInt_road().getStart_pos().getX_Pos(),
                        tw.getInt_road().getStart_pos().getY_Pos());
                for (Road exit : tw.getNext_position()) {
                    writer.print("," + grid.myroads.indexOf(exit));
                }
                writer.println();
            }

            // Four-way intersections — up to four exit roads by index
            for (Fourway fw : grid.my_fourway_intersections) {
                writer.printf("FOURWAY,%d,%d",
                        fw.getInt_road().getStart_pos().getX_Pos(),
                        fw.getInt_road().getStart_pos().getY_Pos());
                for (Road exit : fw.getNext_position()) {
                    writer.print("," + grid.myroads.indexOf(exit));
                }
                writer.println();
            }

            // Traffic lights — colour and position
            for (TrafficLight tl : grid.mytrafficlights) {
                writer.printf("TRAFFICLIGHT,%s,%d,%d%n",
                        tl.getColor(),
                        tl.getPosition().getX_Pos(),
                        tl.getPosition().getY_Pos());
            }
        }
    }

    /**
     * Reads the named layout from the data file and applies it to {@code grid}.
     *
     * <p>The grid is cleared via {@link Grid#clearLayout()} before any records
     * are applied, so any roads or intersections present beforehand are
     * discarded.
     *
     * <p>Intersection records resolve exit roads by the 0-based index recorded
     * in the file; an index that is out of range for the loaded road list is
     * silently ignored.
     *
     * @param name the layout name to load (must exactly match a section header)
     * @param grid the grid to populate with the loaded layout
     * @return {@code true} if the layout was found and applied; {@code false} if
     *         the file does not exist or contains no section with the given name
     */
    public static boolean loadLayout(String name, Grid grid) {
        File file = new File(LAYOUTS_FILE);
        if (!file.exists()) return false;

        grid.clearLayout();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean inSection = false;
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Section header detection
                if (trimmed.equals("[" + name + "]")) {
                    inSection = true;
                    found = true;
                    continue;
                }
                // Next section starts — stop reading
                if (inSection && trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    break;
                }
                if (!inSection || trimmed.isEmpty()) continue;

                String[] parts = trimmed.split(",");
                switch (parts[0]) {
                    case "ROAD": {
                        int startX = Integer.parseInt(parts[1]);
                        int startY = Integer.parseInt(parts[2]);
                        int endX   = Integer.parseInt(parts[3]);
                        int endY   = Integer.parseInt(parts[4]);
                        char dir   = parts[5].charAt(0);
                        grid.myroads.add(new Road(
                                grid.myroads.size() + 1,
                                new Coordinates(startX, startY),
                                new Coordinates(endX, endY),
                                dir));
                        break;
                    }
                    case "STRAIGHT": {
                        int x      = Integer.parseInt(parts[1]);
                        int y      = Integer.parseInt(parts[2]);
                        int exitIdx = Integer.parseInt(parts[3]);
                        if (exitIdx >= 0 && exitIdx < grid.myroads.size()) {
                            grid.my_straight_intersections.add(new Straight(
                                    new Road(250, new Coordinates(x, y), new Coordinates(x, y), 'E'),
                                    grid.myroads.get(exitIdx)));
                        }
                        break;
                    }
                    case "THREEWAY": {
                        int x  = Integer.parseInt(parts[1]);
                        int y  = Integer.parseInt(parts[2]);
                        Threeway tw = new Threeway(
                                new Road(250, new Coordinates(x, y), new Coordinates(x, y), 'E'),
                                new ArrayList<>());
                        for (int i = 3; i < parts.length; i++) {
                            int idx = Integer.parseInt(parts[i]);
                            if (idx >= 0 && idx < grid.myroads.size()) {
                                tw.getNext_position().add(grid.myroads.get(idx));
                            }
                        }
                        grid.my_threeway_intersections.add(tw);
                        break;
                    }
                    case "FOURWAY": {
                        int x  = Integer.parseInt(parts[1]);
                        int y  = Integer.parseInt(parts[2]);
                        Fourway fw = new Fourway(
                                new Road(250, new Coordinates(x, y), new Coordinates(x, y), 'E'),
                                new ArrayList<>());
                        for (int i = 3; i < parts.length; i++) {
                            int idx = Integer.parseInt(parts[i]);
                            if (idx >= 0 && idx < grid.myroads.size()) {
                                fw.getNext_position().add(grid.myroads.get(idx));
                            }
                        }
                        grid.my_fourway_intersections.add(fw);
                        break;
                    }
                    case "TRAFFICLIGHT": {
                        String color = parts[1];
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        grid.mytrafficlights.add(new TrafficLight(color, new Coordinates(x, y)));
                        break;
                    }
                    default:
                        break; // unknown record type — skip silently
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return found;
    }

    /**
     * Deletes the named layout from the data file.
     * Reserved preset names are silently ignored.
     *
     * @param name the layout name to remove
     */
    public static void deleteLayout(String name) {
        File file = new File(LAYOUTS_FILE);
        if (!file.exists()) return;

        StringBuilder retained = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean skipping = false;
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.equals("[" + name + "]")) {
                    skipping = true;
                } else if (skipping && trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    skipping = false;
                }
                if (!skipping) {
                    retained.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(LAYOUTS_FILE))) {
            writer.print(retained);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
