# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

Two Maven profiles control how the app is launched.

### Dev (default) — runs directly from compiled classes, no packaging needed
```bash
./mvnw javafx:run          # uses -Pdev automatically (activeByDefault)
./mvnw javafx:run -Pdev    # explicit
```

### UAT — packages the JAR, copies deps to `target/lib`, runs via `java --module-path`
```bash
./mvnw package exec:exec -Puat   # compile + package + copy deps + run
./mvnw package -Puat             # compile + package + copy deps only
./mvnw exec:exec -Puat           # run already-packaged output
```

The UAT launcher sets `-Denv=uat`; dev sets `-Denv=dev`. Neither is consumed by the app yet — it's available for future environment-branching logic.

### Tests and compilation
```bash
./mvnw test                      # run all tests
./mvnw test -Dtest=ClassName     # run a single test class
./mvnw compile                   # compile only
```

The project targets Java 23 and uses JavaFX 17 + FXGL 17.3. Because `module-info.java` declares a named module (`trafficsimulator.trafficsimulator`), both profiles use `--module-path` rather than `-cp` — the UAT profile copies runtime deps to `target/lib` for this reason.

## Architecture Overview

The simulation is built around a `Grid` class (in `Main.java`) that acts as the central state container. It owns two parallel 2D string arrays — `mygrid_roads` and `mygrid_vehicles` — that form a 51×51 tile map. Cell values are string tokens:

| Token | Meaning |
|-------|---------|
| `"RD1"` | Road tile |
| `"ST1"` | Straight-through intersection |
| `"TW1"` | Three-way intersection |
| `"FW1"` | Four-way intersection |
| `"TL1"` | Traffic light |
| `"SP1"` | Spawn/despawn point (road boundary) |
| `"CR1"` | Car (written to `mygrid_vehicles`) |

**Simulation loop** (`backup.process_run`): every 500 ms, the loop calls `Grid.drawGrid()` → `Grid.updateCarPos()` → repaint. Every 5 ticks, all traffic lights are toggled via `TrafficLight.operate()`.

**Intersection routing**: `Grid.updateCarPos()` inspects the *next* tile a car is about to enter and dispatches differently for each intersection type. At three-way and four-way intersections, the exit road is chosen randomly from `next_position` (2 choices for `Threeway`, 3 for `Fourway`). Cars are blocked at intersections unless `checkIntersection()` and `checkTrafficLight()` both pass.

**Coordinate system**: `Coordinates(X_Pos, Y_Pos)` — note that the grid arrays are indexed `[Y][X]` (row-major), so keep this in mind when reading or writing tile data.

**Intersection class hierarchy**:
- `Intersection` (base) — holds the intersection tile as a `Road` object
  - `Straight` — connects to one `next_road`
  - `Threeway` — holds an `ArrayList<Road>` of 2 exit roads
  - `Fourway` — holds an `ArrayList<Road>` of 3–4 exit roads

**UI**: The active UI is `backup.java` (Swing/AWT, `JFrame`-based). `gui.java` is an earlier prototype kept for reference. The app has two modes toggled by top buttons: **City Mode** (layout/editor panel on the left side) and **Simulator** (start/stop panel). Pre-built layouts (`layout1`, `layout2`) are defined on `Grid` and wired to buttons. Custom roads and intersections can be added via Swing dialog boxes (`create_road`, `create_intersection`).

**Known issues / rough edges**:
- `TrafficLight.operate()` and color comparisons use `==` on `String` literals instead of `.equals()` — this only works by accident due to string interning.
- `Threeway(ArrayList)` constructor has a bug: it references `this.next_position = next_position` where the field is uninitialized (the variable shadows itself); use the two-arg constructor instead.
- The "Stop" button calls the deprecated `Thread.stop()`.
- `layout2()` uses `my_threeway_intersections.get(0)` for traffic light positions even though it only populates `my_fourway_intersections`, causing an `IndexOutOfBoundsException`.
- The `hello-view.fxml` resource exists but is unused — it's a leftover from an abandoned JavaFX FXML approach.