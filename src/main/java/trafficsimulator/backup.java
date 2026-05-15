package trafficsimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Random;

/**
 * Swing panel that renders the 51 × 51 simulation grid.
 *
 * <p>The panel repaints by reading directly from {@link Grid#mygrid_roads} and
 * {@link Grid#mygrid_vehicles}, which are rebuilt each tick by
 * {@link Grid#drawGrid()}.  Each tile is drawn as a 15 × 15 pixel square, so
 * the full grid occupies 765 × 765 pixels.
 *
 * <p>Tile rendering priority (later items paint over earlier ones):
 * <ol>
 *   <li>Green background (empty ground).</li>
 *   <li>{@code "RD1"} → dark grey road tile.</li>
 *   <li>{@code "TW1"} / {@code "FW1"} → white intersection tile.</li>
 *   <li>{@code "TL1"} → green or red traffic-light tile (colour looked up from
 *       the matching {@link TrafficLight} object).</li>
 *   <li>{@code "CR1"} → cyan car tile.</li>
 *   <li>{@link #highlightTile} → yellow/orange overlay for road-drawing preview.</li>
 * </ol>
 *
 * <p><b>Coordinate mapping:</b> the outer loop variable {@code i} iterates over
 * columns (X), the inner variable {@code j} over rows (Y).  The backing arrays
 * are accessed as {@code mygrid_roads[j][i]} = {@code mygrid_roads[Y][X]}.
 * Screen pixels for tile (X, Y) are at {@code (X*15, Y*15)}.
 */
class mygrid extends JPanel {

    private Grid the_grid;

    /**
     * When non-null, this tile is highlighted in yellow/orange to show the user
     * where they clicked the first road endpoint in draw-road mode.
     * Set to {@code null} to remove the highlight.
     */
    Coordinates highlightTile = null;

    /**
     * Creates a grid panel bound to the given simulation state.
     *
     * @param the_grid the grid whose tile arrays this panel will render
     */
    public mygrid(Grid the_grid) {
        this.the_grid = the_grid;
    }

    /**
     * Renders the current state of both tile layers onto this panel.
     * Called automatically by Swing on every repaint request.
     *
     * @param g the graphics context provided by the Swing repaint cycle
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(34, 139, 34)); // forest green for empty ground

        // --- Road and intersection layer ---
        // i = column (X), j = row (Y); access array as [Y][X] = [j][i]
        for (int i = 0; i < the_grid.mygrid_roads.length; i++) {
            for (int j = 0; j < the_grid.mygrid_roads[i].length; j++) {
                String cell = the_grid.mygrid_roads[j][i];
                if (cell.equals("RD1")) {
                    g.setColor(new Color(80, 80, 80)); // asphalt grey
                    g.fillRect(i * 15, j * 15, 15, 15);
                } else if (cell.equals("TW1") || cell.equals("FW1")) {
                    g.setColor(Color.WHITE); // intersection crosshatch
                    g.fillRect(i * 15, j * 15, 15, 15);
                }
            }
        }

        // --- Vehicle and traffic-light layer ---
        for (int i = 0; i < the_grid.mygrid_vehicles.length; i++) {
            for (int j = 0; j < the_grid.mygrid_vehicles[i].length; j++) {
                if (the_grid.mygrid_roads[j][i].equals("TL1")) {
                    // Find the TrafficLight object at this tile to read its current colour
                    Color tlColor = Color.RED;
                    for (TrafficLight tl : the_grid.mytrafficlights) {
                        if (tl.getPosition().getY_Pos() == j && tl.getPosition().getX_Pos() == i) {
                            tlColor = tl.getColor().equals("G") ? Color.GREEN : Color.RED;
                        }
                    }
                    g.setColor(tlColor);
                    g.fillRect(i * 15, j * 15, 15, 15);
                } else if (the_grid.mygrid_vehicles[j][i].equals("CR1")) {
                    g.setColor(Color.CYAN);
                    g.fillRect(i * 15, j * 15, 15, 15);
                }
            }
        }

        // --- Road-draw start point highlight ---
        // Semi-transparent yellow fill + solid orange border to indicate the
        // tile the user clicked as the first endpoint of a new road
        if (highlightTile != null) {
            int hx = highlightTile.getX_Pos() * 15;
            int hy = highlightTile.getY_Pos() * 15;
            g.setColor(new Color(255, 200, 0, 180));
            g.fillRect(hx, hy, 15, 15);
            g.setColor(Color.ORANGE);
            g.drawRect(hx, hy, 14, 14);
        }
    }
}


/**
 * Main application window and simulation controller.
 *
 * <p>The window is divided into three areas using {@link BorderLayout}:
 * <ul>
 *   <li><b>Top bar</b> ({@code PAGE_START}): "City Mode" / "Simulator" toggle
 *       buttons that switch both the active grid view and the side panel.</li>
 *   <li><b>Centre</b>: a {@link CardLayout} holding two {@link mygrid} panels —
 *       {@code "simulation_grid"} (read-only, used during simulation) and
 *       {@code "editor_grid"} (interactive, used in City Mode for road
 *       drawing).</li>
 *   <li><b>Left side</b> ({@code LINE_START}): a {@link CardLayout} holding
 *       two control panels:
 *       <ul>
 *         <li>{@code "sim_controls"} — Start / Stop buttons for the simulation
 *             loop.</li>
 *         <li>{@code "editor_controls"} — Layout 1/2/3 preset buttons, Add Road
 *             toggle, and Add Intersection dialog launcher.</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h2>Simulation loop</h2>
 * {@link #process_run(JFrame)} spawns a background thread that ticks every
 * 500 ms: it rebuilds the grid arrays, advances all cars, and repaints.
 * Every five ticks (≈ 2.5 s) all traffic lights are toggled.
 * The loop is stopped cleanly by {@link #stop_sim()} via a volatile flag.
 *
 * <h2>Road drawing (City Mode)</h2>
 * Clicking "Add Road" toggles draw-road mode.  The user then clicks two tiles
 * on the editor grid:
 * <ul>
 *   <li>First click: records the start tile and highlights it in yellow.</li>
 *   <li>Second click: validates the end tile.
 *       <ul>
 *         <li>Same Y as start → horizontal road ({@code 'E'}).</li>
 *         <li>Same X as start → vertical road ({@code 'S'}).</li>
 *         <li>Different X <em>and</em> Y → diagonal; rejected with an error
 *             message and the selection is reset.</li>
 *       </ul>
 *   </li>
 * </ul>
 */
public class backup extends JFrame {

