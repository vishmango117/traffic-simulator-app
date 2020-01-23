package vmanghnani;

import javax.swing.*;
import java.awt.*;

class fillgrid extends JPanel {

    public String[][] mygrid_roads;
    public String[][] mygrid_vehicles;

    public fillgrid(String[][] mygrid_roads, String[][] mygrid_vehicles) {
        this.mygrid_roads = mygrid_roads;
        this.mygrid_vehicles = mygrid_vehicles;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255,255,0,1));
        g2d.fillRect(20, 20, 300, 300);
    }
}

public class gui extends JFrame {

    private String[][] mygrid_roads;
    private String[][] mygrid_vehicles;

    public gui(String[][] mygrid_roads, String[][] mygrid_vehicles) throws HeadlessException {
        this.mygrid_roads = mygrid_roads;
        this.mygrid_vehicles = mygrid_vehicles;
    }

    public static boolean RIGHT_TO_LEFT = false;

    public void addComponentsToPane(Container pane) {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

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

        //Make the center component big, since that's the
        //typical usage of BorderLayout.
        JPanel gridpanel = new JPanel();
        gridpanel.setPreferredSize(new Dimension(500,500));
        gridpanel.setLayout(new CardLayout());
        gridpanel.add(new fillgrid(this.mygrid_roads, this.mygrid_vehicles));
        pane.add(gridpanel, BorderLayout.CENTER);

        JPanel sidepanel = new JPanel();
        sidepanel.setLayout(new FlowLayout());

        sidepanel.setPreferredSize(new Dimension(200,500));
        JButton start_simulation = new JButton("Start");
        start_simulation.setPreferredSize(new Dimension(200,200));
        JButton stop_simulation = new JButton("Stop");
        stop_simulation.setPreferredSize(new Dimension(200,200));
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
        frame.setSize(1366,768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
        //Use the content pane's default BorderLayout. No need for
        //setLayout(new BorderLayout());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
