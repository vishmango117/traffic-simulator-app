package vmanghnani;


import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Grid {
    public String[][] mygrid_roads;
    public String[][] mygrid_vehicles;
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
        for(TrafficLight current_tl: mytrafficlights) {
            this.mygrid_roads[current_tl.getPosition().getY_Pos()][current_tl.getPosition().getX_Pos()]="TL1";
        }

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

    public boolean checkTrafficLight(Car currentcar) {
        Coordinates mytlposition = new Coordinates();
        TrafficLight mytrafficlight = new TrafficLight();
        if(currentcar.getDirection() == 'E') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos(), currentcar.getCar_pos().getY_Pos()-1);
        }
        else if(currentcar.getDirection() == 'W') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos(), currentcar.getCar_pos().getY_Pos()+1);
        }
        else if(currentcar.getDirection() == 'N') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos()-1, currentcar.getCar_pos().getY_Pos()-1);
        }
        else if(currentcar.getDirection() == 'S') {
            mytlposition = new Coordinates(currentcar.getCar_pos().getX_Pos()+1, currentcar.getCar_pos().getY_Pos());
        }
        for(TrafficLight current_tl: mytrafficlights) {
            if(current_tl.getPosition().getY_Pos() == mytlposition.getY_Pos() &&
                    current_tl.getPosition().getX_Pos() == mytlposition.getX_Pos()) {
                mytrafficlight = current_tl;
                System.out.println(current_tl.getPosition().getX_Pos()+","+current_tl.getPosition().getY_Pos());
            }
        }
        if(mytrafficlight.getColor() == "G") {
            return true;
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
                if (checkIntersection() && checkTrafficLight(currentcar)) {
                    currentcar.setCar_pos(next_pos);
                }
            }
            //when car is on road
            else if(this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("RD1")  &&
            this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {
                currentcar.setCar_pos(next_pos);
            }
            //when car is near the end
            else if(this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("RD1") &&
            this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("   ")) {
                mycars.remove(currentcar);
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
                    if(myintersection.getNext_position().get(mynumber).getDirection() == 'W') {
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

    public void layout1() {
        //Adding Roads for threeway
        this.myroads.add(new Road(1, new Coordinates(0, 2), new Coordinates(17, 2), 'E'));
        this.myroads.add(new Road(2, new Coordinates(19, 2), new Coordinates(49, 2), 'E'));
        this.myroads.add(new Road(3, new Coordinates(18, 3), new Coordinates(18, 24), 'S'));

        //Adding Roads for fourway
        this.myroads.add(new Road(5, new Coordinates(12, 17), new Coordinates(12, 20), 'S'));
        this.myroads.add(new Road(6, new Coordinates(13, 16), new Coordinates(15, 16), 'E'));
        this.myroads.add(new Road(7, new Coordinates(0, 25), new Coordinates(17, 25), 'E'));

        //Adding 3Way
        Threeway intersection3 = new Threeway(new Road(250, new Coordinates(18,2), new Coordinates(18,2),'E'), new ArrayList<Road>());
        intersection3.getNext_position().add(this.myroads.get(1));
        intersection3.getNext_position().add(this.myroads.get(2));
        this.my_threeway_intersections.add(intersection3);
        //Eastbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,-1))));
        //Northbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,-1))));
        //Westbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,1))));

        //Adding 4way
        Fourway intersection4 = new Fourway(new Road(250, new Coordinates(18,25), new Coordinates(18,25),'E'), new ArrayList<Road>());
        intersection4.getNext_position().add(this.myroads.get(3));
        intersection4.getNext_position().add(this.myroads.get(4));
        intersection4.getNext_position().add(this.myroads.get(5));
        intersection4.getNext_position().add(this.myroads.get(2));
        this.my_fourway_intersections.add(intersection4);
        //Eastbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,-1))));
        //Southbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,1))));
        //Westbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,1))));
        //Northbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,-1))));

    }
}


public class Main {


    public static void main(String[] args) throws IOException {

        //Initialising the grid
        Grid the_grid = new Grid(50, 50);
        the_grid.myroads = new ArrayList<>();
        the_grid.mycars = new ArrayList<>();
        the_grid.my_straight_intersections = new ArrayList<>();
        the_grid.mytrafficlights = new ArrayList<>();
        the_grid.my_threeway_intersections = new ArrayList<>();
        the_grid.my_fourway_intersections = new ArrayList<>();


        for (int i = 0; i < the_grid.myroads.size(); i++) {
            Random mynumber = new Random();
            int myvalue = mynumber.nextInt(the_grid.myroads.size());
            if (the_grid.myroads.get(myvalue).getDirection() == 'E') {
                the_grid.mycars.add(new Car(i, the_grid.myroads.get(myvalue).getStart_pos(), the_grid.myroads.get(myvalue).getDirection()));
                the_grid.mycars.get(i).Move();
            } else {
                the_grid.mycars.add(new Car(i, the_grid.myroads.get(myvalue).getEnd_pos(), the_grid.myroads.get(myvalue).getDirection()));
                the_grid.mycars.get(i).Move();
            }
        }
        //Adding the cars
        the_grid.drawGrid();
        backup myframe = new backup(the_grid);
        myframe.loadscreen();
        myframe.validate();
    }
}