    /** The simulation grid shared between the controller and the render panels. */
    public Grid the_grid;

    /**
     * Tick counter incremented every 500 ms by the simulation thread.
     * Traffic lights are toggled when this value is a multiple of 5.
     */
    public int counter = 0;

    /**
     * Controls the simulation loop.  Set to {@code true} by
     * {@link #process_run(JFrame)} when the simulation starts, and to
     * {@code false} by {@link #stop_sim()} to signal the thread to exit its
     * loop cleanly.  Declared {@code volatile} so the write in the EDT is
     * immediately visible to the simulation thread.
     */
    private volatile boolean simRunning = false;

    /**
     * Reference to the simulation background thread.
     * Held so {@link #stop_sim()} can interrupt it if it is blocked in
     * {@link Thread#sleep}.
     */
    private Thread simThread = null;

    /**
     * When {@code true}, clicks on the editor grid are interpreted as road
     * endpoints rather than plain selection.  Toggled by the "Add Road" button.
     */
    private boolean drawRoadMode = false;

    /**
     * The tile coordinate of the first endpoint clicked when in draw-road mode.
     * {@code null} means no start point has been selected yet (either the mode
     * just became active or the previous road was just committed).
     */
    private Coordinates roadStart = null;

    /**
     * Status label displayed below the editor controls.  Updated with
     * instruction prompts, success confirmations, and error messages as the
     * user draws roads.
     */
    private JLabel statusLabel;

    /**
     * The interactive grid panel shown in City Mode.  Stored as a field so the
     * mouse listener in {@link #loadscreen()} and the road-drawing logic in
     * {@link #handleEditorClick} can share access to it.
     */
    private mygrid editorGrid;

    /**
     * Dropdown in the editor panel listing all available layouts (built-in
     * presets first, then any custom layouts saved to disk).  Stored as a field
     * so it can be refreshed after a new layout is saved without rebuilding the
     * entire UI.
     */
    private JComboBox<String> layoutDropdown;

    /**
     * Creates the window controller bound to the given simulation grid.
     *
     * @param the_grid the central simulation state object
     * @throws HeadlessException if the environment does not support a display
     */
    public backup(Grid the_grid) throws HeadlessException {
        this.the_grid = the_grid;
    }

