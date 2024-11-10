package trafficsimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class maingrid extends JPanel implements Runnable {
    private String [][] mygrid_roads;

    public maingrid(String[][] mygrid_roads) {
        this.mygrid_roads = mygrid_roads;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.GREEN);
        for(int i =0;i<mygrid_roads.length;i++) {
            for(int j=0;j<mygrid_roads[i].length;j++) {
                System.out.println(this.mygrid_roads[i][j]);
                if(this.mygrid_roads[i][j]=="RD1") {
                g.setColor(Color.BLUE);
                g.fillRect(i * 100, j * 100, 50, 50); }
            }
        }
    }

    @Override
    public void run() {
        while(true) {
            this.repaint();
            this.repaint();
            try{
                Thread.sleep(1000);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}


public class gui extends JFrame{

    private String[][] mygrid_roads;
    private String[][] mygrid_vehicles;

    public gui(String[][] mygrid_roads, String[][] mygrid_vehicles) throws HeadlessException {
        this.mygrid_roads = mygrid_roads;
        this.mygrid_vehicles = mygrid_vehicles;
    }

    public JPanel getgridstuff() {
        JPanel gridpanel = new JPanel();
        gridpanel.setLayout(new CardLayout());
        gridpanel.setPreferredSize(new Dimension(500,500));
        gridpanel.add(new maingrid(this.mygrid_roads));
        return gridpanel;
    }

    public static boolean RIGHT_TO_LEFT = false;

    public void addComponentsToPane(Container pane) {

        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }

        JPanel modepanel = new JPanel();
        modepanel.setPreferredSize(new Dimension(150,50));
        JButton citymode = new JButton("City Mode");
        JButton simulator = new JButton("Simulator Mode");
        modepanel.add(citymode);
        modepanel.add(simulator);
        pane.add(modepanel, BorderLayout.PAGE_START);
        pane.repaint();

        //Make the center component big, since that's the
        //typical usage of BorderLayout.
        pane.add(getgridstuff(), BorderLayout.CENTER);

        JPanel sidepanel = new JPanel();
        sidepanel.setLayout(new FlowLayout());

        sidepanel.setPreferredSize(new Dimension(200,500));
        JButton start_simulation = new JButton("Start");
        start_simulation.setPreferredSize(new Dimension(200,200));
        start_simulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                getgridstuff();
            }
        });
        JButton stop_simulation = new JButton("Stop");
        stop_simulation.setPreferredSize(new Dimension(200,200));
        stop_simulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Stop Simulation
            }
        });
        sidepanel.add(start_simulation);
        sidepanel.add(stop_simulation);
        pane.add(sidepanel, BorderLayout.LINE_START);

        JPanel statspanel = new JPanel();
        JLabel statspanel1 = new JLabel("Hello World");
        statspanel.add(statspanel1);
        pane.add(statspanel, BorderLayout.PAGE_END);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("BorderLayoutDemo");
        frame.setSize(1920,1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
        //Use the content pane's default BorderLayout. No need for
        //setLayout(new BorderLayout());
        //Display the window.
        frame.pack();
        frame.setVisible(true);

        frame.revalidate();
        frame.repaint();
    }
}
