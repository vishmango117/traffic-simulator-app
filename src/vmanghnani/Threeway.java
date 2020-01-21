package vmanghnani;

import java.util.ArrayList;

public class Threeway extends Intersection {
    private ArrayList<Road> next_position;

    public Threeway(Road int_road, ArrayList<Road> next_position) {
        super(int_road);
        this.next_position = next_position;
    }

    public Threeway(ArrayList<Road> next_positions) {
        this.next_position = next_position;
    }

    public Threeway() {
    }

    public ArrayList<Road> getNext_position() {
        return next_position;
    }
}
