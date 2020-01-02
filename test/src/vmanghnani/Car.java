package vmanghnani;

public class Car {
    private Coordinates car_pos;
    private int car_speed;
    private char Direction;

    public Car(Coordinates car_pos) {
        this.car_pos = car_pos;
        this.car_speed = 0;
        this.Direction = 'S';
    }

    public void Move() {
        this.car_speed = 1;
    }
    public void Stop(){
        this.car_speed = 0;
    }

    public Coordinates getCar_pos() {
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
        return Direction;
    }

    public void setDirection(char direction) {
        Direction = direction;
    }
}
