package vmanghnani;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Grid {
    private String[][] mygrid;
    private int height;
    private int width;
    public ArrayList<Road> myroads;
    public ArrayList<Car> mycars;
    public ArrayList<TrafficLight> mytrafficlights;
    public ArrayList<Straight> my_straight_intersections;
    public ArrayList<ThreeWay> my_threeway_intersections;
    public ArrayList<FourWay> my_fourway_intersections;

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
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                this.mygrid[i][j] = "   ";
            }
        }
        //Constructing Roads
        for (Road currentroad : this.myroads) {
            for (int i = currentroad.getStart_pos().getY_Pos(); i <= currentroad.getEnd_pos().getY_Pos(); i++) {
                for (int j = currentroad.getStart_pos().getX_Pos(); j <= currentroad.getEnd_pos().getX_Pos(); j++) {
                    this.mygrid[i][j] = "RD1";
                }
            }
        }
        //Adding Intersections
        for (Straight current_intersections : my_straight_intersections) {
            this.mygrid[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "ST1";
        }
        //Adding Three Way Intersections
        for(ThreeWay current_intersections: my_threeway_intersections) {
            this.mygrid[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "TW1";
        }

        //Adding Cars
        for (Car currentcar : this.mycars) {
            this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] = "CR1";

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
    }

    public boolean checkIntersection() {
        for (Car currentcar : mycars) {
            if (this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "ST1" ||
                    this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "TW1" ||
                    this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "FW1") {
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
            //when car is on road
            if (this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("RD1")) {
                currentcar.setCar_pos(next_pos);
            }
            // when cars are reaching near the intersection
            else if (this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("ST1") ||
                    this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("TW1") ||
                    this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("FW1")) {
                if (checkIntersection()) {
                    currentcar.setCar_pos(next_pos);
                }
            }
            //when car is inside the straight intersection
            else if(this.mygrid[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("ST1")) {
                for (Straight current_int : this.my_straight_intersections) {
                    if (current_int.getInt_road().equals(currentcar.getCar_pos())) {
                        currentcar.setCar_pos(current_int.getNext_road().getStart_pos());
                    }
                }
            }
            //when car is inside the three-way intersection
            else if(this.mygrid[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("TW1")) {
                System.out.println("Yes");
                for (ThreeWay currentint: this.my_threeway_intersections) {
                    if(currentint.getInt_road().equals(currentcar.getCar_pos())) {
                        Random rand = new Random();
                        int mydirection = rand.nextInt(2);
                        currentcar.setDirection(currentint.getNext_position().get(1).getDirection());
                        currentcar.setCar_pos(currentint.getNext_position().get(1).getStart_pos());
                    }
                }
            }
//            //when car is inside the fourway intersection
//            else if(this.mygrid[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("FW1")) {
//                for (FourWay currentint: this.my_fourway_intersections) {
//                    if(currentint.getInt_road().equals(currentcar.getCar_pos())) {
//                        Random rand = new Random();
//                        currentcar.setCar_pos(currentint.getNext_position().get(rand.nextInt(3)).getStart_pos());
//                    }
//                }
//            }
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

        //Adding Roads
        the_grid.myroads.add(new Road(1, new Coordinates(10,2), new Coordinates(17, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(19,2), new Coordinates(28, 2),'E'));
        the_grid.myroads.add(new Road(3, new Coordinates(18,10), new Coordinates(18, 20),'S'));
        the_grid.myroads.add(new Road(4, new Coordinates(18,22), new Coordinates(18, 26),'S'));
        the_grid.myroads.add(new Road(5, new Coordinates(18,3), new Coordinates(18, 9),'S'));
        //Adding the intersection
        Straight intersection2 = new Straight(new Road(200,new Coordinates(18,21), new Coordinates(18,21),'N'), the_grid.myroads.get(3));
        the_grid.my_straight_intersections.add(intersection2);
        //the_grid.mytrafficlights.add(0,new TrafficLight("G", Coordinates.add_coordinate(the_grid.straight_intersections.get(0).getCurrent_position(), new Coordinates(-1,-1))));

        ThreeWay intersection3 = new ThreeWay(new Road(250, new Coordinates(18,2), new Coordinates(18,2),'S'), new ArrayList<Road>());
        intersection3.getNext_position().add(the_grid.myroads.get(1));
        intersection3.getNext_position().add(the_grid.myroads.get(4));
        the_grid.my_threeway_intersections.add(intersection3);
        //Adding the cars
        the_grid.mycars.add(new Car(1, the_grid.myroads.get(0).getStart_pos(), the_grid.myroads.get(0).getDirection()));
        //the_grid.mycars.add(new Car(2, the_grid.myroads.get(2).getStart_pos(), the_grid.myroads.get(2).getDirection()));
        the_grid.mycars.get(0).Move();
        //the_grid.mycars.get(1).Move();


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
