package vmanghnani;

public class Straight extends Intersection {
    private Coordinates next_position;

    public Straight(Coordinates current_position, Coordinates next_position) {
        super(current_position);
        this.next_position = next_position;
    }

    public Coordinates getNext_position() {
        return next_position;
    }
}
