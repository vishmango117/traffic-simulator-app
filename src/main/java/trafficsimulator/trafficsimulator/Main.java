package trafficsimulator;


import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Grid {
    public String[][] mygrid_roads;
    public String[][] mygrid_vehicles;
    private int height;
    private int width;
    public ArrayList<trafficsimulator.Road> myroads;
    public ArrayList<trafficsimulator.Car> mycars;
    public ArrayList<trafficsimulator.TrafficLight> mytrafficlights;
    public ArrayList<trafficsimulator.Straight> my_straight_intersections;
    public ArrayList<Threeway> my_threeway_intersections;
    public ArrayList<trafficsimulator.Fourway> my_fourway_intersections;

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
        for (trafficsimulator.Road currentroad : this.myroads) {
            for (int i = currentroad.getStart_pos().getY_Pos(); i <=currentroad.getEnd_pos().getY_Pos(); i++) {
                for (int j = currentroad.getStart_pos().getX_Pos(); j<=currentroad.getEnd_pos().getX_Pos(); j++) {
                    this.mygrid_roads[i][j] = "RD1";
                }
            }
            if(currentroad.getStart_pos().getY_Pos() == 0 || currentroad.getStart_pos().getY_Pos() == 49) {
                System.out.println("yes");
                this.mygrid_roads[currentroad.getStart_pos().getY_Pos()][currentroad.getStart_pos().getX_Pos()] = "SP1";
            }
            if(currentroad.getEnd_pos().getX_Pos() == 0 || currentroad.getStart_pos().getX_Pos() == 49) {
                this.mygrid_roads[currentroad.getStart_pos().getY_Pos()][currentroad.getStart_pos().getX_Pos()] = "SP1";
            }
        }
        //Adding Intersections
        for (trafficsimulator.Straight current_intersections : my_straight_intersections) {
            this.mygrid_roads[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "ST1";
        }
        //Adding Three Way Intersections
        for(Threeway current_intersections: my_threeway_intersections) {
            this.mygrid_roads[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "TW1";
        }

        //Adding Three Way Intersections
        for(trafficsimulator.Fourway current_intersections: my_fourway_intersections) {
            this.mygrid_roads[current_intersections.getInt_road().getStart_pos().getY_Pos()][current_intersections.getInt_road().getStart_pos().getX_Pos()] = "FW1";
        }

        //Adding Cars
        for (trafficsimulator.Car currentcar : this.mycars) {
            this.mygrid_vehicles[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] = "CR1";

        }
        for(trafficsimulator.TrafficLight current_tl: mytrafficlights) {
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
        for (trafficsimulator.Car currentcar : mycars) {
            if (this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "ST1" ||
                    this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "TW1" ||
                    this.mygrid_roads[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()] == "FW1") {
                return false;
            }
        }
        return true;
    }

    public void updateTrafficLight() {
        for (trafficsimulator.TrafficLight currentTrafficLight : mytrafficlights) {
            currentTrafficLight.operate();
        }
    }

    public boolean checkTrafficLight(trafficsimulator.Car currentcar) {
        trafficsimulator.Coordinates mytlposition = new trafficsimulator.Coordinates();
        trafficsimulator.TrafficLight mytrafficlight = new trafficsimulator.TrafficLight();
        if(currentcar.getDirection() == 'E') {
            mytlposition = new trafficsimulator.Coordinates(currentcar.getCar_pos().getX_Pos(), currentcar.getCar_pos().getY_Pos()-1);
        }
        else if(currentcar.getDirection() == 'W') {
            mytlposition = new trafficsimulator.Coordinates(currentcar.getCar_pos().getX_Pos(), currentcar.getCar_pos().getY_Pos()+1);
        }
        else if(currentcar.getDirection() == 'N') {
            mytlposition = new trafficsimulator.Coordinates(currentcar.getCar_pos().getX_Pos()-1, currentcar.getCar_pos().getY_Pos()-1);
        }
        else if(currentcar.getDirection() == 'S') {
            mytlposition = new trafficsimulator.Coordinates(currentcar.getCar_pos().getX_Pos()+1, currentcar.getCar_pos().getY_Pos());
        }
        for(trafficsimulator.TrafficLight current_tl: mytrafficlights) {
            if(current_tl.getPosition().getY_Pos() == mytlposition.getY_Pos() &&
                    current_tl.getPosition().getX_Pos() == mytlposition.getX_Pos()) {
                mytrafficlight = current_tl;
            }
        }
        if(mytrafficlight.getColor() == "G") {
            return true;
        }
        return false;
    }


    public void updateCarPos(ArrayList<trafficsimulator.Car> mycars) {
        for (Car currentcar : mycars) {
            trafficsimulator.Coordinates current_pos = currentcar.getCar_pos();
            trafficsimulator.Coordinates next_pos = trafficsimulator.Coordinates.add_coordinate(current_pos, current_pos.direction_to_coords(currentcar.getDirection()));
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
//            else if( this.mygrid_roads[current_pos.getY_Pos()][current_pos.getX_Pos()].equals("SP1") &&
//                    this.mygrid_roads[next_pos.getY_Pos()][next_pos.getX_Pos()].equals("   ")) {
//                mycars.remove(currentcar);
//            }
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
                trafficsimulator.Fourway myintersection = new trafficsimulator.Fourway();
                for(trafficsimulator.Fourway current_int: this.my_fourway_intersections) {
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



    public void create_road() {
        JTextField road_startpos = new JTextField(10);
        JTextField road_endpos = new JTextField(10);
        JTextField road_direction = new JTextField(10);
        JTextField road_size = new JTextField(10);
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Road Start Pos (X-Value)"));
        myPanel.add(road_startpos);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Road Start Pos (Y-Value)"));
        myPanel.add(road_endpos);
        myPanel.add(new JSeparator());
        myPanel.add(new JLabel("Direction"));
        myPanel.add(road_direction);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Size"));
        myPanel.add(road_size);


        while (true) {
            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Please Enter Road Length and Direction", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                System.out.println("Road Start Position(X): " + Integer.parseInt(road_startpos.getText()));
                System.out.println("Road Start Position(Y):  " + Integer.parseInt(road_endpos.getText()));
                System.out.println("Road Direction: " + (road_direction.getText()));
            }
            if (road_direction.getText().equals("N") || road_direction.getText().equals("S") ||
                    road_direction.getText().equals("W") || road_direction.getText().equals("E")) {
                break;
            }
        }
        int x_value = Integer.parseInt(road_startpos.getText());
        int y_value = Integer.parseInt(road_endpos.getText());
        int size = Integer.parseInt(road_size.getText());
        char direction = road_direction.getText().charAt(0);
        if(direction == 'N') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value,y_value-size), new Coordinates(x_value, y_value), road_direction.getText().charAt(0)));
            System.out.println("Road Info"+this.myroads.get(0).getStart_pos().getX_Pos()+","+this.myroads.get(0).getStart_pos().getY_Pos());
        }
        else if(direction == 'S') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value, y_value), new Coordinates(x_value, y_value+size), road_direction.getText().charAt(0)));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        }
        else if(direction == 'E') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value, y_value), new Coordinates(x_value+size, y_value), road_direction.getText().charAt(0)));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        }
        if(direction == 'W') {
            this.myroads.add(new Road(this.myroads.size(),
                    new Coordinates(x_value-size, y_value), new Coordinates(x_value, y_value), road_direction.getText().charAt(0)));
            System.out.println("Road Info" + this.myroads.get(0).getStart_pos().getX_Pos() + "," + this.myroads.get(0).getStart_pos().getY_Pos());
        }

    }
    public void create_intersection() {
        char[] coords = new char[] {'N', 'S', 'E', 'W'};
        JTextField intersection_xvalue = new JTextField(10);
        JTextField intersection_yvalue = new JTextField(10);
        JTextField type = new JTextField(10);
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Road Start Pos (X-Value)"));
        myPanel.add(intersection_xvalue);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Road Start Pos (Y-Value)"));
        myPanel.add(intersection_yvalue);
        myPanel.add(new JSeparator());
        myPanel.add(new JLabel("Type"));
        myPanel.add(type);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer

        while (true) {
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Road Length and Direction", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println("Road Start Position(X): " + Integer.parseInt(intersection_xvalue.getText()));
            System.out.println("Road Start Position(Y):  " + Integer.parseInt(intersection_yvalue.getText()));
            System.out.println("Int type: " + (type.getText()));
        }
        if (type.getText().equals("three-way") || type.getText().equals("four-way")) {
            break;
        }
        if(result == JOptionPane.CANCEL_OPTION) {
            break;
        }
    }
    int x_value = Integer.parseInt(intersection_xvalue.getText());
    int y_value = Integer.parseInt(intersection_yvalue.getText());
    Threeway myintersection = new Threeway();
    Coordinates intersection_pos = new Coordinates(x_value, y_value);
    for(int i =0;i<coords.length;i++) {
        Coordinates next_pos = Coordinates.add_coordinate(intersection_pos, intersection_pos.direction_to_coords(coords[i]));
        for(Road current_road: this.myroads) {
            if((current_road.getStart_pos().getX_Pos() == next_pos.getX_Pos() || current_road.getEnd_pos().getX_Pos() == next_pos.getX_Pos())
            && (current_road.getStart_pos().getY_Pos() == next_pos.getY_Pos() || current_road.getEnd_pos().getY_Pos() == next_pos.getY_Pos())) {
                myintersection = new Threeway(new Road(140, intersection_pos, intersection_pos, 'E'), new ArrayList<>());
                myintersection.getNext_position().add(current_road);
            }
        }
        this.my_threeway_intersections.add(myintersection);
    }

}

    public void layout1() {
        //Adding Roads for threeway
        this.myroads.add(new Road(1, new Coordinates(0, 2), new Coordinates(17, 2), 'E'));
        this.myroads.add(new Road(2, new Coordinates(19, 2), new Coordinates(49, 2), 'E'));
        this.myroads.add(new Road(3, new Coordinates(18, 3), new Coordinates(18, 24), 'S'));

        //Adding Roads for fourway
        this.myroads.add(new Road(5, new Coordinates(18, 26), new Coordinates(18, 49), 'S'));
        this.myroads.add(new Road(6, new Coordinates(19, 25), new Coordinates(49, 25), 'E'));
        this.myroads.add(new Road(7, new Coordinates(0, 25), new Coordinates(17, 25), 'E'));

        //Adding 3Way
        Threeway intersection3 = new Threeway(new Road(250, new Coordinates(18,2), new Coordinates(18,2),'E'), new ArrayList<Road>());
        intersection3.getNext_position().add(this.myroads.get(1));
        intersection3.getNext_position().add(this.myroads.get(2));
        this.my_threeway_intersections.add(intersection3);
        //Eastbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,-1))));
        //Northbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,-1))));
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
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,1))));
        //Westbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,1))));
        //Northbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,-1))));

    }

    public void layout2() {
        //Adding Roads for threeway
        this.myroads.add(new Road(1, new Coordinates(0, 2), new Coordinates(17, 2), 'E'));
        this.myroads.add(new Road(2, new Coordinates(19, 2), new Coordinates(49, 2), 'E'));
        this.myroads.add(new Road(3, new Coordinates(18, 3), new Coordinates(18, 24), 'S'));

        //Adding Roads for fourway
        this.myroads.add(new Road(5, new Coordinates(18, 26), new Coordinates(18, 49), 'S'));
        this.myroads.add(new Road(6, new Coordinates(19, 25), new Coordinates(49, 25), 'E'));
        this.myroads.add(new Road(7, new Coordinates(0, 25), new Coordinates(17, 25), 'E'));

        //Adding 3Way
        Fourway intersection3 = new Fourway(new Road(250, new Coordinates(18,2), new Coordinates(18,2),'E'), new ArrayList<Road>());
        intersection3.getNext_position().add(this.myroads.get(1));
        intersection3.getNext_position().add(this.myroads.get(2));
        intersection3.getNext_position().add(this.myroads.get(4));
        this.my_fourway_intersections.add(intersection3);
        //Eastbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,-1))));
        //Northbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(this.my_threeway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,-1))));
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
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,1))));
        //Westbound
        this.mytrafficlights.add(new TrafficLight("R", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(-1,1))));
        //Northbound
        this.mytrafficlights.add(new TrafficLight("G", Coordinates.add_coordinate(this.my_fourway_intersections.get(0).getInt_road().getStart_pos(),new Coordinates(1,-1))));

    }
}


public class Main {


    public static void main(String[] args) throws IOException {

        //Initialising the grid
        Grid the_grid = new Grid(51, 51);
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
                the_grid.mycars.add(new trafficsimulator.Car(i, the_grid.myroads.get(myvalue).getStart_pos(), the_grid.myroads.get(myvalue).getDirection()));
                the_grid.mycars.get(i).Move();
            } else {
                the_grid.mycars.add(new trafficsimulator.Car(i, the_grid.myroads.get(myvalue).getEnd_pos(), the_grid.myroads.get(myvalue).getDirection()));
                the_grid.mycars.get(i).Move();
            }
        }
        //Adding the cars
        the_grid.drawGrid();
        trafficsimulator.backup myframe = new backup(the_grid);
        myframe.loadscreen();
        myframe.validate();
    }
}