    /**
     * Starts the simulation loop on a dedicated background thread.
     *
     * <p>Each tick (500 ms):
     * <ol>
     *   <li>Rebuilds both tile arrays via {@link Grid#drawGrid()}.</li>
     *   <li>Advances every car one tile via {@link Grid#updateCarPos}.</li>
     *   <li>Repaints the window.</li>
     *   <li>If the tick counter is a multiple of 5, toggles all traffic lights.</li>
     * </ol>
     *
     * <p>The loop exits when {@link #simRunning} is set to {@code false} by
     * {@link #stop_sim()}.  On interrupt the thread restores the interrupted
     * flag and exits the loop naturally on the next iteration.
     *
     * @param myframe the window to repaint on each tick
     */
    public void process_run(JFrame myframe) {
        simRunning = true;
        counter = 0;
        simThread = new Thread(() -> {
            while (simRunning) {
                try {
                    Thread.sleep(500);
                    this.the_grid.drawGrid();
                    this.the_grid.updateCarPos(the_grid.mycars);
                    myframe.validate();
                    myframe.repaint();
                    counter += 1;
                    // Toggle all traffic lights every 5 ticks (≈ 2.5 seconds)
                    if (counter % 5 == 0) {
                        for (TrafficLight tl : the_grid.mytrafficlights) {
                            tl.operate();
                        }
                    }
                } catch (InterruptedException e) {
                    // Restore interrupted status so the while-condition re-evaluates
                    Thread.currentThread().interrupt();
                }
            }
        });
        simThread.start();
    }

    /**
     * Stops the simulation loop.
     *
     * <p>Sets {@link #simRunning} to {@code false} so the loop condition fails
     * on the next iteration, then interrupts the thread if it is currently
     * sleeping in {@link Thread#sleep} to avoid waiting up to 500 ms for the
     * next natural wake-up.
     */
    public void stop_sim() {
        simRunning = false;
        if (simThread != null) simThread.interrupt();
    }

    /**
     * Spawns one car per road segment at the start of the simulation.
     *
     * <p>For each road, a random road is chosen from the full list and a car is
     * placed at:
     * <ul>
     *   <li>{@code start_pos} for eastbound ({@code 'E'}) roads, so it heads
     *       toward the road's end.</li>
     *   <li>{@code end_pos} for all other directions, so it heads back toward
     *       the road's start.</li>
     * </ul>
     *
     * <p>Each new car's speed is immediately set to 1 via {@link Car#Move()}.
     * Does nothing if there are no roads loaded.
     */
    public void autogeneratecars() {
        if (the_grid.myroads.isEmpty()) return;
        for (int i = 0; i < the_grid.myroads.size(); i++) {
            int myvalue = new Random().nextInt(the_grid.myroads.size());
            Road road = the_grid.myroads.get(myvalue);
            // Eastbound cars start at the western end; all others start at the eastern/southern end
            Coordinates pos = road.getDirection() == 'E' ? road.getStart_pos() : road.getEnd_pos();
            Car car = new Car(i, pos, road.getDirection());
            car.Move();
            the_grid.mycars.add(car);
        }
    }

    /**
     * Clears the current layout, loads a new preset, redraws the tile arrays,
     * and repaints the window.
     *
     * <p>Clearing before loading is essential because the preset methods use
     * hard-coded list indices (e.g., {@code myroads.get(2)}) which would be
     * wrong if roads from a previous layout were still present.
     *
     * @param layoutLoader a no-arg method reference such as
     *                     {@code the_grid::layout1}
     * @param myframe      the window to repaint after loading
     */
    private void loadPreset(Runnable layoutLoader, JFrame myframe) {
        the_grid.clearLayout();
        layoutLoader.run();
        the_grid.drawGrid();
        myframe.repaint();
    }

    /**
     * Repopulates {@link #layoutDropdown} with the three built-in preset names
     * followed by every custom layout name read from the data file.
     *
     * <p>The currently selected item is preserved if it still exists in the
     * refreshed list; otherwise the selection defaults to the first item.
     */
    private void refreshLayoutDropdown() {
        String current = (String) layoutDropdown.getSelectedItem();
        layoutDropdown.removeAllItems();
        layoutDropdown.addItem("Layout 1");
        layoutDropdown.addItem("Layout 2");
        layoutDropdown.addItem("Layout 3");
        for (String name : LayoutManager.getCustomLayoutNames()) {
            layoutDropdown.addItem(name);
        }
        if (current != null) {
            layoutDropdown.setSelectedItem(current);
        }
    }

