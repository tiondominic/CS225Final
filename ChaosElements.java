import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ChaosElements {

    private final Gamestate gamestate;
    private JFrame frame;
    private PopupCookieButton goldenCookieButton;
    private boolean exiting = false;
    private Timer spawnTimer;
    private Random random = new Random();

    // Change DEBUG to false to prevent automatic appearance during development
    private static final boolean DEBUG = false;

    public ChaosElements(Gamestate gamestate) {
        this.gamestate = gamestate;
    }

    public void setUp() {
        if (frame != null && frame.isVisible()) return;

        SwingUtilities.invokeLater(() -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int cookieSize = (int) (screenSize.height * 0.15);

            frame = new JFrame();
            frame.setUndecorated(true);
            frame.setSize(cookieSize, cookieSize);
            frame.setAlwaysOnTop(true);
            
            // Set random position on screen (with padding from edges)
            int padding = cookieSize;
            int x = random.nextInt(screenSize.width - 2 * padding) + padding;
            int y = random.nextInt(screenSize.height - 2 * padding) + padding;
            frame.setLocation(x, y);
            
            frame.setLayout(new BorderLayout());

            frame.setBackground(new Color(0, 0, 0, 0));
            frame.getContentPane().setBackground(new Color(0, 0, 0, 0));
            ((JComponent) frame.getContentPane()).setOpaque(false);

            goldenCookieButton = new PopupCookieButton("assets/goldencookie.png", () -> {
                double randomAmount = Math.random() * (gamestate.getAmount() / 2);
                gamestate.goldenClick(randomAmount);
                System.out.println("[GC DEBUG] Added Cookies: " + randomAmount + " Cookies");
                exit();
            });

            frame.add(goldenCookieButton, BorderLayout.CENTER);
            frame.setVisible(true);

            if (DEBUG) {
                goldenCookieButton.showWithAnimation();
            } else {
                // Always show the animation when not in debug mode
                goldenCookieButton.showWithAnimation();
            }
        });
    }

    public void exit() {
        if (frame != null && frame.isVisible() && !exiting) {
            exiting = true;
            goldenCookieButton.triggerFadeOut();
            Timer disposeTimer = new Timer(900, e -> {
                frame.setVisible(false);
                frame.dispose();
                exiting = false;
            });
            disposeTimer.setRepeats(false);
            disposeTimer.start();
        }
    }
}