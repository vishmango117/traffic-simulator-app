package vmanghnani;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Grid {
    private String[][] mygrid_roads;
    private String[][] mygrid_vehicles;
    private int height;
    private int width;
    public ArrayList<Road> myroads;
    public ArrayList<Car> mycars;
    public ArrayList<TrafficLight> mytrafficlights;
    public ArrayList<Straight> my_straight_intersections;
    public ArrayList<Threeway> my_threeway_intersections;
    public ArrayList<Fourway> my_fourway_intersections;

    public Grid(int height, int width) {
        this.mygrid_roads = new String[height][width];
        this.mygrid_vehicles = new String[height][width];
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
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                this.mygrid_roads[i][j] = "   ";
                this.mygrid_vehicles[i][j] = "   ";
            }
        }
        //Constructing Roads
        for (Road currentroad : this.myroads) {
            for (int i = currentroad.getStart_pos().getY_Pos(); i <= currentroad.getEnd_pos().getY_Pos(); i++) {
                for (int j = currentroad.getStart_pos().getX_Pos(); j <= currentroad.getEnd_pos().getX_Pos(); j++) {
                    this.mygrid_roads[i][j] = "RD1";
                }
            }
        }
        //Adding Intersections
        for (Straight current_intersections : my_straight_intersections) {
            this.mygrid_roads[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "ST1";
        }
        //Adding Three Way Intersections
        for(Threeway current_intersections: my_threeway_intersections) {
            this.mygrid_roads[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "TW1";
        }

        //Adding Three Way Intersections
        for(Fourway current_intersections: my_fourway_intersections) {
            this.mygrid_roads[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "FW1";
        }

        //Adding Cars
        for (Car currentcar : this.mycars) {
            this.mygrid_vehicles[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] = "CR1";

        }
//        for(TrafficLight current_tl: mytrafficlights) {
//            this.mygrid[current_tl.getPosition().getY_Pos()][current_tl.getPosition().getX_Pos()]="T1";
//        }

    }

    //For Debugging Testing and Development Purposes Only
    public void printGrid() {
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                if (this.mygrid_vehicles[i][j] == "CR1") {
                    System.out.printf("|%s|", this.mygrid_vehicles[i][j]);
                }
                else {
                    System.out.printf("|%s|", this.mygrid_roads[i][j]);
                }
            }
            System.out.println();
        }
    }

    /*
    public void displayGrid() {
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                //if(this.mygrid[i][j].equals("TB") || this.mygrid[i][j].equals("BB"))
                //   System.out.printf("-----");
                if (this.mygrid[i][j].equals("SB")) {
                    System.out.printf("|");
                } else if (this.mygrid[i][j].startsWith("R")) {
                    System.out.printf("|---|");
                } else if (this.mygrid[i][j].startsWith("C")) {
                    System.out.printf("|-C-|");
                } else if (this.mygrid[i][j].startsWith("I")) {
                    System.out.printf("|-X-|");
                } else if (this.mygrid[i][j].startsWith("T")) {
                    System.out.println("|-:-|");
                } else {
                    System.out.printf("|   |");
                }
            }
            System.out.println();
        }
    }*/

    public boolean checkIntersection() {
        for (Car currentcar : mycars) {
            if (this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "ST1" ||
                    this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "TW1" ||
                    this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "FW1") {
                return false;
            }
        }
        return true;
    }

    public void updateTrafficLight() {
        for (TrafficLight currentTrafficLight : mytrafficlights) {
            currentTrafficLight.operate();
        }
    }

    public boolean checkTrafficLight(Coordinates next_pos) {
        for (TrafficLight current_tl : mytrafficlights) {
            if (current_tl.getPosition().equals(next_pos)) {
                if (current_tl.getColor() == "G") {
                    return true;
                }
            }
        }
        return false;
    }


    public void updateCarPos(ArrayList<Car> mycars) {
        for (Car currentcar : mycars) {
            Coordinates current_pos = currentcar.getCar_pos();
            Coordinates next_pos = Coordinates.add_coordinate(current_pos, current_pos.direction_to_coords(currentcar.getDirection()));
            // when cars are reaching near the intersection
            if (this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("ST1") ||
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("TW1") ||
                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("FW1")) {
                if (checkIntersection()) {
                    currentcar.setCar_pos(next_pos);
                }
            }
            //when car is on road
            else if(this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("RD1")  &&
            this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {
                currentcar.setCar_pos(next_pos);
            }
            //when car is inside the straight intersection
            else if(this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("ST1")) {
                for (Straight current_int : this.my_straight_intersections) {
                    if (current_int.getInt_road().equals(currentcar.getCar_pos())) {
                        currentcar.setCar_pos(current_int.getNext_road().getStart_pos());
                    }
                }
            }
            //when car is inside the three-way intersection
            else if(this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("TW1") &&
            this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {
                System.out.println("Yes");
                Threeway myintersection = new Threeway();
                for(Threeway current_int: this.my_threeway_intersections) {
                    if(current_int.getInt_road().getStart_pos().getX_Pos() == currentcar.getCar_pos().getX_Pos() &&
                    current_int.getInt_road().getStart_pos().getY_Pos() == currentcar.getCar_pos().getY_Pos()) {
                        myintersection = current_int;
                    }
                }
                Random mydirection = new Random();
                int mynumber = mydirection.nextInt(2);
                if(currentcar.getDirection()=='N') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'W') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                    }
                else if(currentcar.getDirection()=='S') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                }
                else if(currentcar.getDirection()=='E') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'N') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                }
                else if(currentcar.getDirection()=='W') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                }
            }
            //when car is inside the four-way intersection
            else if(this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("FW1") &&
            this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {
                System.out.println("Yes");
                Fourway myintersection = new Fourway();
                for(Fourway current_int: this.my_fourway_intersections) {
                    if(current_int.getInt_road().getStart_pos().getX_Pos() == currentcar.getCar_pos().getX_Pos() &&
                    current_int.getInt_road().getStart_pos().getY_Pos() == currentcar.getCar_pos().getY_Pos()) {
                        myintersection = current_int;
                    }
                }
                Random mydirection = new Random();
                int mynumber = mydirection.nextInt(3);
                if(currentcar.getDirection()=='N') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'W') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                }
                else if(currentcar.getDirection()=='S') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                }
                else if(currentcar.getDirection()=='E') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'N') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                }
                else if(currentcar.getDirection()=='W') {
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'S') {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getStart_pos());
                    }
                    else {
                        currentcar.setDirection(myintersection.getNext_position().get(mynumber).getDirection());
                        currentcar.setCar_pos(myintersection.getNext_position().get(mynumber).getEnd_pos());
                    }
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {

        //Initialising the grid
        Grid the_grid = new Grid(30,30);
        the_grid.myroads = new ArrayList<>();
        the_grid.mycars = new ArrayList<>();
        the_grid.my_straight_intersections = new ArrayList<>();
        the_grid.mytrafficlights = new ArrayList<>();
        the_grid.my_threeway_intersections = new ArrayList<>();
        the_grid.my_fourway_intersections = new ArrayList<>();

        //Adding Roads for threeway
        the_grid.myroads.add(new Road(1, new Coordinates(10,2), new Coordinates(17, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(19,2), new Coordinates(28, 2),'E'));
        the_grid.myroads.add(new Road(3, new Coordinates(18,3), new Coordinates(18, 20),'S'));

        //Adding Roads for fourway
        the_grid.myroads.add(new Road(4, new Coordinates(12,10), new Coordinates(12,15),'N'));
        the_grid.myroads.add(new Road(5, new Coordinates(12,17), new Coordinates(12,20), 'S'));
        the_grid.myroads.add(new Road(6, new Coordinates(13,16), new Coordinates(15,16), 'E'));
        the_grid.myroads.add(new Road(7, new Coordinates(5,16), new Coordinates(11,16), 'E'));

        //Adding 3Way
        Threeway intersection3 = new Threeway(new Road(250, new Coordinates(18,2), new Coordinates(18,2),'S'), new ArrayList<Road>());
        intersection3.getNext_position().add(the_grid.myroads.get(1));
        intersection3.getNext_position().add(the_grid.myroads.get(2));
        the_grid.my_threeway_intersections.add(intersection3);

        //Adding 4way
        Fourway intersection4 = new Fourway(new Road(250, new Coordinates(12,16), new Coordinates(12,16),'E'), new ArrayList<Road>());
        intersection4.getNext_position().add(the_grid.myroads.get(3));
        intersection4.getNext_position().add(the_grid.myroads.get(4));
        intersection4.getNext_position().add(the_grid.myroads.get(5));
        intersection4.getNext_position().add(the_grid.myroads.get(6));
        the_grid.my_fourway_intersections.add(intersection4);
        //Adding the cars
        the_grid.mycars.add(new Car(1, the_grid.myroads.get(0).getStart_pos(), the_grid.myroads.get(0).getDirection()));
        the_grid.mycars.add(new Car(2, the_grid.myroads.get(6).getStart_pos(), the_grid.myroads.get(6).getDirection()));
        the_grid.mycars.get(0).Move();
        the_grid.mycars.get(1).Move();


        try {
            for(int i=0;i<15;i++) {
                the_grid.drawGrid();
                the_grid.printGrid();
                System.out.println();
                the_grid.updateCarPos(the_grid.mycars);
            }
        }
        catch(NullPointerException e) {
            System.out.println("LUL");
        }
        the_grid.mycars.get(0).Stop();
        //the_grid.mycars.get(1).Stop();
    }

}
