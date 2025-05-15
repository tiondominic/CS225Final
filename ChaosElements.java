import java.awt.GridLayout;
import javax.swing.*;

public class ChaosElements {
    private final Gamestate gamestate;
    private JFrame GoldenCookieFrame;
    private JPanel Ccontainer;
    private JButton GoldenCookie;

    public ChaosElements(Gamestate gamestate){
        this.gamestate = gamestate;
    }

    public void setUp() {
        // Initialize components
        GoldenCookieFrame = new JFrame("Golden Cookie");
        Ccontainer = new JPanel(new GridLayout(1, 1));
        GoldenCookie = new JButton("GOLDEN");

        GoldenCookie.addActionListener(e ->{

             double randomAmount = 100000; // Math.random() * (gamestate.getAmount()/2); putting 100k for testing
            gamestate.goldenClick(randomAmount);
            GoldenCookieFrame.dispose();
        });

        Ccontainer.add(GoldenCookie);

        GoldenCookieFrame.add(Ccontainer);

        GoldenCookieFrame.setSize(300, 200);
        GoldenCookieFrame.setLocation(500, 300);
        GoldenCookieFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GoldenCookieFrame.setVisible(true);
    }
    
}
