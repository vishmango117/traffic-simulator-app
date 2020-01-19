package vmanghnani;

import java.util.ArrayList;

public class FourWay extends Intersection {
    private ArrayList<Road> next_position;

    public FourWay(Road current_position, ArrayList<Road> next_position) {
        super(current_position);
        this.next_position = next_position;
    }

    public FourWay(ArrayList<Road> next_position) {
        this.next_position = next_position;
    }
}
