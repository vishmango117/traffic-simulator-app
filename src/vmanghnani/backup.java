package vmanghnani;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

class mygrid extends JPanel {

    private String[][] mygrid_roads;
    private String[][] mygrid_vehicles;

    public mygrid(String[][] mygrid_roads, String[][] mygrid_vehicles) {
        this.mygrid_roads = mygrid_roads;
        this.mygrid_vehicles = mygrid_vehicles;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.GREEN);
        for(int i=0;i<mygrid_roads.length;i++) {
            for(int j=0;j<mygrid_roads[i].length;j++) {
                if(mygrid_roads[j][i]=="RD1") {
                    g.setColor(Color.BLUE);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                else if(mygrid_roads[j][i]=="TW1") {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                else if(mygrid_roads[j][i]=="FW1") {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
                else if(mygrid_roads[j][i]=="TL1") {
                    g.setColor(Color.RED);
                    g.fillRect(i * 20, j * 20, 20, 20);
                }
            }
        }
        for(int i=0;i<mygrid_vehicles.length;i++) {
            for (int j = 0; j < mygrid_vehicles[i].length; j++) {
                if(mygrid_vehicles[j][i]=="CR1") {
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
                Thread.sleep(1000);
                this.the_grid.updateCarPos(the_grid.mycars);
                this.the_grid.drawGrid();
                this.the_grid.printGrid();
                mygridpanel.validate();
                mygridpanel.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void createroad() {
        JPanel road_values = new JPanel();
        JLabel x_label = new JLabel("X:");
        JTextArea x_input = new JTextArea();
        JLabel y_label = new JLabel("Y: ");
        JTextArea y_input = new JTextArea();
        road_values.add(x_label);
        road_values.add(x_input);
        road_values.add(y_label);
        road_values.add(y_input);

        //the_grid.myroads.add();

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
        myframe.setSize(600,600);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Adding Grid
        JPanel mygridpanel = new mygrid(the_grid.mygrid_roads, the_grid.mygrid_vehicles);
        myframe.add(mygridpanel, BorderLayout.CENTER);
        //Adding MenuMode
        JPanel modepanel = new JPanel();
        modepanel.setPreferredSize(new Dimension(150,50));
        JButton citymode = new JButton("City Mode");
        citymode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createroad();
            }
        });
        JButton simulator = new JButton("Simulator");
        modepanel.add(citymode);
        modepanel.add(simulator);
        myframe.add(modepanel, BorderLayout.PAGE_START);
        //Window Settings
        //SIDEPANEL
        JPanel sidepanel = new JPanel();
        sidepanel.setLayout(new CardLayout());
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
                    the_grid.layout1();
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


        JPanel sidepanel2 = new JPanel();
        sidepanel2.setLayout(new FlowLayout());
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
        sidepanel1.add(start_simulator);
        sidepanel1.add(stop_simulator);
        sidepanel2.add(preset);
        sidepanel2.add(custom);
        sidepanel.add(sidepanel1);
        sidepanel.add(sidepanel2);
        myframe.add(sidepanel, BorderLayout.LINE_START);
        myframe.setVisible(true);
        myframe.revalidate();
        myframe.repaint();
    }
}
