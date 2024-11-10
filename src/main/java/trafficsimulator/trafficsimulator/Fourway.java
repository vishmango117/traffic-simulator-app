package trafficsimulator;

import java.util.ArrayList;

public class Fourway extends Intersection {
    private ArrayList<trafficsimulator.Road> next_position;

    public Fourway(trafficsimulator.Road current_position, ArrayList<trafficsimulator.Road> next_position) {
        super(current_position);
        this.next_position = next_position;
    }

    public Fourway() {
    }

    public Fourway(ArrayList<trafficsimulator.Road> next_position) {
        this.next_position = next_position;
    }

    public ArrayList<Road> getNext_position() {
        return next_position;
    }
}
