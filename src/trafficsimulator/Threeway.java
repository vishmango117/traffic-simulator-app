package trafficsimulator;

import java.util.ArrayList;

public class Threeway extends Intersection {
    private ArrayList<trafficsimulator.Road> next_position;

    public Threeway(trafficsimulator.Road int_road, ArrayList<trafficsimulator.Road> next_position) {
        super(int_road);
        this.next_position = next_position;
    }

    public Threeway(ArrayList<trafficsimulator.Road> next_positions) {
        this.next_position = next_position;
    }

    public Threeway() {
    }

    public ArrayList<Road> getNext_position() {
        return next_position;
    }
}
