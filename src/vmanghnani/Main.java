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
        for (Road currentroad : this.myroads) {
            for (int i =currentroad.getStart_pos().getY_Pos(); i<=currentroad.getEnd_pos().getY_Pos(); i++) {
                for (int j =currentroad.getStart_pos().getX_Pos(); j<=currentroad.getEnd_pos().getX_Pos(); j++) {
                    this.mygrid[i][j] = "R1";
                }
            }
        }
        //Adding Cars
        for (Car currentcar : this.mycars) {
            this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()]="C1";

        }
        //Adding Intersections
        for(Straight current_intersections : straight_intersections) {
            this.mygrid[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()]="I1";
        }
//        for(TrafficLight current_tl: mytrafficlights) {
//            this.mygrid[current_tl.getPosition().getY_Pos()][current_tl.getPosition().getX_Pos()]="T1";
//        }

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

    public boolean checkIntersection() {
        for (Car currentcar: mycars) {
            if(this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "I1") {
                return false;
            }
        }
        return true;
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
            if(this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("R1")) {
                currentcar.setCar_pos(next_pos);
            }
//            else if(this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("I1")) {
//                Straight myintersection = new Straight();
//                for(Straight current_int: straight_intersections) {
//                    if(current_int.getInt_road().getStart_pos().getX_Pos() == next_pos.getX_Pos() && current_int.getInt_road().getStart_pos().getY_Pos() == next_pos.getY_Pos()) {
//                        myintersection = current_int;
//                    }
//                }
//                if(checkIntersection()) {
//                    currentcar.setCar_pos(next_pos);
//                    currentcar.setCar_pos(myintersection.getNext_road().getStart_pos());
//                }
//            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {

        Grid the_grid = new Grid(30,30);
        the_grid.myroads = new ArrayList<>();
        the_grid.mycars = new ArrayList<>();
        the_grid.straight_intersections = new ArrayList<>();
        the_grid.mytrafficlights = new ArrayList<>();


        the_grid.myroads.add(new Road(1, new Coordinates(10,2), new Coordinates(17, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(19,2), new Coordinates(28, 2),'E'));
        //the_grid.myroads.add(new Road(2, new Coordinates(15,10), new Coordinates(15, 25),'S'));
        //the_grid.myroads.add(new Road(3, new Coordinates(16,10), new Coordinates(28, 10),'E'));
        Straight intersection = new Straight(new Road(150,new Coordinates(18,2), new Coordinates(18,2),'E'), the_grid.myroads.get(1));

        the_grid.straight_intersections.add(intersection);
        //the_grid.mytrafficlights.add(0,new TrafficLight("G", Coordinates.add_coordinate(the_grid.straight_intersections.get(0).getCurrent_position(), new Coordinates(-1,-1))));

        the_grid.mycars.add(new Car(1, the_grid.myroads.get(0).getStart_pos(), the_grid.myroads.get(0).getDirection()));
        //the_grid.mycars.add(new Car(2, the_grid.myroads.get(2).getStart_pos(), the_grid.myroads.get(2).getDirection()));
        the_grid.mycars.get(0).Move();
        //the_grid.mycars.get(1).Move();
        try {
            for(int i=0;i<4;i++) {
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
