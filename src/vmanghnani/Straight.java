package vmanghnani;

public class Straight extends Intersection {
    private Road next_road;

    public Straight(Road int_road, Road next_road) {
        super(int_road);
        this.next_road = next_road;
    }


    public Straight() {
        super();
    }

    public Road getNext_road() {
        return next_road;
    }

    public void setNext_road(Road next_road) {
        this.next_road = next_road;
    }
}
