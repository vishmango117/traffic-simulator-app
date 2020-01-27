package trafficsimulator;

public class Coordinates {
    private int Y_Pos;
    private int X_Pos;

    public Coordinates(int X_Pos, int Y_Pos) {
        this.X_Pos = X_Pos;
        this.Y_Pos = Y_Pos;
    }

    public Coordinates() {
    }

    public static Coordinates add_coordinate (Coordinates coords1, Coordinates coords2) {
        Coordinates result = new Coordinates(0,0);
        result.setX_Pos(coords1.X_Pos+coords2.X_Pos);
        result.setY_Pos(coords1.Y_Pos+coords2.Y_Pos);
        return result;
    }

    public Coordinates direction_to_coords(char direction) {
        Coordinates mycoords;
        if(direction == 'N') {
            mycoords = new Coordinates(0,-1);
        }
        else if(direction == 'S') {
            mycoords = new Coordinates(0, 1);
        }

        else if(direction == 'E') {
            mycoords = new Coordinates(1,0);
        }
        else if(direction == 'W') {
            mycoords = new Coordinates(-1,0);
        }
        else {
            mycoords = new Coordinates(0,0);
        }
        return mycoords;
    }

    public int getY_Pos() {
        return Y_Pos;
    }

    public void setY_Pos(int y_Pos) {
        Y_Pos = y_Pos;
    }

    public int getX_Pos() {
        return X_Pos;
    }

    public void setX_Pos(int x_Pos) {
        X_Pos = x_Pos;
    }
}
