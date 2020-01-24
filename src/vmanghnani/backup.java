package vmanghnani;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.Flow;

class mygrid extends JPanel {

    private Grid the_grid;

    public mygrid(Grid the_grid) {
        this.the_grid = the_grid;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(0,100,0 ,1 ));
        for(int i=0;i<the_grid.mygrid_roads.length;i++) {
            for(int j=0;j<the_grid.mygrid_roads[i].length;j++) {
                if(the_grid.mygrid_roads[j][i]=="RD1") {
                    g.setColor(Color.BLUE);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                else if(the_grid.mygrid_roads[j][i]=="TW1") {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                else if(the_grid.mygrid_roads[j][i]=="FW1") {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                }
            }
        for(int i=0;i<the_grid.mygrid_vehicles.length;i++) {
            for (int j = 0; j <the_grid.mygrid_vehicles[i].length; j++) {
                  if(the_grid.mygrid_roads[j][i]=="TL1") {
                    for(TrafficLight currenttl: this.the_grid.mytrafficlights) {
                        if(currenttl.getPosition().getY_Pos() == j &&
                                currenttl.getPosition().getX_Pos() == i) {
                            if(currenttl.getColor()=="G") {
                                g.setColor(Color.GREEN);
                            }
                            else {
                                g.setColor(Color.RED);
                            }
                        }
                    }
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                else if(the_grid.mygrid_vehicles[j][i]=="CR1") {
                    g.setColor(Color.CYAN);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
            }
        }
    }
}

public class backup extends JFrame {

    public Grid the_grid;

    public backup(Grid the_grid) throws HeadlessException {
        this.the_grid = the_grid;
    }

    public void run_process(JPanel mygridpanel, boolean state) {
        Thread thread = new Thread(() -> {
            while(state) {
            try {
                int counter =0;
                Thread.sleep(500);
                this.the_grid.updateCarPos(the_grid.mycars);
                this.the_grid.drawGrid();
                this.the_grid.printGrid();
                mygridpanel.validate();
                mygridpanel.repaint();
                counter++;
                if(counter%10==0) {
                    for (TrafficLight currenttl : the_grid.mytrafficlights) {
                        currenttl.operate();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void createroad() {
        int x_value = Integer.parseInt(JOptionPane.showInputDialog("Input X"));
        int y_value = Integer.parseInt(JOptionPane.showInputDialog("Input Y"));
        int size = Integer.parseInt(JOptionPane.showInputDialog("Input Y"));
        String orientation = JOptionPane.showInputDialog("Orientation: ");
        //char direction = JOptionPane.showInputDialog("Input Direction");

        if(orientation=="vertical") {
        the_grid.myroads.add(new Road(the_grid.myroads.size(), new Coordinates(x_value,y_value), new Coordinates(x_value,y_value+size),(char) direction));
        }
        else
        {
            the_grid.myroads.add(new Road(the_grid.myroads.size(), new Coordinates(x_value,y_value), new Coordinates(x_value,y_value+size), (char) direction);
        }
    }


    public void autogeneratecars() {
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
    }

    public void loadscreen() {
        JFrame myframe = new JFrame("Traffic Car Simulator");
        myframe.setLayout(new BorderLayout());
        myframe.setSize(1920,1080);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Adding Grid
        JPanel mygridpanel = new mygrid(the_grid);
        mygridpanel.setLayout(new FlowLayout());
        mygridpanel.setPreferredSize(new Dimension(1366,768));
        myframe.add(mygridpanel, BorderLayout.CENTER);
        //Adding MenuMode
        JPanel modepanel = new JPanel();
        modepanel.setPreferredSize(new Dimension(150,50));
        JButton citymode = new JButton("City Mode");
        JButton simulator = new JButton("Simulator");
        modepanel.add(citymode);
        modepanel.add(simulator);
        myframe.add(modepanel, BorderLayout.PAGE_START);
        //Window Settings
        //SIDEPANEL
        JPanel sidepanel = new JPanel();
        CardLayout mycard1 = new CardLayout();
        sidepanel.setLayout(mycard1);
        sidepanel.setPreferredSize(new Dimension(100,500));
        JPanel sidepanel1 = new JPanel();
        //SIDEPANEL1
        sidepanel1.setLayout(new FlowLayout());
        JButton start_simulator = new JButton("Start");
        //SIDEPANEL1's BUTTONS
        start_simulator.setPreferredSize(new Dimension(100,100));
        start_simulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                    autogeneratecars();
                    run_process(mygridpanel, true);
            }
        });
        JButton stop_simulator = new JButton("Stop");
        stop_simulator.setPreferredSize(new Dimension(100,100));
        stop_simulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                run_process(mygridpanel, false);
            }
        });

        //SIDEPANEL2
        JPanel sidepanel2 = new JPanel(new FlowLayout());
        JButton preset = new JButton("Preset");
        preset.setPreferredSize(new Dimension(100,100));
        JButton custom = new JButton("Custom");
        custom.setPreferredSize(new Dimension(100,100));
        preset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                the_grid.layout1();
            }
        });
        custom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createroad();
            }
        });
        JPanel sidepanel3 = new JPanel(new FlowLayout());
        JButton add_road = new JButton("Add Road");
        JButton add_intersection = new JButton("Add Intersection");
        sidepanel1.add(start_simulator);
        sidepanel1.add(stop_simulator);
        sidepanel2.add(preset);
        sidepanel2.add(custom);
        sidepanel3.add(add_road);
        sidepanel3.add(add_intersection);
        sidepanel.add(sidepanel1,"s1");
        sidepanel.add(sidepanel2, "s2");
        sidepanel.add(sidepanel3,"s3");

        citymode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mycard1.show(sidepanel, "s1");
            }
        });
        simulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mycard1.show(sidepanel, "s2");
            }
        });
        myframe.add(sidepanel, BorderLayout.LINE_START);
        myframe.setVisible(true);
        myframe.revalidate();
        myframe.repaint();
    }
}