    /**
     * Handles a mouse click on the editor grid during road-draw mode.
     *
     * <p>State machine:
     * <ul>
     *   <li><b>No start point yet</b> ({@link #roadStart} is {@code null}):
     *       record the clicked tile as the start, highlight it, update the
     *       status label.</li>
     *   <li><b>Start already set</b>: validate the end tile:
     *       <ul>
     *         <li>Same tile as start → prompt to pick a different end point.</li>
     *         <li>Different X <em>and</em> Y → diagonal; show error, reset.</li>
     *         <li>Same Y → create a horizontal {@code 'E'} road from
     *             min(X) to max(X).</li>
     *         <li>Same X → create a vertical {@code 'S'} road from
     *             min(Y) to max(Y).</li>
     *       </ul>
     *       On success the road is appended to {@link Grid#myroads}, the grid
     *       is redrawn, and the state resets for the next road.</li>
     * </ul>
     *
     * <p>Does nothing if {@link #drawRoadMode} is {@code false}.
     *
     * @param pixelX    x pixel coordinate of the mouse click within the panel
     * @param pixelY    y pixel coordinate of the mouse click within the panel
     * @param addRoadBtn the "Add Road" button (unused in logic, kept for
     *                   potential future label updates from this method)
     */
    private void handleEditorClick(int pixelX, int pixelY, JButton addRoadBtn) {
        if (!drawRoadMode) return;

        // Convert pixel position to tile coordinate (each tile is 15×15 px)
        int tileX = pixelX / 15;
        int tileY = pixelY / 15;

        // Clamp to valid grid bounds (0–50 inclusive for a 51×51 grid)
        if (tileX < 0 || tileX > 50 || tileY < 0 || tileY > 50) return;

        if (roadStart == null) {
            // First click — record start tile and show highlight
            roadStart = new Coordinates(tileX, tileY);
            editorGrid.highlightTile = roadStart;
            editorGrid.repaint();
            statusLabel.setText("<html>Start: (" + tileX + ", " + tileY + ")<br>Click end point</html>");
        } else {
            // Second click — validate and create the road
            int sx = roadStart.getX_Pos(), sy = roadStart.getY_Pos();
            int ex = tileX,                ey = tileY;

            if (sx == ex && sy == ey) {
                statusLabel.setText("<html>Same tile — pick<br>a different end</html>");
                return;
            }

            if (sx != ex && sy != ey) {
                // Diagonal — reject
                statusLabel.setText("<html><font color='red'>Diagonal roads not<br>allowed. Try again.</font></html>");
                roadStart = null;
                editorGrid.highlightTile = null;
                editorGrid.repaint();
                return;
            }

            Road newRoad;
            int roadNo = the_grid.myroads.size() + 1;

            if (sy == ey) {
                // Horizontal road: normalise so start.X < end.X
                int minX = Math.min(sx, ex), maxX = Math.max(sx, ex);
                newRoad = new Road(roadNo, new Coordinates(minX, sy), new Coordinates(maxX, sy), 'E');
            } else {
                // Vertical road: normalise so start.Y < end.Y
                int minY = Math.min(sy, ey), maxY = Math.max(sy, ey);
                newRoad = new Road(roadNo, new Coordinates(sx, minY), new Coordinates(sx, maxY), 'S');
            }

            the_grid.myroads.add(newRoad);
            the_grid.drawGrid();
            editorGrid.repaint();

            // Reset for the next road
            roadStart = null;
            editorGrid.highlightTile = null;
            statusLabel.setText("<html>Road #" + roadNo + " added.<br>Click start point</html>");
        }
    }

