import java.awt.*;
import javax.swing.*;

public class Window {
    private JFrame frame;
    private JLabel counter;
    private GridLayout maingrid;
    private Gamestate gamestate;
    private JButton Cookie;

    public Window(Gamestate gamestate){
        this.gamestate = gamestate;

        frame = new JFrame("Cookie Realm");
        frame.setLayout(new GridLayout(1,3));
        frame.setMinimumSize(new Dimension(500, 500));
        counter = new JLabel("Cookies: 0");
        maingrid = new GridLayout(1, 3);

        frame.add(new JLabel("Placeholder 1"));

        JPanel panel2 = new JPanel(new BorderLayout());
        Cookie = new JButton("CLICK");
        Cookie.setPreferredSize(new Dimension(50, 50));

        Cookie.addActionListener(e -> {gamestate.Click();});

        panel2.add(counter, BorderLayout.NORTH);
        panel2.add(Cookie, BorderLayout.CENTER);

        frame.add(panel2);


        frame.add(new JLabel("Placeholder 1"));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public void update(double a){
        counter.setText("Cookies: " + (int)a + " | Current CPS: " + (int)gamestate.GetCPS());
    }
    

}
