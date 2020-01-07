package vmanghnani;

import java.io.IOException;
import java.util.ArrayList;

class Grid {
    private String[][] mygrid;
    private int height;
    private int width;
    public ArrayList<Road> myroads;
    public ArrayList<Car> mycars;
    public ArrayList<TrafficLight> mytrafficlights;
    public ArrayList<Straight> straight_intersections;

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


    public void drawGrid() {
        //Initialise Plain Grid
        for (int i = 0; i <this.getHeight(); i++) {
            for (int j = 0; j <this.getWidth(); j++) {
                this.mygrid[i][j] = "  ";
            }
        }
        //Constructing Roads
        for (Road currentroad : myroads) {
            for (int i =currentroad.getStart_pos().getY_Pos(); i<=currentroad.getEnd_pos().getY_Pos(); i++) {
                for (int j =currentroad.getStart_pos().getX_Pos(); j<=currentroad.getEnd_pos().getX_Pos(); j++) {
                    this.mygrid[i][j] = "R1";
                }
            }
        }
        //Adding Cars
        for (Car currentcar : mycars) {
            this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()]="C1";

        }
        //Adding Intersections
        for(Straight current_intersections : straight_intersections) {
            this.mygrid[current_intersections.getCurrent_position().getY_Pos()][current_intersections.getCurrent_position().getX_Pos()]="I1";
            this.mygrid[current_intersections.getCurrent_position().getY_Pos()-1][current_intersections.getCurrent_position().getX_Pos()-1]="T1";
        }
    }
    //For Debugging Testing and Development Purposes Only
    public void printGrid() {
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                System.out.printf("|%s|", this.mygrid[i][j]);
            }
            System.out.println();
        }
    }


    public void displayGrid() {
        for(int i=0;i<this.getHeight();i++) {
            for(int j=0;j<this.getWidth();j++) {
                //if(this.mygrid[i][j].equals("TB") || this.mygrid[i][j].equals("BB"))
                //   System.out.printf("-----");
                if(this.mygrid[i][j].equals("SB")){
                    System.out.printf("|");
                }
                else if(this.mygrid[i][j].startsWith("R")) {
                    System.out.printf("|---|");
                }
                else if(this.mygrid[i][j].startsWith("C")) {
                    System.out.printf("|-C-|");
                }
                else if(this.mygrid[i][j].startsWith("I")) {
                    System.out.printf("|-X-|");
                }
                else if(this.mygrid[i][j].startsWith("T")) {
                    System.out.println("|-:-|");
                }
                else {
                    System.out.printf("|   |");
                }
            }
            System.out.println();
        }
    }

    public void getIntersection() {

    }

    public void updateTrafficLight() {
        for(TrafficLight currentTrafficLight: mytrafficlights) {
            currentTrafficLight.operate();
        }
    }

    public boolean checkTrafficLight(Coordinates next_pos) {
        for(TrafficLight current_tl: mytrafficlights) {
            if(current_tl.getPosition().equals(next_pos)) {
                if(current_tl.getColor()=="G") {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateCarPos(ArrayList<Car> mycars) {
        for(Car currentcar: mycars) {
            Coordinates current_pos = currentcar.getCar_pos();
            Coordinates next_pos = Coordinates.add_coordinate(current_pos, current_pos.direction_to_coords(currentcar.getDirection()));
            if(this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()] == "R1") {
                currentcar.setCar_pos(next_pos);
            }
            else if(this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()] == "I1") {
                if(checkTrafficLight(next_pos)) {

                }
                else {
                    currentcar.setCar_pos(current_pos);
                }
                // check traffic light

                // check if car is not inside the intersection
            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {

        Grid the_grid = new Grid(30,30);
        the_grid.myroads = new ArrayList<>();
        the_grid.mycars = new ArrayList<>();
        the_grid.straight_intersections = new ArrayList<>();

        the_grid.myroads.add(new Road(1, new Coordinates(10,2), new Coordinates(17, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(19,2), new Coordinates(28, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(15,10), new Coordinates(15, 25),'S'));
        //the_grid.myroads.add(new Road(3, new Coordinates(16,10), new Coordinates(28, 10),'E'));
        Straight lol = new Straight(new Coordinates(18,2), new Coordinates(19,2));

        the_grid.straight_intersections.add(lol);

        the_grid.mycars.add(new Car(1, the_grid.myroads.get(0).getStart_pos(), the_grid.myroads.get(0).getDirection()));
        the_grid.mycars.add(new Car(2, the_grid.myroads.get(2).getStart_pos(), the_grid.myroads.get(2).getDirection()));
        the_grid.mycars.get(0).Move();
        the_grid.mycars.get(1).Move();
        try {
            for(int i=0;i<16;i++) {
                the_grid.updateCarPos(the_grid.mycars);
                the_grid.drawGrid();
                the_grid.printGrid();
                System.out.println();
            }
        }
        catch(NullPointerException e) {
            System.out.println("LUL");
        }
        the_grid.mycars.get(0).Stop();
//        the_grid.mycars.get(1).Stop();
    }

}