    /**
     * Builds and displays the main application window.
     *
     * <p>Layout overview:
     * <pre>
     * ┌─────────────────────────────────────────────────────────┐
     * │  [City Mode]  [Simulator]          ← top bar            │
     * ├──────────────┬──────────────────────────────────────────┤
     * │ [Layout ▼  ] │                                          │
     * │ [   Load   ] │         Grid panel (CardLayout)          │
     * │ [Save Layout]│   simulation_grid ↔ editor_grid          │
     * │ [ Add Road ] │                                          │
     * │ [ Add Int. ] │                                          │
     * │   [status]   │                                          │
     * └──────────────┴──────────────────────────────────────────┘
     * </pre>
     *
     * <p>The centre grid and left side panel each use a {@link CardLayout} so
     * that "City Mode" and "Simulator" can swap both panels simultaneously
     * with a single button click.
     *
     * <p>The layout dropdown lists the three built-in presets followed by any
     * custom layouts saved to {@value LayoutManager#LAYOUTS_FILE}.  "Load"
     * applies the selected layout; "Save Layout" prompts for a name and writes
     * the current grid state to the same file.
     *
     * <p>Mouse clicks on the editor grid are forwarded to
     * {@link #handleEditorClick} to support the interactive road-drawing
     * workflow.
     */
    public void loadscreen() {
        JFrame myframe = new JFrame("Traffic Car Simulator");
        myframe.setLayout(new BorderLayout());
        myframe.setSize(1366, 768);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---- Grid panels ----

        // Simulation view — read-only rendering of the running simulation
        mygrid simGrid = new mygrid(the_grid);
        simGrid.setPreferredSize(new Dimension(700, 700));

        // Editor view — interactive grid for road drawing in City Mode
        editorGrid = new mygrid(the_grid);
        editorGrid.setPreferredSize(new Dimension(700, 700));

        // Outer CardLayout swaps between the two grid views
        JPanel gridCardPanel = new JPanel();
        CardLayout gridCard = new CardLayout();
        gridCardPanel.setLayout(gridCard);
        gridCardPanel.setPreferredSize(new Dimension(700, 700));
        gridCardPanel.add(simGrid,    "simulation_grid");
        gridCardPanel.add(editorGrid, "editor_grid");
        myframe.add(gridCardPanel, BorderLayout.CENTER);

        // ---- Top mode-toggle bar ----
        JPanel modepanel = new JPanel();
        JButton cityModeBtn  = new JButton("City Mode");
        JButton simulatorBtn = new JButton("Simulator");
        modepanel.add(cityModeBtn);
        modepanel.add(simulatorBtn);
        myframe.add(modepanel, BorderLayout.PAGE_START);

        // ---- Side panels ----
        JPanel sidepanel = new JPanel();
        CardLayout sideCard = new CardLayout();
        sidepanel.setLayout(sideCard);
        sidepanel.setPreferredSize(new Dimension(165, 600));

        // Simulator control panel (Start / Stop)
        JPanel simControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        JButton startBtn = new JButton("Start");
        JButton stopBtn  = new JButton("Stop");
        startBtn.setPreferredSize(new Dimension(130, 55));
        stopBtn.setPreferredSize(new Dimension(130, 55));
        simControls.add(startBtn);
        simControls.add(stopBtn);

        // Editor control panel (layout dropdown, road/intersection tools, status)
        JPanel editorControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));

        // Layout selector — presets at the top, custom saved layouts below
        JLabel layoutLabel = new JLabel("Select Layout:");
        layoutLabel.setPreferredSize(new Dimension(140, 20));
        layoutDropdown = new JComboBox<>();
        layoutDropdown.setPreferredSize(new Dimension(140, 28));
        refreshLayoutDropdown();

        JButton loadLayoutBtn = new JButton("Load");
        JButton saveLayoutBtn = new JButton("Save Layout");
        JButton addRoadBtn    = new JButton("Add Road");
        JButton addIntBtn     = new JButton("Add Intersection");
        statusLabel = new JLabel("<html><center>Select a layout<br>or draw roads</center></html>");

        for (JButton b : new JButton[]{loadLayoutBtn, saveLayoutBtn, addRoadBtn, addIntBtn}) {
            b.setPreferredSize(new Dimension(140, 45));
        }
        statusLabel.setPreferredSize(new Dimension(140, 50));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        editorControls.add(layoutLabel);
        editorControls.add(layoutDropdown);
        editorControls.add(loadLayoutBtn);
        editorControls.add(saveLayoutBtn);
        editorControls.add(addRoadBtn);
        editorControls.add(addIntBtn);
        editorControls.add(statusLabel);

        sidepanel.add(simControls,    "sim_controls");
        sidepanel.add(editorControls, "editor_controls");
        myframe.add(sidepanel, BorderLayout.LINE_START);

