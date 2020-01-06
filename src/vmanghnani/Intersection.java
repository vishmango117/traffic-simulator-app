package vmanghnani;

public class Intersection {
    private Coordinates current_position;

    public Intersection(Coordinates current_position) {
        this.current_position = current_position;
    }

    public Coordinates getCurrent_position() {
        return current_position;
    }

    public void setCurrent_position(Coordinates current_position) {
        this.current_position = current_position;
    }
}
