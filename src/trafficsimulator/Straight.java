package trafficsimulator;

public class Straight extends Intersection {
    private trafficsimulator.Road next_road;

    public Straight(trafficsimulator.Road int_road, trafficsimulator.Road next_road) {
        super(int_road);
        this.next_road = next_road;
    }


    public Straight() {
        super();
    }

    public trafficsimulator.Road getNext_road() {
        return next_road;
    }

    public void setNext_road(Road next_road) {
        this.next_road = next_road;
    }
}
