import java.awt.*;
import javax.swing.*;

public class Window {
    private final JFrame frame;
    private final JLabel counter;
    private final GridLayout maingrid;
    private final Gamestate gamestate;
    private final JButton Cookie;
    private final int UpgradeCount;
    private final JPanel upgradePanel;

    public Window(Gamestate gamestate){
        this.gamestate = gamestate;
        this.UpgradeCount = 10;

        frame = new JFrame("Cookie Realm"); // organize later
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(500, 700));
        counter = new JLabel("Cookies: 0");
        maingrid = new GridLayout(3, 3);
        GridLayout UpgradeGrid = new GridLayout(UpgradeCount, 1);

        frame.add(new JLabel("Placeholder 1"), BorderLayout.WEST);

        JPanel panel2 = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(maingrid);
        upgradePanel = new JPanel(UpgradeGrid);
        Cookie = new JButton("CLICK");

        JPanel nullPanel = new JPanel(null);
        Cookie.setBounds(0, 0, 150, 150);
        nullPanel.add(Cookie);

        gridPanel.add(new JLabel()); // change this soon
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(nullPanel); //
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        gridPanel.add(new JLabel());
        

        Cookie.addActionListener(e -> {gamestate.Click();});

        frame.add(counter, BorderLayout.NORTH);
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
        
        JButton upgradeButton = new JButton("<html>" + name + "<br>Owned: " + upgrade.getOwned() +  "<br>Cost: " + upgrade.getCost() + "</html>");
        upgradeButton.setPreferredSize(new Dimension(100, 200));
        upgradeButton.addActionListener(e -> {
            if (gamestate.tryBuyUpgrade(upgrade)) {
                upgradeButton.setText("<html>" + name + "<br>Owned: " + upgrade.getOwned() +
                    "<br>Cost: " + String.format("%.2f", upgrade.getCost()) + "</html>");
            }
        });

        upgradePanel.add(upgradeButton);

    }
    

}
