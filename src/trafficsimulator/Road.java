package trafficsimulator;

public class Road {
    private int road_no;
    private Coordinates start_pos;
    private Coordinates end_pos;
    private int no_of_segments;
    private char Direction;

    public Road(int road_no, Coordinates start_pos, Coordinates end_pos, char Direction) {
        this.road_no = road_no;
        this.start_pos = start_pos;
        this.end_pos = end_pos;
        this.Direction = Direction;
    }

    public Coordinates getStart_pos() {
        return start_pos;
    }

    public Coordinates getEnd_pos() {
        return end_pos;
    }

    public int getNo_of_segments() {
        return no_of_segments;
    }

    public int getRoad_no() {
        return road_no;
    }

    public void setRoad_no(int road_no) {
        this.road_no = road_no;
    }

    public char getDirection() {
        return Direction;
    }

    public void setDirection(char direction) {
        Direction = direction;
    }
}
