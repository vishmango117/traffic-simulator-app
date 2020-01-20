package vmanghnani;

import java.util.ArrayList;

public class ThreeWay extends Intersection {
    private ArrayList<Road> next_position;

    public ThreeWay(Road int_road, ArrayList<Road> next_position) {
        super(int_road);
        this.next_position = next_position;
    }

    public ThreeWay(ArrayList<Road> next_positions) {
        this.next_position = next_position;
    }

    public ArrayList<Road> getNext_position() {
        return next_position;
    }
}
