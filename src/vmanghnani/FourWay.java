package vmanghnani;

public class FourWay extends Intersection {
    private Road left_size_pos;
    private Road right_size_pos;
    private Road straight_size_pos;

    public FourWay(Coordinates position, Road left_size_pos, Road right_size_pos, Road straight_size_pos) {
        super(position);
        this.left_size_pos = left_size_pos;
        this.right_size_pos = right_size_pos;
        this.straight_size_pos = straight_size_pos;
    }

    public Road getLeft_size_pos() {
        return left_size_pos;
    }

    public void setLeft_size_pos(Road left_size_pos) {
        this.left_size_pos = left_size_pos;
    }

    public Road getRight_size_pos() {
        return right_size_pos;
    }

    public void setRight_size_pos(Road right_size_pos) {
        this.right_size_pos = right_size_pos;
    }

    public Road getStraight_size_pos() {
        return straight_size_pos;
    }

    public void setStraight_size_pos(Road straight_size_pos) {
        this.straight_size_pos = straight_size_pos;
    }
}
