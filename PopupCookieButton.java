import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PopupCookieButton extends JButton {

    private final Image image;
    private float opacity = 0f;
    private float scale = 0f;
    private boolean showing = false;
    private final Runnable onClickAction;
    private Timer fadeOutTimer;
    private Timer idleAnimationTimer;

    private float idleAngle = 0f;
    private float idleScaleOffset = 0f;
    private long idleStartTime;

    private static final int ANIMATION_DELAY = 16;
    private static final int ANIMATION_DURATION = 800;

    public PopupCookieButton(String imagePath, Runnable onClickAction) {
        this.image = new ImageIcon(imagePath).getImage();
        this.onClickAction = onClickAction;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        addActionListener(e -> {
            if (showing && opacity >= 0.9f) {
                triggerFadeOut();
                onClickAction.run();
            }
        });

        // Idle animation
        idleStartTime = System.currentTimeMillis();
        idleAnimationTimer = new Timer(ANIMATION_DELAY, e -> {
            long time = System.currentTimeMillis() - idleStartTime;
            double radians = (time % 2000) / 2000.0 * 2 * Math.PI;

            // Oscillate rotation ±3 degrees and scale ±2.5%
            idleAngle = (float) Math.sin(radians) * 3f;
            idleScaleOffset = (float) (Math.sin(radians * 2) * 0.025f);

            if (showing) repaint();
        });
        idleAnimationTimer.start();
    }

    public void showWithAnimation() {
        if (showing) return;
        showing = true;
        opacity = 0f;
        scale = 0f;

        final long startTime = System.currentTimeMillis();

        Timer animationTimer = new Timer(ANIMATION_DELAY, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float t = Math.min(1.0f, elapsed / (float) ANIMATION_DURATION);

            scale = bounceOut(t);
            opacity = t;

            repaint();

            if (t >= 1.0f) ((Timer) e.getSource()).stop();
        });
        animationTimer.start();
    }

    public void triggerFadeOut() {
        if (!showing) return;

        if (fadeOutTimer != null && fadeOutTimer.isRunning()) {
            fadeOutTimer.stop();
        }

        final float startOpacity = opacity;
        final long startTime = System.currentTimeMillis();
        fadeOutTimer = new Timer(ANIMATION_DELAY, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float t = Math.min(1.0f, elapsed / (float) ANIMATION_DURATION);
            opacity = startOpacity * (1 - t);
            scale = 1 - 0.2f * t;
            repaint();

            if (t >= 1.0f) {
                showing = false;
                ((Timer) e.getSource()).stop();
            }
        });
        fadeOutTimer.start();
    }

    private float bounceOut(float t) {
        if (t < (1 / 2.75)) {
            return 7.5625f * t * t;
        } else if (t < (2 / 2.75)) {
            t -= (1.5 / 2.75);
            return 7.5625f * t * t + 0.75f;
        } else if (t < (2.5 / 2.75)) {
            t -= (2.25 / 2.75);
            return 7.5625f * t * t + 0.9375f;
        } else {
            t -= (2.625 / 2.75);
            return 7.5625f * t * t + 0.984375f;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!showing) return;

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();

        float totalScale = scale + idleScaleOffset;
        int drawW = (int) (panelW * totalScale);
        int drawH = (int) (panelH * totalScale);
        int drawX = (panelW - drawW) / 2;
        int drawY = (panelH - drawH) / 2;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Apply rotation around center
        g2d.translate(panelW / 2, panelH / 2);
        g2d.rotate(Math.toRadians(idleAngle));
        g2d.translate(-panelW / 2, -panelH / 2);

        g2d.drawImage(image, drawX, drawY, drawW, drawH, this);
        g2d.dispose();
    }
}
