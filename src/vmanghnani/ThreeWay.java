package vmanghnani;

public class ThreeWay extends Intersection {
    private Road left_pos;
    private Road right_pos;

    public ThreeWay(Coordinates position, Road left_pos, Road right_pos) {
        super(position);
        this.left_pos = left_pos;
        this.right_pos = right_pos;
    }

    public Road getLeft_pos() {
        return left_pos;
    }

    public void setLeft_pos(Road left_pos) {
        this.left_pos = left_pos;
    }

    public Road getRight_pos() {
        return right_pos;
    }

    public void setRight_pos(Road right_pos) {
        this.right_pos = right_pos;
    }
}
