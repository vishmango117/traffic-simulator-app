package trafficsimulator;

public class Intersection {
    private trafficsimulator.Road int_road;

    public Intersection(trafficsimulator.Road int_road) {
        this.int_road = int_road;
    }

    public Intersection() {
    }

    public trafficsimulator.Road getInt_road() {
        return int_road;
    }

    public void setInt_road(Road int_road) {
        this.int_road = int_road;
    }
}