        // ---- Mode toggle actions ----
        // City Mode: show editor grid + editor control panel
        cityModeBtn.addActionListener(e -> {
            sideCard.show(sidepanel,    "editor_controls");
            gridCard.show(gridCardPanel, "editor_grid");
            myframe.validate();
            myframe.repaint();
        });
        // Simulator: show simulation grid + start/stop panel
        simulatorBtn.addActionListener(e -> {
            sideCard.show(sidepanel,    "sim_controls");
            gridCard.show(gridCardPanel, "simulation_grid");
            myframe.validate();
            myframe.repaint();
        });

        // ---- Load button — applies the selected layout ----
        loadLayoutBtn.addActionListener(e -> {
            String selected = (String) layoutDropdown.getSelectedItem();
            if (selected == null) return;
            switch (selected) {
                case "Layout 1":
                    loadPreset(the_grid::layout1, myframe);
                    statusLabel.setText("<html>Layout 1 loaded.<br>3-way + 4-way.</html>");
                    break;
                case "Layout 2":
                    loadPreset(the_grid::layout2, myframe);
                    statusLabel.setText("<html>Layout 2 loaded.<br>Single crossroads.</html>");
                    break;
                case "Layout 3":
                    loadPreset(the_grid::layout3, myframe);
                    statusLabel.setText("<html>Layout 3 loaded.<br>H-shape, 2 junctions.</html>");
                    break;
                default:
                    // Custom saved layout — read from the data file
                    boolean ok = LayoutManager.loadLayout(selected, the_grid);
                    if (ok) {
                        the_grid.drawGrid();
                        myframe.repaint();
                        statusLabel.setText("<html>\"" + selected + "\"<br>loaded.</html>");
                    } else {
                        statusLabel.setText("<html><font color='red'>Layout not<br>found in file.</font></html>");
                    }
            }
        });

        // ---- Save Layout — persists current grid state with a user-supplied name ----
        saveLayoutBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(myframe,
                    "Enter a name for this layout:",
                    "Save Layout",
                    JOptionPane.PLAIN_MESSAGE);

            // User cancelled or left the field empty
            if (name == null || name.trim().isEmpty()) return;
            name = name.trim();

            // Guard against overwriting the built-in presets
            if (name.equals("Layout 1") || name.equals("Layout 2") || name.equals("Layout 3")) {
                JOptionPane.showMessageDialog(myframe,
                        "\"" + name + "\" is a built-in preset and cannot be overwritten.\n"
                                + "Choose a different name.",
                        "Reserved Name", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Warn if a custom layout with this name already exists
            if (LayoutManager.getCustomLayoutNames().contains(name)) {
                int confirm = JOptionPane.showConfirmDialog(myframe,
                        "A saved layout named \"" + name + "\" already exists.\nOverwrite it?",
                        "Overwrite?", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            try {
                LayoutManager.saveLayout(name, the_grid);
                refreshLayoutDropdown();
                layoutDropdown.setSelectedItem(name); // select the just-saved layout
                statusLabel.setText("<html>\"" + name + "\"<br>saved.</html>");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(myframe,
                        "Failed to save layout:\n" + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- Add Road toggle ----
        // Clicking the button enters or exits interactive road-drawing mode.
        // While active, two clicks on the editor grid define a road's endpoints.
        addRoadBtn.addActionListener(e -> {
            drawRoadMode = !drawRoadMode;
            // Reset any in-progress road on mode exit
            roadStart = null;
            editorGrid.highlightTile = null;
            editorGrid.repaint();
            if (drawRoadMode) {
                addRoadBtn.setText("Cancel Road");
                statusLabel.setText("<html>Click start point<br>on the grid</html>");
            } else {
                addRoadBtn.setText("Add Road");
                statusLabel.setText("<html>Road mode off</html>");
            }
        });

        // ---- Add Intersection (Swing dialog) ----
        addIntBtn.addActionListener(e -> the_grid.create_intersection());

        // ---- Mouse listener for road drawing on the editor grid ----
        // Delegates to handleEditorClick which manages the two-click state machine
        editorGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleEditorClick(e.getX(), e.getY(), addRoadBtn);
            }
        });

        // ---- Simulation start / stop ----
        startBtn.addActionListener(e -> {
            autogeneratecars();
            process_run(myframe);
        });
        stopBtn.addActionListener(e -> stop_sim());

        myframe.revalidate();
        myframe.repaint();
        myframe.setVisible(true);
    }
}
