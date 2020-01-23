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
        if(this.color=="Green") {
            this.color = "Red";
        }
        else if(this.color=="Red") {
            this.color="Green";
        }
    }

    public static void spawn_traffc_light() {

    }
}
