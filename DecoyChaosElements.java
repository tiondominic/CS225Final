import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DecoyChaosElements {

    private final Gamestate gamestate;
    private JFrame frame;
    private PopupCookieButton decoyGoldenCookieButton;
    private boolean exiting = false;
    private Random random = new Random();

    public DecoyChaosElements(Gamestate gamestate) {
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

            // IMPORTANT: Uses the same golden cookie image to look identical
            decoyGoldenCookieButton = new PopupCookieButton("assets/goldencookie.png", () -> {
                // Calculate amount to deduct (10% to 25% of current cookies)
                double deductionPercentage = 0.1 + (Math.random() * 0.15); // 10% to 25%
                double deductionAmount = gamestate.getAmount() * deductionPercentage;
                
                // Minimum deduction of 100 cookies, maximum of current amount
                deductionAmount = Math.max(100, Math.min(deductionAmount, gamestate.getAmount()));
                
                gamestate.decoyClick(deductionAmount);
                gamestate.goldenClick(deductionAmount);
                String decoyText = "-" + (int) deductionAmount + " Cookies!";
                FloatingTextLabel floatLabel = new FloatingTextLabel(decoyText, Color.RED, 1400);
                floatLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
                frame.getLayeredPane().add(floatLabel, JLayeredPane.POPUP_LAYER);

                System.out.println("[DECOY GC DEBUG] Deducted Cookies: " + deductionAmount + " Cookies");
                
                exit();
            });

            frame.add(decoyGoldenCookieButton, BorderLayout.CENTER);
            frame.setVisible(true);

            // Always show the animation
            decoyGoldenCookieButton.showWithAnimation();
            
            System.out.println("[DECOY GC DEBUG] Decoy golden cookie spawned at (" + x + ", " + y + ")");
        });
    }

    public void exit() {
        if (frame != null && frame.isVisible() && !exiting) {
            exiting = true;
            decoyGoldenCookieButton.triggerFadeOut();
            Timer disposeTimer = new Timer(900, e -> {
                frame.setVisible(false);
                frame.dispose();
                exiting = false;
                System.out.println("[DECOY GC DEBUG] Decoy golden cookie disposed");
            });
            disposeTimer.setRepeats(false);
            disposeTimer.start();
        }
    }
}