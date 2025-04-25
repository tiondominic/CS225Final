import java.awt.*;
import javax.swing.*;

public class Window {
    private JFrame frame;
    private JLabel counter;
    private GridLayout maingrid;
    private Gamestate gamestate;
    private JButton Cookie;
    private Upgrade upgrade;
    private int UpgradeCount;
    private JPanel upgradePanel;

    public Window(Gamestate gamestate){
        this.gamestate = gamestate;
        this.UpgradeCount = 10;

        frame = new JFrame("Cookie Realm");
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(500, 500));
        counter = new JLabel("Cookies: 0");
        maingrid = new GridLayout(3, 3);
        GridLayout UpgradeGrid = new GridLayout(UpgradeCount, 1);

        frame.add(new JLabel("Placeholder 1"), BorderLayout.WEST);

        JPanel panel2 = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(maingrid);
        upgradePanel = new JPanel(UpgradeGrid);
        Cookie = new JButton("CLICK");
        Cookie.setPreferredSize(new Dimension(50, 50));

        gridPanel.add(new JLabel()); // change this soon
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(Cookie);
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        

        Cookie.addActionListener(e -> {gamestate.Click();});

        panel2.add(counter, BorderLayout.NORTH);
        panel2.add(gridPanel, BorderLayout.CENTER);

        frame.add(panel2);


        frame.add(new JLabel("Placeholder 1"), BorderLayout.SOUTH);
        frame.add(upgradePanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public void update(double a){
        counter.setText("Cookies: " + (int)a + " | Current CPS: " + (int)gamestate.GetCPS());
    }

    public void addUpgrade(Upgrade upgrade){
        String name = upgrade.getName();
        JButton upgradeButton = new JButton(name);
        upgradeButton.addActionListener(e -> {upgrade.buy();});
        upgradePanel.add(upgradeButton);

    }
    

}
