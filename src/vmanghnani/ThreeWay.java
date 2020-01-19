package vmanghnani;

import java.util.ArrayList;

public class ThreeWay extends Intersection {
    private ArrayList<Road> next_positions;

    public ThreeWay(Road int_road, ArrayList<Road> next_positions) {
        super(int_road);
        this.next_positions = next_positions;
    }

    public ThreeWay(ArrayList<Road> next_positions) {
        this.next_positions = next_positions;
    }

    public ThreeWay() {
    }
}
