package vmanghnani;

public class TrafficLight {
    private String color;
    private Coordinates Position;

    public TrafficLight(String color, Coordinates position) {
        this.color = color;
        Position = position;
    }

    public TrafficLight() {
    }

    public String getColor() {
        return color;
    }

    public Coordinates getPosition() {
        return Position;
    }

    public void operate() {
        if(this.color=="G") {
            this.color = "R";
        }
        else if(this.color=="R") {
            this.color="G";
        }
    }

    public static void spawn_traffc_light() {

    }
}
