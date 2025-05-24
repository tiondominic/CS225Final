import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FloatingTextLabel extends JLabel {

    private float opacity = 1f;
    private int yOffset = 0;
    private final Timer animationTimer;

    public FloatingTextLabel(String text, Color textColor, int durationMs) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, 16));
        setForeground(textColor); // Use the provided color
        setHorizontalAlignment(SwingConstants.CENTER);

        int steps = durationMs / 16;
        animationTimer = new Timer(16, new AbstractAction() {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                currentStep++;
                yOffset = - (int)(currentStep * 0.5);
                opacity = 1f - (float) currentStep / steps;
                repaint();
                if (currentStep >= steps) {
                    animationTimer.stop();
                    Container parent = getParent();
                    if (parent != null) parent.remove(FloatingTextLabel.this);
                    parent.repaint();
                }
            }
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.setColor(getForeground());
        g2d.setFont(getFont());

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + yOffset;

        g2d.drawString(getText(), x, y);
        g2d.dispose();
    }
}
