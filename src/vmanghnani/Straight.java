package vmanghnani;

public class Straight extends Intersection {
    private Road straight_size_pos;

    public Straight(Coordinates position, Road straight_size_pos) {
        super(position);
        this.straight_size_pos = straight_size_pos;
    }

    public Road getStraight_size_pos() {
        return straight_size_pos;
    }

    public void setStraight_size_pos(Road straight_size_pos) {
        this.straight_size_pos = straight_size_pos;
    }
}
