package vmanghnani;

import java.io.IOException;
import java.util.ArrayList;

class Grid {
    private String[][] mygrid;
    private int height;
    private int width;
    public ArrayList<Road> myroads;
    public ArrayList<Car> mycars;
    public ArrayList<Straight> mystraight_intersections;
    public ArrayList<ThreeWay> mythreeway_intersections;
    public ArrayList<FourWay> myfourway_intersections;

    public Grid(int height, int width) {
        this.mygrid = new String[height][width];
        this.height = height;
        this.width = width;
        this.myroads = new ArrayList<>();
        this.mycars = new ArrayList<>();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void drawRoad(Coordinates start_pos, Coordinates end_pos) {
        //System.out.println(start_pos.getX_coordinate()+","+start_pos.getY_coordinate());
        for(int i=start_pos.getX_coordinate();i<=end_pos.getX_coordinate();i++) {
            for(int j=start_pos.getY_coordinate();j<=end_pos.getY_coordinate();j++) {
                this.mygrid[i][j]="|---|";
            }
        }
    }

    public void drawGrid() {
        //Initialise Plain Grid
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                this.mygrid[i][j] = "|   |";
            }
        }
        //Drawing Borders
        for (int i = 0; i < width; i++) {
            this.mygrid[i][0] = "|";
            this.mygrid[i][width - 1] = "|";
            this.mygrid[0][i] = "_____";
            this.mygrid[height - 1][i] = "_____";
        }
        //Constructing Roads
        for (Road currentroad : myroads) {
            for (int i = currentroad.getStart_pos().getX_coordinate(); i <= currentroad.getEnd_pos().getX_coordinate(); i++) {
                for (int j = currentroad.getStart_pos().getY_coordinate(); j <= currentroad.getEnd_pos().getY_coordinate(); j++) {
                    if (currentroad.getDirection() == 'N') {
                        this.mygrid[i][j] = String.format("|%d^%d|", currentroad.getRoad_no(), currentroad.getRoad_no());
                    } else if (currentroad.getDirection() == 'S') {
                        this.mygrid[i][j] = String.format("|%d_%d|", currentroad.getRoad_no(), currentroad.getRoad_no());
                    } else if (currentroad.getDirection() == 'E') {
                        this.mygrid[i][j] = String.format("|<-%d|", currentroad.getRoad_no(), currentroad.getRoad_no());
                    } else if (currentroad.getDirection() == 'W') {
                        this.mygrid[i][j] = String.format("|%d->|", currentroad.getRoad_no(), currentroad.getRoad_no());
                    }
                }
            }
        }
        //Adding Cars
        for (Car currentcar : mycars) {
            //System.out.printf("%d %d",currentcar.getCar_pos().getX_coordinate(),currentcar.getCar_pos().getY_coordinate());
            this.mygrid[currentcar.getCar_pos().getX_coordinate()][currentcar.getCar_pos().getY_coordinate()] = "|Car|";

        }
        //Adding Intersections
    }


    public void displayGrid() {
        for(int i=0;i<this.getHeight();i++) {
            for(int j=0;j<this.getWidth();j++) {
                System.out.printf("%s",this.mygrid[i][j]);
            }
            System.out.println();
        }
    }

    public void updateCarPos(ArrayList<Car> mycars) {
        for(Car currentcar: mycars) {
            if (currentcar.getCar_speed() == 1 || currentcar.getCar_speed() == 0) {
                Coordinates current_pos = currentcar.getCar_pos();
                Coordinates next_pos = Coordinates.add_coordinate(current_pos, currentcar.getCar_pos().direction_to_coords(currentcar.getDirection()));
                if (this.mygrid[next_pos.getX_coordinate()][next_pos.getY_coordinate()] == "|---|") {
                    currentcar.setCar_pos(next_pos);
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        Grid the_grid = new Grid(30,30);
        the_grid.myroads.add(new Road(1, new Coordinates(1,10), new Coordinates(15, 10),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(2,1), new Coordinates(2, 25),'S'));
        the_grid.myroads.add(new Road(3, new Coordinates(16,10), new Coordinates(28, 10),'E'));
        the_grid.mycars.add(new Car(new Coordinates(1,10)));
        the_grid.mycars.add(new Car(new Coordinates(2,1)));
        the_grid.mycars.get(0).setDirection('S');
        the_grid.mycars.get(1).setDirection('E');
        the_grid.drawGrid();
        the_grid.displayGrid();
        the_grid.mycars.get(0).Move();
        the_grid.mycars.get(1).Move();
        for(int i=0;i<3;i++) {
            the_grid.updateCarPos(the_grid.mycars);
            the_grid.drawGrid();
            the_grid.displayGrid();
        }
        the_grid.mycars.get(0).Stop();
        the_grid.mycars.get(1).Stop();
    }

}
