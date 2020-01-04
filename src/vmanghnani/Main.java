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
                System.out.println("i: "+i);
                for (int j =currentroad.getStart_pos().getX_Pos(); j<=currentroad.getEnd_pos().getX_Pos(); j++) {
                    System.out.println("j: "+j);
                    this.mygrid[i][j] = "R1";
                }
            }
        }
        //Adding Cars
        for (Car currentcar : mycars) {
            this.mygrid[currentcar.getCar_pos().getY_Pos()][currentcar.getCar_pos().getX_Pos()]="C1";

        }
        //Adding Intersections
        /*for (Intersection currentintersection: mystraight_intersections) {
            this.mygrid[currentintersection.getPosition().getY_Pos()][currentintersection.getPosition().getX_Pos()]="I1";
        }*/
    }

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
                if(this.mygrid[i][j].equals("TB") || this.mygrid[i][j].equals("BB"))
                    System.out.printf("-----");
                else if(this.mygrid[i][j].equals("SB")){
                    System.out.printf("|");
                }
                else if(this.mygrid[i][j].startsWith("R")) {
                    System.out.printf("|---|");
                }
                else if(this.mygrid[i][j].startsWith("C")) {
                    System.out.printf("|-C-|");
                }
                else {
                    System.out.printf("|   |");
                }
            }
            System.out.println();
        }
    }

    public void updateCarPos(ArrayList<Car> mycars) {
        for(Car currentcar: mycars) {
            Coordinates current_pos = currentcar.getCar_pos();
            Coordinates next_pos = Coordinates.add_coordinate(current_pos, current_pos.direction_to_coords(currentcar.getDirection()));
            if(this.mygrid[next_pos.getY_Pos()][next_pos.getX_Pos()] == "R1") {
                currentcar.setCar_pos(next_pos);
            }
            }
        }
}

public class Main {
    public static void main(String[] args) throws IOException {
        Grid the_grid = new Grid(30,30);

        the_grid.myroads.add(new Road(1, new Coordinates(10,2), new Coordinates(17, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(18,2), new Coordinates(28, 2),'E'));
        the_grid.myroads.add(new Road(2, new Coordinates(15,10), new Coordinates(15, 25),'S'));
        //the_grid.myroads.add(new Road(3, new Coordinates(16,10), new Coordinates(28, 10),'E'));

        the_grid.mycars.add(new Car(1, the_grid.myroads.get(0).getStart_pos(), 'E'));
        the_grid.mycars.add(new Car(2, the_grid.myroads.get(2).getStart_pos(), the_grid.myroads.get(1).getDirection()));
        the_grid.mycars.get(0).Move();
        the_grid.mycars.get(1).Move();
        try {
            for(int i=0;i<16;i++) {
                the_grid.updateCarPos(the_grid.mycars);
                the_grid.drawGrid();
                the_grid.printGrid();
            }
        }
        catch(NullPointerException e) {
            System.out.println("LUL");
        }
        the_grid.mycars.get(0).Stop();
//        the_grid.mycars.get(1).Stop();
    }

}
