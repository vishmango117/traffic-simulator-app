package trafficsimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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
                    g.fillRect(i * 15, j * 15, 15, 15);
                }
                else if(the_grid.mygrid_roads[j][i]=="TW1") {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 15, j * 15, 15, 15);
                }
                else if(the_grid.mygrid_roads[j][i]=="FW1") {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 15, j * 15, 15, 15);
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
                    g.fillRect(i * 15, j * 15, 15, 15);
                }
                else if(the_grid.mygrid_vehicles[j][i]=="CR1") {
                    g.setColor(Color.CYAN);
                    g.fillRect(i * 15, j * 15, 15, 15);
                }
            }
        }
    }
}

public class backup extends JFrame {

    public Grid the_grid;
    public static boolean state=true;
    public int counter = 0;
    Thread thread = new Thread();
    public backup(Grid the_grid) throws HeadlessException {
        this.the_grid = the_grid;
    }


    public void show_edit_grid(JFrame myframe) {

    }

    public void process_run(JFrame myframe, boolean state) {
         new Thread(() -> {
            while (state) {
                try {
                    Thread.sleep(500);
                    this.the_grid.drawGrid();
                    this.the_grid.printGrid();
                    this.the_grid.updateCarPos(the_grid.mycars);
                    myframe.validate();
                    myframe.repaint();
                    counter+=1;
                    if (counter % 5 == 0) {
                        for (TrafficLight currenttl : the_grid.mytrafficlights) {
                            currenttl.operate();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void autogeneratecars() {
        for (int i = 0; i < the_grid.myroads.size(); i++) {
            Random mynumber = new Random();
            int myvalue = mynumber.nextInt(the_grid.myroads.size());
            if (the_grid.myroads.get(myvalue).getDirection() == 'E') {
                the_grid.mycars.add(new trafficsimulator.Car(i, the_grid.myroads.get(myvalue).getStart_pos(), the_grid.myroads.get(myvalue).getDirection()));
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
        myframe.setSize(1366,768);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //GRIDPANEL1
        JPanel mygridpanel1 = new mygrid(the_grid);
        mygridpanel1.setLayout(new FlowLayout());
        mygridpanel1.setPreferredSize(new Dimension(700,700));

        //GRIDPANEL2
        JPanel mygridpanel2 = new mygrid(the_grid);
        mygridpanel2.setLayout(new FlowLayout());
        mygridpanel2.setPreferredSize(new Dimension(700,700));

        //GRIDPANEL
        JPanel mygridpanel = new mygrid(the_grid);
        CardLayout mygridlayout = new CardLayout();
        mygridpanel.setLayout(mygridlayout);
        mygridpanel.setPreferredSize(new Dimension(700,700));
        mygridpanel.add(mygridpanel1, "simulation_grid");
        mygridpanel.add(mygridpanel2, "editor_grid");
        myframe.add(mygridpanel, BorderLayout.CENTER);
        myframe.validate();
        myframe.repaint();

        //MODEPANEL
        JPanel modepanel = new JPanel();
        modepanel.setPreferredSize(new Dimension(150,50));
        JButton citymode = new JButton("City Mode");
        JButton simulator = new JButton("Simulator");
        modepanel.add(citymode);
        modepanel.add(simulator);
        myframe.add(modepanel, BorderLayout.PAGE_START);
        myframe.validate();
        myframe.repaint();
        //Window Settings
        //SIDEPANEL
        JPanel sidepanel = new JPanel();
        CardLayout sidepanelcard = new CardLayout();

        sidepanel.setLayout(sidepanelcard);
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
                    process_run(myframe, true);
            }
        });
        JButton stop_simulator = new JButton("Stop");
        stop_simulator.setPreferredSize(new Dimension(100,100));
        stop_simulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                thread.stop();
            }
        });

        //SIDEPANEL2
        JPanel sidepanel2 = new JPanel(new FlowLayout());
        JButton layout1 = new JButton("Layout1");
        JButton layout2 = new JButton("Layout2");
        JButton layout3 = new JButton("Layout3");
        layout1.setPreferredSize(new Dimension(100,100));
        CardLayout layoutscard = new CardLayout();
        JButton custom = new JButton("Custom");
        custom.setPreferredSize(new Dimension(100,100));
        layout1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                the_grid.layout1();
                sidepanel.validate();
                sidepanel.repaint();
            }
        });
        layout2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                the_grid.layout1();
                sidepanel.validate();
                sidepanel.repaint();
            }
        });
        custom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                the_grid.create_road();
                sidepanel.repaint();
            }
        });
        JButton add_road = new JButton("Add Road");
        JButton add_intersection = new JButton("Add Intersection");
        add_road.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                the_grid.create_road();
                sidepanel.repaint();
            }
        });
        add_intersection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                the_grid.create_intersection();
                sidepanel.repaint();
            }
        });
        sidepanel1.add(start_simulator);
        sidepanel1.add(stop_simulator);
        sidepanel2.add(layout1);
        sidepanel2.add(layout2);
        sidepanel2.add(layout3);
        sidepanel2.add(add_road);
        sidepanel2.add(add_intersection);
        sidepanel.add(sidepanel1,"s1");
        sidepanel.add(sidepanel2, "s2");
        myframe.validate();
        myframe.repaint();

        citymode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sidepanelcard.show(sidepanel, "s1");
                mygridlayout.show(mygridpanel, "editor_grid");
                myframe.validate();
                myframe.repaint();
            }
        });
        simulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sidepanelcard.show(sidepanel, "s2");
                mygridlayout.show(mygridpanel,"simulation_grid");
                sidepanel.validate();
                sidepanel.repaint();
            }
        });
        myframe.add(sidepanel, BorderLayout.LINE_START);
        myframe.revalidate();
        myframe.repaint();
        myframe.setVisible(true);
    }
}
