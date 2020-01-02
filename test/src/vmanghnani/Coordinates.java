package vmanghnani;

public class Coordinates {
    private int x_coordinate;
    private int y_coordinate;

    public Coordinates(int x_coordinate, int y_coordinate) {
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }

    public static Coordinates add_coordinate (Coordinates coords1, Coordinates coords2) {
        Coordinates result = new Coordinates(0,0);
        result.setX_coordinate(coords1.x_coordinate+coords2.x_coordinate);
        result.setY_coordinate(coords1.y_coordinate+coords2.y_coordinate);
        return result;
    }

    public Coordinates direction_to_coords(char direction) {
        Coordinates mycoords;
        switch (direction) {
            case 'E': {
                mycoords = new Coordinates(0, 1);
                break;
            }
            case 'S': {
                mycoords = new Coordinates(1, 0);
                break;
            }
            case 'W': {
                mycoords = new Coordinates(0, -1);
                break;
            }
            case 'N': {
                mycoords = new Coordinates(-1, 0);
                break;
            }
            default: {
                mycoords = new Coordinates(0, 0);
                System.out.println("No Change");
            }
        }
        return mycoords;
    }

    public int getX_coordinate() {
        return x_coordinate;
    }

    public int getY_coordinate() {
        return y_coordinate;
    }

    public void setX_coordinate(int x_coordinate) {
        this.x_coordinate = x_coordinate;
    }

    public void setY_coordinate(int y_coordinate) {
        this.y_coordinate = y_coordinate;
    }


}
