package trafficsimulator;

public class Car {
    private int car_no;
    private trafficsimulator.Coordinates car_pos;
    private int car_speed;
    private char direction;

    public Car(int car_no, trafficsimulator.Coordinates car_pos, char direction) {
        this.car_no = car_no;
        this.car_pos = car_pos;
        this.car_speed = 0;
        this.direction = direction;
    }

    public void Move() {
        this.car_speed = 1;
    }
    public void Stop(){
        this.car_speed = 0;
    }

    public trafficsimulator.Coordinates getCar_pos() {
        return car_pos;
    }

    public void setCar_pos(Coordinates car_pos) {
        this.car_pos = car_pos;
    }

    public int getCar_speed() {
        return car_speed;
    }

    public void setCar_speed(int car_speed) {
        this.car_speed = car_speed;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public int getCar_no() {
        return car_no;
    }

    public void setCar_no(int car_no) {
        this.car_no = car_no;
    }
}
