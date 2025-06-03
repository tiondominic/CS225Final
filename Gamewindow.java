import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import java.io.InputStream;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

class NumberFormatter {
    private static final DecimalFormat formatter0 = new DecimalFormat("#,###");      // No decimals
    private static final DecimalFormat formatter1 = new DecimalFormat("#,###.0");    // One decimal
    private static final DecimalFormat formatter2 = new DecimalFormat("#,###.00");   // Two decimals
    private static final DecimalFormat formatter3 = new DecimalFormat("#,###.000");  // Three decimals

    public static String formatNumber(double number) {
        if (number < 100) {
            return formatter0.format(number);
        } else if (number < 100_000) {
            return formatter1.format(number);
        } else if (number < 1_000_000) {
            return formatter2.format(number / 1_000); // Thousand
        } else if (number < 1_000_000_000) {
            return formatter3.format(number / 1_000_000); // Million
        } else if (number < 1_000_000_000_000L) {
            return formatter3.format(number / 1_000_000_000); // Billion
        } else if (number < 1_000_000_000_000_000L) {
            return formatter3.format(number / 1_000_000_000_000L); // Trillion
        } else {
            return formatter3.format(number / 1_000_000_000_000_000L); // Quadrillion
        }
    }

    public static String formatCPS(double number) {
        if (number < 100_000) {
            return formatter1.format(number); // Always at least one decimal
        } else if (number < 1_000_000) {
            return formatter2.format(number / 1_000);
        } else if (number < 1_000_000_000) {
            return formatter3.format(number / 1_000_000);
        } else if (number < 1_000_000_000_000L) {
            return formatter3.format(number / 1_000_000_000);
        } else if (number < 1_000_000_000_000_000L) {
            return formatter3.format(number / 1_000_000_000_000L);
        } else {
            return formatter3.format(number / 1_000_000_000_000_000L);
        }
    }

    public static String getUnit(double number) {
        if (number < 100_000) {
            return "";
        } else if (number < 1_000_000) {
            return "Thousand";
        } else if (number < 1_000_000_000) {
            return "Million";
        } else if (number < 1_000_000_000_000L) {
            return "Billion";
        } else if (number < 1_000_000_000_000_000L) {
            return "Trillion";
        } else {
            return "Quadrillion";
        }
    }
}

class StaticImageToggleButton extends JToggleButton {
    private final Image image;
    private double currentScale = 1.0;
    private int frameHeight = (getHeight() + 24);
    private int baseFontSize = (int) (frameHeight * 0.030);
    private Font baseFont = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 1.3)); // default

    public StaticImageToggleButton(String text, Image image) {
        super(text);
        this.image = image;

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setText(null); // Hide default button text
        setRolloverEnabled(true);
        setLayout(new GridBagLayout()); // for centered JLabel if needed
    }

    public void setBaseFont(Font font) {
        this.baseFont = font;
        updateLabelFontScale();
    }

    private void updateLabelFontScale() {
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel label) {
                float newSize = (float) (baseFont.getSize2D() * currentScale);
                label.setFont(baseFont.deriveFont(newSize));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean isPressed = getModel().isArmed() && getModel().isPressed();
        boolean isHovered = getModel().isRollover();
        boolean isToggled = isSelected();

        double scale = 1.0;
        if (isPressed) {
            scale = 0.9;
        } else if (isHovered) {
            scale = 0.975;
        } else if (isToggled) {
            scale = 0.95;
        }

        currentScale = scale;
        updateLabelFontScale();

        int buttonWidth = getWidth();
        int buttonHeight = getHeight();

        double imgAspect = (double) image.getWidth(null) / image.getHeight(null);
        int drawWidth = (int) (buttonWidth * scale);
        int drawHeight = (int) (drawWidth / imgAspect);

        if (drawHeight > buttonHeight * scale) {
            drawHeight = (int) (buttonHeight * scale);
            drawWidth = (int) (drawHeight * imgAspect);
        }

        int x = (buttonWidth - drawWidth) / 2;
        int y = (buttonHeight - drawHeight) / 2;

        g2d.drawImage(image, x, y, drawWidth, drawHeight, this);

        if (isToggled) {
            int overlayHeight = (int) (drawHeight * 0.625);
            int overlayY = y + (drawHeight - overlayHeight) / 2;

            g2d.setColor(new Color(0, 0, 0, 100));
            int arcWidth = 13;
            int arcHeight = 13;
            g2d.fillRoundRect(x, overlayY, drawWidth, overlayHeight, arcWidth, arcHeight);
        }

        g2d.dispose();
    }
}

class StaticImageButton extends JButton {
    private final Image image;
    private int frameHeight = (getHeight() + 24);
    private int baseFontSize = (int) (frameHeight * 0.030);
    private Font baseFont = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 1.3)); // default
    private double currentScale = 1.0;

    public StaticImageButton(String text, Image image) {
        super(text);
        this.image = image;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setText(null);
        setRolloverEnabled(true);
        setLayout(new GridBagLayout()); // allow label centering
    }

    public void setBaseFont(Font font) {
        this.baseFont = font;
        updateLabelFontScale();
    }

    private void updateLabelFontScale() {
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel label) {
                float newSize = (float) (baseFont.getSize2D() * currentScale);
                label.setFont(baseFont.deriveFont(newSize));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean isPressed = getModel().isArmed() && getModel().isPressed();
        boolean isHovered = getModel().isRollover();

        double scale = 1.0;
        if (isPressed) {
            scale = 0.8;
        } else if (isHovered) {
            scale = 0.95;
        }

        currentScale = scale;
        updateLabelFontScale();

        int buttonWidth = getWidth();
        int buttonHeight = getHeight();

        double imgAspect = (double) image.getWidth(null) / image.getHeight(null);
        int drawWidth = (int) (buttonWidth * scale);
        int drawHeight = (int) (drawWidth / imgAspect);

        if (drawHeight > buttonHeight * scale) {
            drawHeight = (int) (buttonHeight * scale);
            drawWidth = (int) (drawHeight * imgAspect);
        }

        int x = (buttonWidth - drawWidth) / 2;
        int y = (buttonHeight - drawHeight) / 2;

        g2d.drawImage(image, x, y, drawWidth, drawHeight, this);
        g2d.dispose();
    }
}

class RotatingImageButton extends JButton {
    private final Image image;
    private double rotationAngle = 0;

    public RotatingImageButton(String text, Image image) {
        super(text);
        this.image = image;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setText(null);

        // Enable hover detection
        setRolloverEnabled(true);
        
        int rotationDurationSeconds = 30;
        // Timer for 60-second full rotation (360° / (60fps * 60s))
        Timer rotationTimer = new Timer(1000 / 60, e -> {
            rotationAngle += (2 * Math.PI) / (60 * rotationDurationSeconds ); // One full rotation every 60 seconds
            if (rotationAngle >= 2 * Math.PI) {
                rotationAngle -= 2 * Math.PI;
            }
            repaint();
        });
        rotationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable antialiasing and quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Button state detection
        boolean isPressed = getModel().isArmed() && getModel().isPressed();
        boolean isHovered = getModel().isRollover();

        // Base scaling
        double scale = 0.9;
        if (isPressed) {
            scale = 0.8;
        } else if (isHovered) {
            scale = 1.0;
        }

        int buttonWidth = getWidth();
        int buttonHeight = getHeight();

        // Maintain image aspect ratio
        double imgAspect = (double) image.getWidth(null) / image.getHeight(null);
        int drawWidth = (int) (buttonWidth * scale);
        int drawHeight = (int) (drawWidth / imgAspect);

        if (drawHeight > buttonHeight * scale) {
            drawHeight = (int) (buttonHeight * scale);
            drawWidth = (int) (drawHeight * imgAspect);
        }

        // Centering position
        int centerX = buttonWidth / 2;
        int centerY = buttonHeight / 2;
        int imgX = -drawWidth / 2;
        int imgY = -drawHeight / 2;

        // Apply rotation and draw image
        g2d.translate(centerX, centerY);
        g2d.rotate(rotationAngle);
        g2d.drawImage(image, imgX, imgY, drawWidth, drawHeight, this);
        g2d.dispose();
    }
}

class ImagePanel extends JPanel {
    private Image backgroundImage;
    
    public ImagePanel(Image image) {
        this.backgroundImage = image;
        setOpaque(false);
    }
    
    public void setImage(Image image) {
        this.backgroundImage = image;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = getWidth();
        int h = getHeight();
        
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            
            // High-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Stretch image to fill the entire panel (no aspect ratio preservation)
            g2d.drawImage(backgroundImage, 0, 0, w, h, this);
            g2d.dispose();
        }
    }
}

class TiledImagePanel extends JPanel {
    private Image backgroundImage;

    public TiledImagePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.setOpaque(false);
    }

    public void setImage(Image image) {
    // If you want to allow changing image dynamically
    // You could add a check to see if the image really changed, but not mandatory
        this.backgroundImage = image;
        repaint();
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);  // Debug: fill blue background
        g.fillRect(0, 0, getWidth(), getHeight());

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Define how many tiles you'd like horizontally/vertically
            int tileCountX = 1; // Try tuning this number for tighter/looser tiling
            int tileCountY = 4;

            int scaledTileWidth = panelWidth / tileCountX;
            int scaledTileHeight = panelHeight / tileCountY;

            for (int y = 0; y < panelHeight; y += scaledTileHeight) {
                for (int x = 0; x < panelWidth; x += scaledTileWidth) {
                    g2d.drawImage(backgroundImage, x, y, (scaledTileWidth + 18), scaledTileHeight, this);
                }
            }

            g2d.dispose();
        }
    }
}

// Custom panel for upgrade buttons with three-column layout
class UpgradeButton extends JButton {
    private final JLabel nameLabel;
    private final JLabel costLabel;
    private final JLabel ownedLabel;
    private final Color RED_TEXT = new Color(0xFF0000);
    private final Color GREEN_TEXT = new Color(0x5ce85a);
    private final Upgrade upgrade;
    private Image buttonImage;
    private int currentStage = 1;

    // Track button state
    private boolean isHovered = false;
    private boolean isPressed = false;
    private double currentScale = 1.0;

    // Font sizes
    private Font nameLabelFont;
    private Font costLabelFont;
    private Font ownedLabelFont;

    // Cached font
    private static Font robotoSerifExtraBold;

    static {
        try {
            robotoSerifExtraBold = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/RobotoSerif-ExtraBold.ttf"));
        } catch (Exception e) {
            System.err.println("Failed to load custom font. Using fallback.");
            robotoSerifExtraBold = new Font("SansSerif", Font.BOLD, 12);
        }
    }

    // Corner radius for overlays
    private final int cornerRadius = 32;

    public UpgradeButton(Upgrade upgrade, int buttonIndex) {
        super();
        this.upgrade = upgrade;
        setLayout(null);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        // Load the appropriate button image
        int imageIndex = Math.min(Math.max(buttonIndex + 2, 2), 16);
        String imagePath = "assets/Upgrade Buttons/Upgrade_Button-" + String.format("%02d", imageIndex) + ".png";

        try {
            buttonImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath);
            buttonImage = null;
        }

        nameLabelFont = FontManager.getFont("RobotoSerif-ExtraBold", 32f);
        costLabelFont = FontManager.getFont("RobotoSerif-SemiBold", 20f);
        ownedLabelFont = FontManager.getFont("RobotoSerif-Bold", 42f);

        // Create labels with larger fonts and white/red text
        nameLabel = new ShadowLabel("???", SwingConstants.LEFT);
        ((ShadowLabel) nameLabel).setShadow(new Color(0, 0, 0, 100), 2, 2, 4);
        nameLabel.setFont(nameLabelFont);
        nameLabel.setForeground(Color.WHITE);

        costLabel = new ShadowLabel("???", SwingConstants.LEFT);
        ((ShadowLabel) costLabel).setShadow(new Color(0, 0, 0, 125), 1, 1, 4);
        costLabel.setFont(costLabelFont);
        costLabel.setForeground(RED_TEXT);

        ownedLabel = new ShadowLabel("0", SwingConstants.CENTER);
        ((ShadowLabel) costLabel).setShadow(new Color(0, 0, 0, 125), 1, 1, 1);
        ownedLabel.setFont(ownedLabelFont);
        ownedLabel.setForeground(new Color(0, 0, 0, 128));

        add(nameLabel);
        add(costLabel);
        add(ownedLabel);

        setOpaque(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLabelPositions();
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setHovered(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setHovered(false);
                setPressed(false); // Reset pressed state when mouse exits
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                setPressed(true);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                setPressed(false);
            }
        });
    }

    private void updateLabelPositions() {
        int width = getWidth();
        int height = getHeight();

        int ownedWidth = 70;
        int labelWidth = (int) (width * 0.3);
        int labelHeight = (int) (height * 0.15);
        int labelHeight2 = (int) (height * 0.5);
        int labelHeight3 = (int) (height * 0.25);

        nameLabel.setBounds(labelWidth, labelHeight, width - ownedWidth - 20, height / 2 - 5);
        costLabel.setBounds(labelWidth, labelHeight2, width - ownedWidth - 20, height / 2 - 5);
        ownedLabel.setBounds(width - ownedWidth - 10, labelHeight3, ownedWidth, height - 10);

        // Don't override fonts here!
        updateLabelFontScale();
    }

    private void updateLabelFontScale() {
        nameLabel.setFont(nameLabelFont.deriveFont((float) (nameLabelFont.getSize2D() * currentScale)));
        costLabel.setFont(costLabelFont.deriveFont((float) (costLabelFont.getSize2D() * currentScale)));
        ownedLabel.setFont(ownedLabelFont.deriveFont((float) (ownedLabelFont.getSize2D() * currentScale)));
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
        repaint();
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double scale = 1.0;
        if (isPressed) {
            scale = 0.9;
        } else if (isHovered) {
            scale = 0.95;
        }

        currentScale = scale;
        updateLabelFontScale();

        int drawWidth = (int) (width * scale);
        int drawHeight = (int) (height * scale);

        int x = (width - drawWidth) / 2;
        int y = (height - drawHeight) / 2;

        if (buttonImage != null) {
            g2d.drawImage(buttonImage, x, y, drawWidth, drawHeight, this);
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, new Color(0xF7F4B7), width, height, new Color(0xE5D68A));
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, drawWidth, drawHeight, cornerRadius, cornerRadius);
        }

        if (currentStage == 1) {
            g2d.setColor(new Color(0, 0, 0, 230));
            g2d.fillRoundRect(x, y, drawWidth, drawHeight, cornerRadius, cornerRadius);
        } else if (currentStage == 2) {
            g2d.setColor(new Color(0, 0, 0, 115));
            g2d.fillRoundRect(x, y, drawWidth, drawHeight, cornerRadius, cornerRadius);
        }

        g2d.dispose();
    }

    public void updateStage(int stage, double cookieCount) {
        this.currentStage = stage;
        ownedLabel.setText(String.valueOf(upgrade.getOwned()));

        if (stage == 1) {
            nameLabel.setText("???");
            costLabel.setText("???");
        } else {
            nameLabel.setText(upgrade.getName());
            double cost = upgrade.getCost(Global.getQuantity());
            String formattedCost = NumberFormatter.formatNumber(cost);
            String unit = NumberFormatter.getUnit(cost);
            costLabel.setText(formattedCost + " " + unit);

            costLabel.setForeground(stage == 3 ? GREEN_TEXT : RED_TEXT);
        }

        repaint();
    }
}

class ShadowLabel extends JLabel {
    private Color shadowColor = new Color(0, 0, 0, 100);
    private int shadowOffsetX = 2;
    private int shadowOffsetY = 2;
    private int blurRadius = 4;

    public ShadowLabel(String text, int alignment) {
        super(text, alignment);
    }

    public void setShadow(Color color, int offsetX, int offsetY, int blurRadius) {
        this.shadowColor = color;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        this.blurRadius = blurRadius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        String text = getText();
        Font font = getFont();
        FontMetrics fm = getFontMetrics(font);

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int x = 0;
        if (getHorizontalAlignment() == SwingConstants.CENTER) {
            x = (getWidth() - textWidth) / 2;
        } else if (getHorizontalAlignment() == SwingConstants.RIGHT) {
            x = getWidth() - textWidth;
        }

        int y = fm.getAscent();

        // Create shadow image
        BufferedImage shadowImg = new BufferedImage(textWidth + blurRadius * 2, textHeight + blurRadius * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sg = shadowImg.createGraphics();
        sg.setFont(font);
        sg.setColor(shadowColor);
        sg.drawString(text, blurRadius, y + blurRadius);
        sg.dispose();

        // Apply blur
        shadowImg = getBlurredImage(shadowImg, blurRadius);

        // Draw shadow
        g.drawImage(shadowImg, x + shadowOffsetX - blurRadius, y - fm.getAscent() + shadowOffsetY - blurRadius, null);

        // Draw main text
        g.setFont(font);
        g.setColor(getForeground());
        g.drawString(text, x, y);
    }

    private BufferedImage getBlurredImage(BufferedImage image, int radius) {
        float[] matrix = createGaussianKernel(radius);
        ConvolveOp op = new ConvolveOp(new Kernel(radius * 2 + 1, radius * 2 + 1, matrix), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    private float[] createGaussianKernel(int radius) {
        int size = radius * 2 + 1;
        float[] kernel = new float[size * size];
        float sigma = radius / 2f;
        float norm = 0;
        int index = 0;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                float value = (float) Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[index++] = value;
                norm += value;
            }
        }

        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= norm;
        }

        return kernel;
    }
}

public class Gamewindow extends JFrame {

    // Example dynamic row counts
    private final int maxNameLength = 15;
    private int purchasedUpgrades = 10;
    //private int availableUpgrades = 5;
    private Gamestate gamestate;
    private List<Upgrade> upgrades = new ArrayList<>();
    private Timer uiUpdateTimer;
    private JLabel cookieCountLabel;
    private JLabel cookieUnitLabel;
    private JLabel cpsLabel;
    private JPanel row2C;
    private int upgradeButtonWidth;
    private int upgradeButtonHeight;
    private List<JPanel> upgradeWrappers = new ArrayList<>();
    private List<UpgradeButton> upgradePanels = new ArrayList<>();
    
    // OPTIMIZED: Use CopyOnWriteArrayList for thread safety and better performance
    private final CopyOnWriteArrayList<FloatingText> floatingTexts = new CopyOnWriteArrayList<>();
    
    // OPTIMIZED: Limit maximum floating text objects
    private static final int MAX_FLOATING_TEXTS = 20;
    
    // OPTIMIZED: Cache frequently used objects
    private Font cachedFloatingTextFont;
    private AlphaComposite[] alphaComposites = new AlphaComposite[61]; // Pre-calculated alpha values
    
    // OPTIMIZED: Batch similar floating texts
    // Remove these batching variables - not needed for original Cookie Clicker style
    // private String lastFloatingText = "";
    // private int lastFloatingX = -1;
    // private int lastFloatingY = -1;
    // private long lastFloatingTime = 0;
    // private static final long BATCH_THRESHOLD_MS = 100;

    // Glass pane for floating text
    private OptimizedGlassPane glassPane;

    // Colors for upgrade stages
    private final Color STAGE1_BG = new Color(0x333333); // Dark gray
    private final Color STAGE2_BG = new Color(0x777777); // Lighter gray
    private final Color STAGE3_BG = new Color(0xF7F4B7); // Original color

    List<JButton> upgradeButtons = new ArrayList<>();
    List<JButton> toolButtons = new ArrayList<>();
    List<JToggleButton> genToggleButtons = new ArrayList<>();
    List<JButton> genButtons = new ArrayList<>();
    List<JLabel> genLabel = new ArrayList<>();

    List<JButton> topButtons = new ArrayList<>();
    List<JButton> bottomButtons = new ArrayList<>();
    List<JLabel> purchasedLabels = new ArrayList<>();

    List<JPanel> centerRows = new ArrayList<>();
    List<JButton> firstRowButtons = new ArrayList<>();
    List<JPanel> rowACenterColumnA = new ArrayList<>();
    List<JPanel> rowACenterColumnB = new ArrayList<>();
    List<JPanel> secondRowPanels = new ArrayList<>();

    // Add a new list to track which upgrades have passed Stage 1
    private List<Boolean> upgradePastStage1 = new ArrayList<>();
    
    public Gamewindow(Gamestate gamestate) {
        this.gamestate = gamestate;
        setTitle("Cookie Clicker Layout - Colored");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Pre-calculate alpha composites
        initializeAlphaComposites();

        // Set size to 75% width and 75% height of the screen, and center it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int modifiedwidth = (int) (screenSize.width - 16);
        int modifiedheight = (int) (screenSize.height - 39);
        
        int width = (int) (modifiedwidth * 0.75);
        int height = (int) (modifiedheight * 0.75);

        setSize(width, height);
        setLocationRelativeTo(null);
        

        System.out.println("[DEBUG] MINIMIZED WINDOW DIMENSIONS: " + width + "x" + height);

        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        int westPanelWidth = (int) (width * 0.25);
        int centerPanelWidth = (int) (width * 0.5);
        int eastPanelWidth = (int) (width * 0.25);

        // Create the three main panels with GridBagLayout
        JPanel westPanel = createWestPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel eastPanel = createEastPanel();

        // Set fixed widths for all panels
        westPanel.setPreferredSize(new Dimension(westPanelWidth, height));
        westPanel.setMinimumSize(new Dimension(westPanelWidth, 0));
        westPanel.setMaximumSize(new Dimension(westPanelWidth, Integer.MAX_VALUE));

        eastPanel.setPreferredSize(new Dimension(eastPanelWidth, height));
        eastPanel.setMinimumSize(new Dimension(eastPanelWidth, 0));
        eastPanel.setMaximumSize(new Dimension(eastPanelWidth, Integer.MAX_VALUE));

        centerPanel.setPreferredSize(new Dimension(centerPanelWidth, height));
        centerPanel.setMinimumSize(new Dimension(centerPanelWidth, 0));
        centerPanel.setMaximumSize(new Dimension(centerPanelWidth, Integer.MAX_VALUE));

        // Add panels to the frame
        add(westPanel, BorderLayout.WEST);

        add(centerPanel, BorderLayout.CENTER);

        add(eastPanel, BorderLayout.EAST);

        // Set up UI update timer (16ms = ~60fps)
        uiUpdateTimer = new Timer(16, e -> updateDisplay());
        uiUpdateTimer.start();

        // Reduce timer frequency to 30fps for floating text
        Timer floatingLabelTimer = new Timer(33, e -> {
            updateFloatingTexts();
            if (glassPane != null) {
                glassPane.repaint(); // Repaint glass pane instead of entire frame
            }
        });
        floatingLabelTimer.start();

        setVisible(true);

        setupGlassPane();

        // After visible: dynamically size the side panels
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int currentWidth = (getContentPane().getWidth() - 16);
                int currentHeight = (getContentPane().getHeight() + 24);

                System.out.println("[DEBUG] WINDOW DIMENSIONS: " + currentWidth + "x" + currentHeight);
                
                int sidePanelWidth = (int) (currentWidth * 0.25);
                int centerPanelWidth = (int) (currentWidth * 0.5); // e.g. 45% of frame width (adjust as needed)

                westPanel.setPreferredSize(new Dimension(sidePanelWidth, currentHeight));
                westPanel.setMinimumSize(new Dimension(sidePanelWidth, currentHeight));
                westPanel.setMaximumSize(new Dimension(sidePanelWidth, Integer.MAX_VALUE));

                centerPanel.setPreferredSize(new Dimension(centerPanelWidth, currentHeight));
                centerPanel.setMinimumSize(new Dimension(centerPanelWidth, currentHeight));
                centerPanel.setMaximumSize(new Dimension(centerPanelWidth, Integer.MAX_VALUE));

                eastPanel.setPreferredSize(new Dimension(sidePanelWidth, currentHeight));
                eastPanel.setMinimumSize(new Dimension(sidePanelWidth, currentHeight));
                eastPanel.setMaximumSize(new Dimension(sidePanelWidth, Integer.MAX_VALUE));

                // OPTIMIZED: Update cached font when window resizes
                updateCachedFont();

                // Force re-layout
                revalidate();
                repaint();
            }
        });

        SwingUtilities.invokeLater(() -> {
            Insets insets = getInsets(); // top, left, bottom, right
            Dimension screenSize2 = Toolkit.getDefaultToolkit().getScreenSize();

            int contentWidth = screenSize2.width - insets.left - insets.right;
            int contentHeight = screenSize2.height - insets.top - insets.bottom;

            System.out.println("Adjusted for insets: " + contentWidth + " x " + contentHeight);
            System.out.println("Adjusted for insets centerpanel: " + centerPanelWidth + " x " + contentHeight);
            
            // OPTIMIZED: Initialize cached font after window is fully set up
            updateCachedFont();
        });
    }

    // OPTIMIZED: Pre-calculate alpha composites for better performance
    private void initializeAlphaComposites() {
        alphaComposites = new AlphaComposite[91]; // 90 frames + 1
        for (int i = 0; i <= 90; i++) {
            float opacity;
            if (i < 20) {
                opacity = 1.0f; // Full opacity for first 20 frames
            } else {
                opacity = 1.0f - ((i - 20) / 70.0f); // Fade out over remaining 70 frames
            }
            alphaComposites[i] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0f, opacity));
        }
    }

    // OPTIMIZED: Update cached font based on current window size
    private void updateCachedFont() {
        int frameHeight = getHeight();
        int baseFontSize = (int) (frameHeight * 0.03);
        cachedFloatingTextFont = new Font("Garamond", Font.BOLD, baseFontSize);
    }

    // OPTIMIZED: Custom glass pane with better performance
    private class OptimizedGlassPane extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (floatingTexts.isEmpty()) return;
            
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable antialiasing for smooth text rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Set font once
            if (cachedFloatingTextFont != null) {
                g2d.setFont(cachedFloatingTextFont);
            }
            
            g2d.setColor(Color.WHITE);
            
            // Draw all floating texts
            for (FloatingText ft : floatingTexts) {
                ft.drawOptimized(g2d, alphaComposites);
            }
            
            g2d.dispose();
        }
    }

    // OPTIMIZED: Efficient floating text update
    private void updateFloatingTexts() {
        if (floatingTexts.isEmpty()) return;
        
        // Remove expired texts in batch
        floatingTexts.removeIf(FloatingText::update);
    }

    // OPTION 2: Glass pane setup method
    private void setupGlassPane() {
        glassPane = new OptimizedGlassPane();
        glassPane.setOpaque(false);
        setGlassPane(glassPane);
        glassPane.setVisible(true);
        
        System.out.println("[DEBUG] Optimized glass pane set up for floating text rendering");
    }

    private JPanel createWestPanel() {
        // Create the west panel with GridBagLayout
        JPanel westPanel = new JPanel(new GridBagLayout());
        westPanel.setBackground(new Color(0xFFFFFF));

        int westPanelWidth = westPanel.getWidth();
        int westPanelHeight = westPanel.getHeight();
        System.out.println("[DEBUG] IN WEST PANEL DIMENSIONS: " + westPanelWidth + " x " + westPanelHeight);
        
        // Set preferred and maximum size to maintain the 25% scaling
        westPanel.setPreferredSize(new Dimension(westPanelWidth, 0));
        westPanel.setMaximumSize(new Dimension(westPanelWidth, Integer.MAX_VALUE));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        // === Row 1 (15% of West Panel Height) ===
        Image row1Image = new ImageIcon("assets/west_row1.png").getImage();
        ImagePanel westRow1 = new ImagePanel(row1Image);
        westRow1.setLayout(new BorderLayout());
        centerRows.add(westRow1);
        
        // Add a spacer at the bottom with height 4.25% of window height
        JPanel spacer = new JPanel();
        spacer.setBackground(new Color(0xEAE77D));
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(westPanelWidth, (int)(westPanelHeight * 0.0425)));
        westRow1.add(spacer, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.weighty = 0.15; // 15% of west panel height
        westPanel.add(westRow1, gbc);

        System.out.println("[DEBUG] IN WEST PANEL SPACER DIMENSIONS: " + (spacer.getPreferredSize()));
        
        // === Row 2 (85% of West Panel Height) ===
        JPanel row2 = new JPanel(new GridBagLayout());
        row2.setBackground(new Color(0xFFFFFF));
        
        GridBagConstraints row2Gbc = new GridBagConstraints();
        row2Gbc.fill = GridBagConstraints.BOTH;
        row2Gbc.gridx = 0;
        row2Gbc.weightx = 1.0;
        
        // Row 2A (10% of row2 height)
        Image row2AImage = new ImageIcon("assets/west_row2A.png").getImage();
        ImagePanel row2A = new ImagePanel(row2AImage);
        row2A.setLayout(new GridBagLayout());
        row2A.setBackground(new Color(0xA7EAA7));
        row2A.setOpaque(false);

        GridBagConstraints row2AGbc = new GridBagConstraints();
        row2AGbc.fill = GridBagConstraints.BOTH;
        row2AGbc.gridy = 0;
        row2AGbc.weighty = 1.0;
        
        // Row 2A Column 1 (30% of west panel width)
        JPanel row2AColumn1 = new JPanel();
        row2AColumn1.setBackground(new Color(0xEAA7A7));
        row2AColumn1.setOpaque(false);
        
        row2AGbc.gridx = 0;
        row2AGbc.weightx = 0.50; // 50% of west panel width
        row2A.add(row2AColumn1, row2AGbc);

        // Row 2A Column 2 (30% of west panel width)
        JPanel row2AColumn2 = new JPanel();
        row2AColumn2.setBackground(new Color(0x39b539));
        row2AColumn2.setOpaque(false);
        
        row2AGbc.gridx = 0;
        row2AGbc.weightx = 0.50; // 50% of west panel width
        row2A.add(row2AColumn2, row2AGbc);

        row2Gbc.gridy = 0;
        row2Gbc.weighty = 0.1; // 10% of row2 height
        row2.add(row2A, row2Gbc);
        
        // Row 2B (7.5% of row2 height) with 3 columns
        Image row2BImage = new ImageIcon("assets/west_row2B.png").getImage();
        ImagePanel row2B = new ImagePanel(row2BImage);
        row2B.setLayout(new GridBagLayout());
        row2B.setBackground(new Color(0xB7A7EA));
 
        GridBagConstraints row2BGbc = new GridBagConstraints();
        row2BGbc.fill = GridBagConstraints.BOTH;
        row2BGbc.gridy = 0;
        row2BGbc.weighty = 1.0;
        
        // Row 2B Column 1 (30% of west panel width)
        Image row2BColumn1Image = new ImageIcon("assets/west_row_2B_column_1.png").getImage();
        ImagePanel row2BColumn1 = new ImagePanel(row2BColumn1Image);
        row2BColumn1.setBackground(new Color(0xEAA7A7));
        row2BColumn1.setOpaque(false);
        
        row2BGbc.gridx = 0;
        row2BGbc.weightx = 0.30; // 30% of west panel width
        row2B.add(row2BColumn1, row2BGbc);
        
        // Row 2B Column 2 (35% of west panel width)
        Image row2BColumn2Image = new ImageIcon("assets/west_row_2B_column_2A.png").getImage();
        StaticImageButton row2BColumn2 = new StaticImageButton("Name (A-Z)", row2BColumn2Image);
        row2BColumn2.addActionListener(e -> System.out.println("Button [Name (A-Z)] clicked"));
        row2BColumn2.setBackground(new Color(0xA7EAEA));
        row2BColumn2.setOpaque(false);
        
        row2BGbc.gridx = 1;
        row2BGbc.weightx = 0.35; // 35% of west panel width
        row2B.add(row2BColumn2, row2BGbc);
        
        // Row 2B Column 3 (35% of west panel width)
        Image row2BColumn3Image = new ImageIcon("assets/west_row_2B_column_3A.png").getImage();
        StaticImageButton row2BColumn3 = new StaticImageButton("Collapse All", row2BColumn3Image);
        row2BColumn2.addActionListener(e -> System.out.println("Button [Collapse All] clicked"));
        row2BColumn2.setBackground(new Color(0xA7EAEA));
        row2BColumn2.setOpaque(false);
        
        row2BGbc.gridx = 2;
        row2BGbc.weightx = 0.35; // 35% of west panel width
        row2B.add(row2BColumn3, row2BGbc);
        
        row2Gbc.gridy = 1;
        row2Gbc.weighty = 0.075; // 7.5% of row2 height
        row2.add(row2B, row2Gbc);
        
        // Row 2C (remaining height of row2) - Scrollable panel
        Image row2CImage = new ImageIcon("assets/west_row2C.png").getImage();
        TiledImagePanel row2CPanel = new TiledImagePanel(row2CImage);
        row2CPanel.setLayout(new GridBagLayout());
        row2CPanel.setBackground(new Color(0x7B89C4));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL; // Only stretch horizontally
        cardGbc.weightx = 1.0; // Important: tells GridBagLayout to grow width
        cardGbc.weighty = 0.0;

        for (int i = 0; i < purchasedUpgrades; i++) {
            JPanel row2CPanelCards = new JPanel();
            row2CPanelCards.setBackground(new Color(0xF7F4B7));
            row2CPanelCards.setOpaque(false);

            JLabel lbl = new JLabel("Card Panel " + (i + 1), SwingConstants.CENTER);
            row2CPanelCards.add(lbl);
            purchasedLabels.add(lbl);

            cardGbc.gridy = i;
            cardGbc.weighty = 0.0; // stack normally without expansion
            row2CPanel.add(row2CPanelCards, cardGbc);
        }

        // Filler panel to push content to top if not enough components
        cardGbc.gridy = purchasedUpgrades;
        cardGbc.weighty = 1.0; // take up remaining space
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        row2CPanel.add(filler, cardGbc);

        JScrollPane row2C = new JScrollPane(row2CPanel);
        row2C.getViewport().setBackground(new Color(0xE59C9C));

        row2C.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        row2C.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Ensures row2CPanel always matches the width of the viewport
        row2C.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int viewportWidth = row2C.getViewport().getWidth();
                row2CPanel.setPreferredSize(new Dimension(viewportWidth, row2CPanel.getPreferredSize().height));
                row2CPanel.revalidate();
            }
        });

        row2Gbc.gridy = 2;
        row2Gbc.weighty = 0.825; // Remaining height (82.5% of row2)
        row2.add(row2C, row2Gbc);
        
        gbc.gridy = 1;
        gbc.weighty = 0.85; // 85% of west panel height
        westPanel.add(row2, gbc);
        
        // Add resize listener to handle component sizing
        westPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = westPanel.getWidth();
                int panelHeight = (westPanel.getHeight() + 24);
                System.out.println("[DEBUG] LISTENER WEST PANEL DIMENSIONS: " + panelWidth + " x " + panelHeight);
                
                // Update spacer size
                spacer.setPreferredSize(new Dimension(panelWidth, (int)(panelHeight * 0.0425)));
                System.out.println("[DEBUG] LISTENER WEST PANEL SPACER DIMENSIONS: " + (spacer.getPreferredSize()));

                // Update card panel sizes
                for (int i = 0; i < purchasedLabels.size(); i++) {
                    Component parent = purchasedLabels.get(i).getParent();
                    if (parent instanceof JPanel cardPanel) {
                        
                        int cardHeight = (int)(panelHeight * 0.85 * 0.825 * 0.35); // or however you want to scale it
                        cardPanel.setPreferredSize(new Dimension(panelWidth - 18, cardHeight));
                        cardPanel.setMinimumSize(cardPanel.getPreferredSize());
                        cardPanel.setMaximumSize(cardPanel.getPreferredSize());
                        cardPanel.revalidate();
                    }
                }

                System.out.println("[DEBUG] LISTENER WEST PANEL DIMENSIONS: " + row2CPanel.getPreferredSize());
                
                // Recalculate total height of all cards
                int totalCardHeight = 0;
                for (Component comp : row2CPanel.getComponents()) {
                    if (comp.isVisible()) {
                        totalCardHeight += comp.getPreferredSize().height;
                    }
                }

                // Set preferred size of row2CPanel to viewport width + total height
                row2CPanel.setPreferredSize(new Dimension(
                    row2C.getViewport().getWidth(),
                    totalCardHeight
                ));
                row2CPanel.revalidate();
                row2CPanel.repaint(); // Needed for paintComponent()
            }
        });

        return westPanel;
    }

    private JPanel createCenterPanel() {
        // Create the center panel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(0xFFFFFF));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        // === Row A (Header) ===
        Image bgImage0 = new ImageIcon("assets/center_row1.png").getImage();
        ImagePanel row1 = new ImagePanel(bgImage0);
        row1.setLayout(new GridBagLayout());
        centerRows.add(row1);

        GridBagConstraints rowAGbc = new GridBagConstraints();
        rowAGbc.fill = GridBagConstraints.BOTH;
        rowAGbc.weighty = 1.0;

        // Left glue
        rowAGbc.gridx = 0;
        rowAGbc.weightx = 0.01;
        row1.add(Box.createHorizontalGlue(), rowAGbc);

        // === Column 1 (2 Buttons) ===
        JPanel column1A = new JPanel(new GridLayout(2, 1));
        column1A.setOpaque(false);

        Image staticButtonImage1 = new ImageIcon("assets/center_row1_columnA1.png").getImage();
        StaticImageButton column1AButton1 = new StaticImageButton("Options", staticButtonImage1);
        
        // DEBUG: Bind decoy golden cookie to this button
        column1AButton1.addActionListener(e -> {
            System.out.println("[DEBUG] Spawning decoy golden cookie instantly...");
            
            // Create and immediately show a decoy golden cookie
            DecoyChaosElements debugDecoyChao = new DecoyChaosElements(gamestate);
            debugDecoyChao.setUp();
            
            // Optional: Auto-close after 15 seconds like the normal system
            Timer debugTimer = new Timer(15000, closeEvent -> {
                debugDecoyChao.exit();
                System.out.println("[DEBUG] Debug decoy golden cookie auto-closed after 15 seconds");
            });
            debugTimer.setRepeats(false);
            debugTimer.start();
        });

        firstRowButtons.add(column1AButton1);

        JPanel button1Wrapper = new JPanel(new BorderLayout());
        button1Wrapper.setOpaque(false);
        button1Wrapper.add(column1AButton1, BorderLayout.CENTER);
        column1A.add(button1Wrapper);

        Image staticButtonImage2 = new ImageIcon("assets/center_row1_columnA2.png").getImage();
        StaticImageButton column1AButton2 = new StaticImageButton("Options", staticButtonImage2);

        // DEBUG: Bind decoy golden cookie to this button
        column1AButton2.addActionListener(e -> {
            System.out.println("[DEBUG] Spawning golden cookie instantly...");
            
            // Create and immediately show a decoy golden cookie
            ChaosElements debugChao = new ChaosElements(gamestate);
            debugChao.setUp();
            
            // Optional: Auto-close after 15 seconds like the normal system
            Timer debugTimer = new Timer(15000, closeEvent -> {
                debugChao.exit();
                System.out.println("[DEBUG] Debug golden cookie auto-closed after 15 seconds");
            });
            debugTimer.setRepeats(false);
            debugTimer.start();
        });
        firstRowButtons.add(column1AButton2);

        JPanel button2Wrapper = new JPanel(new BorderLayout());
        button2Wrapper.setOpaque(false);
        button2Wrapper.add(column1AButton2, BorderLayout.CENTER);
        column1A.add(button2Wrapper);

        rowAGbc.gridx = 1;
        rowAGbc.weightx = 0.175; // Was 16.875%
        row1.add(column1A, rowAGbc);

        // === Column 2 (Middle Label) ===
        Image bgImage2 = new ImageIcon("assets/center_row1_columnB1.png").getImage();
        ImagePanel column2A = new ImagePanel(bgImage2);
        column2A.setOpaque(false);

        rowAGbc.gridx = 2;
        rowAGbc.weightx = 0.63; // Adjusted to fill space previously taken by struts
        row1.add(column2A, rowAGbc);

        // === Column 3 (2 Buttons) ===
        JPanel column3A = new JPanel(new GridLayout(2, 1));
        column3A.setOpaque(false);

        Image staticButtonImage3 = new ImageIcon("assets/center_row1_columnC1.png").getImage();
        StaticImageButton column3AButton1 = new StaticImageButton("Options", staticButtonImage3);
        column3AButton1.addActionListener(e -> System.out.println("Button 3 clicked"));
        firstRowButtons.add(column3AButton1);

        JPanel button3Wrapper = new JPanel(new BorderLayout());
        button3Wrapper.setOpaque(false);
        button3Wrapper.add(column3AButton1, BorderLayout.CENTER);
        column3A.add(button3Wrapper);

        Image staticButtonImage4 = new ImageIcon("assets/center_row1_columnC2.png").getImage();
        StaticImageButton column3AButton2 = new StaticImageButton("Options", staticButtonImage4);
        column3AButton2.addActionListener(e -> System.out.println("Button 4 clicked"));
        firstRowButtons.add(column3AButton2);

        JPanel button4Wrapper = new JPanel(new BorderLayout());
        button4Wrapper.setOpaque(false);
        button4Wrapper.add(column3AButton2, BorderLayout.CENTER);
        column3A.add(button4Wrapper);

        rowAGbc.gridx = 3;
        rowAGbc.weightx = 0.175;
        row1.add(column3A, rowAGbc);

        // Right glue
        rowAGbc.gridx = 4;
        rowAGbc.weightx = 0.01;
        row1.add(Box.createHorizontalGlue(), rowAGbc);
        
        gbc.gridy = 0;
        gbc.weighty = 0.15; // 15% height
        centerPanel.add(row1, gbc);

        System.out.println("ROW2 HEIGHT: " + row1.getHeight());
        
        // === ROW B (Content) ===
        Image bgImage = new ImageIcon("assets/main_background_1.png").getImage();
        ImagePanel rowBCenter = new ImagePanel(bgImage);
        rowBCenter.setLayout(new GridBagLayout());
        centerRows.add(rowBCenter);

        GridBagConstraints rowBGbc = new GridBagConstraints();
        rowBGbc.fill = GridBagConstraints.HORIZONTAL;
        rowBGbc.gridx = 0;
        rowBGbc.anchor = GridBagConstraints.CENTER;
        rowBGbc.weightx = 1.0;
        rowBGbc.weighty = 1.0;

        // Row 0 [Vertical Padding]
        rowBGbc.gridy = 0;
        rowBGbc.weighty = 0.05;
        rowBCenter.add(Box.createVerticalStrut(1), rowBGbc);

        // Row 1 [Bakery Name]
        JPanel row1B = new JPanel(new BorderLayout());
        row1B.setOpaque(false);

        Image staticButtonImage5 = new ImageIcon("assets/center_row2_rowA.png").getImage();
        StaticImageButton row1BButton = new StaticImageButton("Options", staticButtonImage5);
        row1BButton.setLayout(new GridBagLayout());

        GridBagConstraints gbcName = new GridBagConstraints();
        gbcName.gridx = 0;
        gbcName.gridy = 0;
        gbcName.anchor = GridBagConstraints.CENTER;
        gbcName.insets = new Insets(9, 0, 0, 0);

        JLabel bakeryNameLabel = new JLabel("Cookie Clicker's Bakery");
        bakeryNameLabel.setForeground(Color.WHITE);
        bakeryNameLabel.setFont(new Font("Garamond", Font.BOLD, 24));
        row1BButton.add(bakeryNameLabel, gbcName);

        row1BButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                row1BButton,
                "Enter your bakery's name (max " + maxNameLength + " characters):",
                bakeryNameLabel.getText().replace("'s Bakery", "")
            );
            if (input != null) {
                input = input.trim();
                if (!input.isEmpty()) {
                    if (input.length() > maxNameLength) {
                        JOptionPane.showMessageDialog(row1BButton, "Name cannot exceed " + maxNameLength + " characters.");
                        return;
                    }
                    bakeryNameLabel.setText(input + "'s Bakery");
                }
            }
        });

        row1B.add(row1BButton, BorderLayout.CENTER);

        rowBGbc.gridy = 1;
        rowBGbc.weighty = 0.1;
        rowBCenter.add(row1B, rowBGbc);
        secondRowPanels.add(row1B);

        // === Row 2 [Cookie Button with spacers] ===
        JPanel row2Container = new JPanel(new GridBagLayout());
        row2Container.setOpaque(false);

        // --- Left spacer (25%) ---
        JPanel row2LeftSpacer = new JPanel();
        row2LeftSpacer.setOpaque(false);
        GridBagConstraints leftSpacerGbc = new GridBagConstraints();
        leftSpacerGbc.gridx = 0;
        leftSpacerGbc.gridy = 0;
        leftSpacerGbc.weightx = 0.25;
        leftSpacerGbc.fill = GridBagConstraints.BOTH;
        row2Container.add(row2LeftSpacer, leftSpacerGbc);

        // --- Center content (cookie button panel) ---
        JPanel row2B = new JPanel(new GridBagLayout());
        row2B.setOpaque(false);
        //row2B.setBorder(BorderFactory.createLineBorder(Color.RED)); // Debug visual

        Image img = new ImageIcon("assets/main_cookie_3.png").getImage();
        RotatingImageButton actionButtonrow2B = new RotatingImageButton(null, img);

        actionButtonrow2B.addActionListener(e -> {
            gamestate.Click();
            updateDisplay();

            // Get click position from the button's center
            Point buttonCenter = actionButtonrow2B.getLocationOnScreen();
            Point frameLocation = getLocationOnScreen();

            int clickX = buttonCenter.x - frameLocation.x + (actionButtonrow2B.getWidth() / 2);
            int clickY = buttonCenter.y - frameLocation.y + (actionButtonrow2B.getHeight() / 2);
            clickX += (int)(Math.random() * 40 - 20);
            clickY += (int)(Math.random() * 40 - 20);

            String floatingText = "+" + NumberFormatter.formatNumber(gamestate.getClickingPower());
            showFloatingTextOptimized(clickX, clickY, floatingText);
        });

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridx = 0;
        buttonGbc.gridy = 0;
        buttonGbc.weightx = 1;
        buttonGbc.weighty = 1;
        buttonGbc.fill = GridBagConstraints.BOTH;
        row2B.add(actionButtonrow2B, buttonGbc);

        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 1;
        centerGbc.gridy = 0;
        centerGbc.weightx = 0.5;
        centerGbc.fill = GridBagConstraints.BOTH;
        row2Container.add(row2B, centerGbc);

        // --- Right spacer (25%) ---
        JPanel row2RightSpacer = new JPanel();
        row2RightSpacer.setOpaque(false);
        GridBagConstraints rightSpacerGbc = new GridBagConstraints();
        rightSpacerGbc.gridx = 2;
        rightSpacerGbc.gridy = 0;
        rightSpacerGbc.weightx = 0.25;
        rightSpacerGbc.fill = GridBagConstraints.BOTH;
        row2Container.add(row2RightSpacer, rightSpacerGbc);

        // === Add full row2Container to parent ===
        rowBGbc.gridy = 2;
        rowBGbc.weighty = 0.525;
        rowBCenter.add(row2Container, rowBGbc);
        secondRowPanels.add(row2Container);

        // === Create a unified container for rows 3-5 with fixed width constraints ===
        JPanel statsContainer = new JPanel(new GridBagLayout());
        statsContainer.setOpaque(false);
        //statsContainer.setBorder(BorderFactory.createLineBorder(Color.RED, 1, true)); // true = rounded, purely visual

        GridBagConstraints statsMainGbc = new GridBagConstraints();
        statsMainGbc.fill = GridBagConstraints.BOTH;
        statsMainGbc.gridx = 0;
        statsMainGbc.weightx = 1.0;

        // === Row 3 (Cookie Number) ===
        JPanel row3Container = new JPanel(new GridBagLayout());
        row3Container.setOpaque(false);
        

        GridBagConstraints row3Gbc = new GridBagConstraints();
        row3Gbc.gridy = 0;
        row3Gbc.weighty = 1.0;
        row3Gbc.fill = GridBagConstraints.BOTH;

        // Left spacer - FIXED WIDTH
        JPanel row3LeftSpacer = new JPanel();
        row3LeftSpacer.setOpaque(false);
        row3Gbc.gridx = 0;
        row3Gbc.weightx = 0.15;
        row3Container.add(row3LeftSpacer, row3Gbc);

        // Content panel - FIXED WIDTH with size constraints
        ImagePanel row3Content = new ImagePanel(new ImageIcon("assets/center_row2_rowC.png").getImage());
        row3Content.setLayout(new GridBagLayout());

        // CRITICAL: Set explicit size constraints to prevent content-based resizing
        row3Content.setMinimumSize(new Dimension(0, 0));
        row3Content.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // FIXED: Label constraints for visibility
        GridBagConstraints row3LabelGbc = new GridBagConstraints();
        row3LabelGbc.gridx = 0;
        row3LabelGbc.gridy = 0;
        row3LabelGbc.weighty = 1.0;
        row3LabelGbc.weightx = 1.0;
        row3LabelGbc.insets = new Insets(16, 0, 0, 0);
        row3LabelGbc.anchor = GridBagConstraints.SOUTH;
        row3LabelGbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion

        cookieCountLabel = new JLabel("0.0");
        cookieCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cookieCountLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        // FIXED: Don't restrict the label size - let it size naturally within constraints
        row3Content.add(cookieCountLabel, row3LabelGbc);

        row3Gbc.gridx = 1;
        row3Gbc.weightx = 0.7;
        row3Container.add(row3Content, row3Gbc);

        // Right spacer - FIXED WIDTH
        JPanel row3RightSpacer = new JPanel();
        row3RightSpacer.setOpaque(false);
        row3Gbc.gridx = 2;
        row3Gbc.weightx = 0.15;
        row3Container.add(row3RightSpacer, row3Gbc);

        // Add to main stats container
        statsMainGbc.gridy = 0;
        statsMainGbc.weighty = 0.1;
        statsContainer.add(row3Container, statsMainGbc);
        secondRowPanels.add(row3Container);

        // === Row 4 (Cookie Unit) ===
        JPanel row4Container = new JPanel(new GridBagLayout());
        row4Container.setOpaque(false);

        GridBagConstraints row4Gbc = new GridBagConstraints();
        row4Gbc.gridy = 0;
        row4Gbc.weighty = 1.0;
        row4Gbc.fill = GridBagConstraints.BOTH;

        // Left spacer - IDENTICAL to row3
        JPanel row4LeftSpacer = new JPanel();
        row4LeftSpacer.setOpaque(false);
        row4Gbc.gridx = 0;
        row4Gbc.weightx = 0.15;
        row4Container.add(row4LeftSpacer, row4Gbc);

        // Content panel - IDENTICAL constraints to row3
        ImagePanel row4Content = new ImagePanel(new ImageIcon("assets/center_row2_rowD.png").getImage());
        row4Content.setLayout(new GridBagLayout());

        // CRITICAL: Same size constraints as row3
        row4Content.setMinimumSize(new Dimension(0, 0));
        row4Content.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // FIXED: Label constraints for visibility
        GridBagConstraints row4LabelGbc = new GridBagConstraints();
        row4LabelGbc.gridx = 0;
        row4LabelGbc.gridy = 0;
        row4LabelGbc.weighty = 1.0;
        row4LabelGbc.weightx = 1.0;
        row4LabelGbc.anchor = GridBagConstraints.NORTH;
        row4LabelGbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion

        cookieUnitLabel = new JLabel("Cookies");
        cookieUnitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cookieUnitLabel.setVerticalAlignment(SwingConstants.TOP);
        // FIXED: Don't restrict the label size
        row4Content.add(cookieUnitLabel, row4LabelGbc);

        row4Gbc.gridx = 1;
        row4Gbc.weightx = 0.7; // IDENTICAL to row3
        row4Container.add(row4Content, row4Gbc);

        // Right spacer - IDENTICAL to row3
        JPanel row4RightSpacer = new JPanel();
        row4RightSpacer.setOpaque(false);
        row4Gbc.gridx = 2;
        row4Gbc.weightx = 0.15;
        row4Container.add(row4RightSpacer, row4Gbc);

        // Add to main stats container
        statsMainGbc.gridy = 1;
        statsMainGbc.weighty = 0.1;
        statsContainer.add(row4Container, statsMainGbc);
        secondRowPanels.add(row4Container);

        // === Row 5 (CPS) ===
        JPanel row5Container = new JPanel(new GridBagLayout());
        row5Container.setOpaque(false);

        GridBagConstraints row5Gbc = new GridBagConstraints();
        row5Gbc.gridy = 0;
        row5Gbc.weighty = 1.0;
        row5Gbc.fill = GridBagConstraints.BOTH;

        // Left spacer - IDENTICAL to row3 and row4
        JPanel row5LeftSpacer = new JPanel();
        row5LeftSpacer.setOpaque(false);
        row5Gbc.gridx = 0;
        row5Gbc.weightx = 0.15;
        row5Container.add(row5LeftSpacer, row5Gbc);

        // Content panel - IDENTICAL constraints to row3 and row4
        ImagePanel row5Content = new ImagePanel(new ImageIcon("assets/center_row2_rowE.png").getImage());
        row5Content.setLayout(new GridBagLayout());

        // CRITICAL: Same size constraints as row3 and row4
        row5Content.setMinimumSize(new Dimension(0, 0));
        row5Content.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // FIXED: Label constraints for visibility
        GridBagConstraints row5LabelGbc = new GridBagConstraints();
        row5LabelGbc.gridx = 0;
        row5LabelGbc.gridy = 0;
        row5LabelGbc.weighty = 1.0;
        row5LabelGbc.weightx = 1.0;
        row5LabelGbc.insets = new Insets(0, 0, 20, 0);
        row5LabelGbc.anchor = GridBagConstraints.NORTH;
        row5LabelGbc.fill = GridBagConstraints.HORIZONTAL; // Allow horizontal expansion

        cpsLabel = new JLabel("0.0 per second");
        cpsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cpsLabel.setVerticalAlignment(SwingConstants.TOP);
        // FIXED: Don't restrict the label size
        row5Content.add(cpsLabel, row5LabelGbc);

        row5Gbc.gridx = 1;
        row5Gbc.weightx = 0.7; // IDENTICAL to row3 and row4
        row5Container.add(row5Content, row5Gbc);

        // Right spacer - IDENTICAL to row3 and row4
        JPanel row5RightSpacer = new JPanel();
        row5RightSpacer.setOpaque(false);
        row5Gbc.gridx = 2;
        row5Gbc.weightx = 0.15;
        row5Container.add(row5RightSpacer, row5Gbc);

        // Add to main stats container
        statsMainGbc.gridy = 2;
        statsMainGbc.weighty = 0.075;
        statsContainer.add(row5Container, statsMainGbc);
        secondRowPanels.add(row5Container);

        // === Add the unified stats container to the main layout ===
        rowBGbc.gridy = 3;
        rowBGbc.weighty = 0.275; // Total: 0.1 + 0.1 + 0.075
        rowBGbc.fill = GridBagConstraints.HORIZONTAL;
        rowBCenter.add(statsContainer, rowBGbc);

        // Add vertical padding
        rowBGbc.gridy = 4;
        rowBGbc.weighty = 0.05; // 5% height
        rowBCenter.add(Box.createVerticalStrut(1), rowBGbc);
        
        gbc.gridy = 1;
        gbc.weighty = 0.85; // 85% height
        centerPanel.add(rowBCenter, gbc);
        
        // Add resize listener to handle component sizing
        centerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = centerPanel.getWidth();
                int panelHeight = centerPanel.getHeight();
                
                // Calculate row heights
                int rowAHeight = (int)(panelHeight * 0.15);
                int rowBHeight = (int)(panelHeight * 0.85);

                System.out.println("ROW1 HEIGHT: " + rowAHeight);
                System.out.println("ROW2 HEIGHT: " + rowBHeight);
                
                // Calculate column widths for Row A
                int rowAColumnAWidth = (int)(panelWidth * 0.175);
                int rowAColumnBWidth = (int)(panelWidth * 0.6065);
                
                // Update button sizes for Row A
                for (JButton btn : firstRowButtons) {
                    btn.setPreferredSize(new Dimension(rowAColumnAWidth, (int)(panelHeight * 0.075)));
                }
                
                // Calculate sizes for Row B elements
                int rowBWidth = (int)(panelWidth * 0.6625);
                System.out.println("rowBWidth: " + rowBWidth);
                System.out.println("panelWidth: " + panelWidth);
                System.out.println("panelHeight: " + panelHeight);
                
                // Update Row B elements
                row1BButton.setPreferredSize(new Dimension(rowBWidth, (int)(panelHeight * 0.075)));
                actionButtonrow2B.setPreferredSize(new Dimension((int)(rowBWidth * 0.5), (int)(rowBHeight * 0.525)));
                actionButtonrow2B.setMaximumSize(new Dimension((int)(rowBWidth * 0.5), (int)(rowBHeight * 0.525)));
                actionButtonrow2B.setMinimumSize(new Dimension((int)(rowBWidth * 0.5), (int)(rowBHeight * 0.525)));

                System.out.println("actionButtonrow2B Preferred size: " + actionButtonrow2B.getPreferredSize());
                System.out.println("actionButtonrow2B Maximum size:   " + actionButtonrow2B.getMaximumSize());
                System.out.println("actionButtonrow2B Minimum size:   " + actionButtonrow2B.getMinimumSize());
                System.out.println("actionButtonrow2B Actual size:    " + actionButtonrow2B.getSize());
                
                // Update fonts
                int baseFontSize = (int)(panelHeight * 0.030);
                Font scaledFont1 = new Font("Garamond", Font.BOLD, baseFontSize);
                Font scaledFont2 = new Font("Garamond", Font.BOLD, (int)(baseFontSize * 1.5));
                Font scaledFont3 = new Font("Garamond", Font.BOLD, (int)(baseFontSize * 0.8));
                Font scaledFont4 = new Font("Garamond", Font.BOLD, (int)(baseFontSize * 1.3));
                
                for (JButton btn : firstRowButtons) {
                    btn.setFont(scaledFont1);
                    btn.setForeground(Color.BLACK);
                }
                
                Color fontColor = Color.WHITE;
                
                row1BButton.setBaseFont(scaledFont4);
                cookieCountLabel.setFont(scaledFont2);
                cookieUnitLabel.setFont(scaledFont2);
                cpsLabel.setFont(scaledFont3);
                
                bakeryNameLabel.setForeground(fontColor);
                cookieCountLabel.setForeground(fontColor);
                cookieUnitLabel.setForeground(fontColor);
                cpsLabel.setForeground(fontColor);
                
                actionButtonrow2B.setFont(scaledFont2);
                actionButtonrow2B.setForeground(fontColor);
                
                centerPanel.revalidate();
                centerPanel.repaint();

                // Force consistent widths for stats rows regardless of content
                SwingUtilities.invokeLater(() -> {
                    // Get the actual width that should be used for all content panels
                    int statsContainerWidth = statsContainer.getWidth();
                    int contentWidth = (int)(statsContainerWidth * 0.7);
                    int spacerWidth = (int)(statsContainerWidth * 0.15);
                    
                    Dimension contentSize = new Dimension(contentWidth, row3Content.getHeight());
                    Dimension spacerSize = new Dimension(spacerWidth, row3Content.getHeight());
                    
                    // Row 3
                    row3Content.setPreferredSize(contentSize);
                    row3Content.setMinimumSize(contentSize);
                    row3Content.setMaximumSize(contentSize);
                    row3LeftSpacer.setPreferredSize(spacerSize);
                    row3RightSpacer.setPreferredSize(spacerSize);
                    
                    // Row 4
                    row4Content.setPreferredSize(contentSize);
                    row4Content.setMinimumSize(contentSize);
                    row4Content.setMaximumSize(contentSize);
                    row4LeftSpacer.setPreferredSize(spacerSize);
                    row4RightSpacer.setPreferredSize(spacerSize);
                    
                    // Row 5
                    row5Content.setPreferredSize(contentSize);
                    row5Content.setMinimumSize(contentSize);
                    row5Content.setMaximumSize(contentSize);
                    row5LeftSpacer.setPreferredSize(spacerSize);
                    row5RightSpacer.setPreferredSize(spacerSize);
                    
                    statsContainer.revalidate();
                    statsContainer.repaint();
                });
            }
        });
        
        return centerPanel;
    }

    private JPanel createEastPanel() {
        // Create the east panel with GridBagLayout
        JPanel eastPanel = new JPanel(new GridBagLayout());
        eastPanel.setBackground(new Color(0xFFFFFF));

        int eastPanelWidth = eastPanel.getWidth();
        int eastPanelHeight = eastPanel.getHeight();
        System.out.println("[DEBUG] IN EAST PANEL DIMENSIONS: " + eastPanelWidth + " x " + eastPanelHeight);

        // Set preferred and maximum size to maintain the 25% scaling
        eastPanel.setPreferredSize(new Dimension(eastPanelWidth, 0));
        eastPanel.setMaximumSize(new Dimension(eastPanelWidth, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // === Row 1 (15% of East Panel Height) ===
        Image eastRow1Image = new ImageIcon("assets/east_row1.png").getImage();
        ImagePanel row1 = new ImagePanel(eastRow1Image);
        row1.setLayout(new BorderLayout());

        gbc.gridy = 0;
        gbc.weighty = 0.15; // 15% of east panel height
        eastPanel.add(row1, gbc);

        // === Row 2 (85% of East Panel Height) ===
        JPanel row2 = new JPanel(new GridBagLayout());
        row2.setBackground(new Color(0xFFFFFF));

        GridBagConstraints row2Gbc = new GridBagConstraints();
        row2Gbc.fill = GridBagConstraints.BOTH;
        row2Gbc.gridx = 0;
        row2Gbc.weightx = 1.0;

        // === Row 2A (15% of row2 height) - Scrollable GridBag with 5 columns ===
        JPanel row2A = new JPanel(new GridBagLayout());
        row2A.setBackground(new Color(0xA7EAA7));

        // Create a grid with 5 columns and temporary number of rows (10)
        GridBagConstraints row2AGbc = new GridBagConstraints();
        row2AGbc.fill = GridBagConstraints.BOTH;
        row2AGbc.weighty = 1.0;
        row2AGbc.weightx = 0.2; // Each column is 20% of the width

        // Add cells to the grid (5 columns x 10 rows)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                JPanel cell = new JPanel();
                cell.setBackground(new Color(0xEAA7A7));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                row2AGbc.gridx = col;
                row2AGbc.gridy = row;
                row2A.add(cell, row2AGbc);
            }
        }

        // Create a scroll pane for row2A
        JScrollPane row2AScroll = new JScrollPane(row2A);
        row2AScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        row2AScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        row2Gbc.gridy = 0;
        row2Gbc.weighty = 0.15; // 15% of row2 height
        row2.add(row2AScroll, row2Gbc);

        // === Row 2B (7.5% of row2 height) - Restore rowCEast functionality ===
        Image row2BImage = new ImageIcon("assets/east_row2B.png").getImage();
        ImagePanel row2B = new ImagePanel(row2BImage);
        row2B.setLayout(new GridBagLayout());

        GridBagConstraints row2BGbc = new GridBagConstraints();
        row2BGbc.fill = GridBagConstraints.BOTH;
        row2BGbc.gridy = 0;
        row2BGbc.weighty = 1.0;

        // Column 1 (Buy/Sell) - 32.5% width
        Image buyImage = new ImageIcon("assets/east_row2B_column1A.png").getImage();
        Image sellImage = new ImageIcon("assets/east_row2B_column1B.png").getImage();

        ImagePanel row2BColumn1 = new ImagePanel(buyImage);
        row2BColumn1.setLayout(new BorderLayout());

        JLabel modeLabel = new JLabel("Buy", SwingConstants.CENTER);
        row2BColumn1.add(modeLabel, BorderLayout.CENTER);
        modeLabel.setVisible(false);

        row2BGbc.gridx = 0;
        row2BGbc.weightx = 0.325; // 32.5% width
        row2B.add(row2BColumn1, row2BGbc);

        // Column 2 (1) - 15% width
        Image row2BColumn2Image = new ImageIcon("assets/east_row2B_column2.png").getImage();
        StaticImageToggleButton row2BColumn2 = new StaticImageToggleButton("1", row2BColumn2Image);

        row2BGbc.gridx = 1;
        row2BGbc.weightx = 0.15; // 15% width
        row2B.add(row2BColumn2, row2BGbc);

        // Column 3 (10) - 17.5% width
        Image row2BColumn3Image = new ImageIcon("assets/east_row2B_column3.png").getImage();
        StaticImageToggleButton row2BColumn3 = new StaticImageToggleButton("10", row2BColumn3Image);

        row2BGbc.gridx = 2;
        row2BGbc.weightx = 0.175; // 17.5% width
        row2B.add(row2BColumn3, row2BGbc);

        // Column 4 (100) - 20% width
        Image row2BColumn4Image = new ImageIcon("assets/east_row2B_column4.png").getImage();
        StaticImageToggleButton row2BColumn4 = new StaticImageToggleButton("100", row2BColumn4Image);

        row2BGbc.gridx = 3;
        row2BGbc.weightx = 0.2; // 20% width
        row2B.add(row2BColumn4, row2BGbc);

        // Column 5 (Switch) - 15% width
        Image row2BColumn5Image = new ImageIcon("assets/east_row2B_column5.png").getImage();
        StaticImageButton row2BColumn5 = new StaticImageButton("S", row2BColumn5Image);

        row2BGbc.gridx = 4;
        row2BGbc.weightx = 0.15; // 15% width
        row2B.add(row2BColumn5, row2BGbc);

        // Set up button group and listeners
        ButtonGroup quantityGroup = new ButtonGroup();
        quantityGroup.add(row2BColumn2);
        quantityGroup.add(row2BColumn3);
        quantityGroup.add(row2BColumn4);

        Color selectedColor = new Color(0xC1FF72); // light green
        Color defaultColor = new Color(0xEAE77D);  // base yellow

        ActionListener quantityListener = e -> {
            AbstractButton source = (AbstractButton) e.getSource();
            
            // Set Global quantity
            if (source == row2BColumn2) Global.setQuantity(1);
            else if (source == row2BColumn3) Global.setQuantity(10);
            else if (source == row2BColumn4) Global.setQuantity(100);
            
            // Update background colors
            row2BColumn2.setBackground(row2BColumn2.isSelected() ? selectedColor : defaultColor);
            row2BColumn3.setBackground(row2BColumn3.isSelected() ? selectedColor : defaultColor);
            row2BColumn4.setBackground(row2BColumn4.isSelected() ? selectedColor : defaultColor);
        };

        row2BColumn2.addActionListener(quantityListener);
        row2BColumn3.addActionListener(quantityListener);
        row2BColumn4.addActionListener(quantityListener);

        row2BColumn5.addActionListener(e -> {
            if ("Buy".equalsIgnoreCase(modeLabel.getText())) {
                modeLabel.setText("Sell");
                row2BColumn1.setImage(sellImage);
                Global.setMode("SELL");
            } else {
                modeLabel.setText("Buy");
                row2BColumn1.setImage(buyImage);
                Global.setMode("BUY");
            }
        });

        // Add to tracking lists
        genLabel.add(modeLabel);
        genToggleButtons.add(row2BColumn2);
        genToggleButtons.add(row2BColumn3);
        genToggleButtons.add(row2BColumn4);
        genButtons.add(row2BColumn5);

        row2Gbc.gridy = 1;
        row2Gbc.weighty = 0.075; // 7.5% of row2 height
        row2.add(row2B, row2Gbc);

        // === Row 2C (remaining height of row2) - Using existing rowDEast functionality ===
        Image row2CImage = new ImageIcon("assets/east_row2C.png").getImage();
        row2C = new TiledImagePanel(row2CImage);
        row2C.setLayout(new GridLayout(0, 1)); // Dynamic row count

        JScrollPane scrollableUpgrades = new JScrollPane(row2C);
        scrollableUpgrades.getViewport().setBackground(new Color(0xE59C9C));
        scrollableUpgrades.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollableUpgrades.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        row2Gbc.gridy = 2;
        row2Gbc.weighty = 0.775; // Remaining height (77.5% of row2)
        row2.add(scrollableUpgrades, row2Gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.85; // 85% of east panel height
        eastPanel.add(row2, gbc);

        // Add resize listener to handle component sizing
        eastPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int eastWidth = eastPanel.getWidth();
                int eastHeight = eastPanel.getHeight();
                
                // Calculate row heights
                int row2Height = (int)(eastHeight * 0.85);
                int row2AHeight = (int)(row2Height * 0.15);
                int row2BHeight = (int)(row2Height * 0.075);
                int row2CHeight = (int)(row2Height * 0.775);
                
                // Update row2A cell sizes
                Component[] row2AComponents = row2A.getComponents();
                for (Component comp : row2AComponents) {
                    if (comp instanceof JPanel) {
                        comp.setPreferredSize(new Dimension((int)(eastWidth * 0.2), (int)(row2AHeight * 0.1)));
                        comp.setMinimumSize(comp.getPreferredSize());
                        comp.setMaximumSize(comp.getPreferredSize());
                    }
                }
                
                // Update row2B column sizes
                row2BColumn1.setPreferredSize(new Dimension((int)(eastWidth * 0.325), row2BHeight));
                row2BColumn2.setPreferredSize(new Dimension((int)(eastWidth * 0.15), row2BHeight));
                row2BColumn3.setPreferredSize(new Dimension((int)(eastWidth * 0.175), row2BHeight));
                row2BColumn4.setPreferredSize(new Dimension((int)(eastWidth * 0.2), row2BHeight));
                row2BColumn5.setPreferredSize(new Dimension((int)(eastWidth * 0.15), row2BHeight));
                
                // Update upgrade button dimensions for row2C (rowDEast)
                upgradeButtonWidth = (int) (eastWidth - 18);
                upgradeButtonHeight = (int) (row2Height * 0.14);
                
                // Resize upgrade wrappers
                for (JPanel wrapper : upgradeWrappers) {
                    wrapper.setPreferredSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                    wrapper.setMinimumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                    wrapper.setMaximumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                }
                
                eastPanel.revalidate();
                eastPanel.repaint();
            }
        });

        return eastPanel;
    }

    // Method to add an upgrade to the UI
    public void addUpgrade(Upgrade upgrade) {
        upgrades.add(upgrade);
        upgradePastStage1.add(false); // Initialize as not past Stage 1

        // Create a custom button for the upgrade
        UpgradeButton upgradeBtn = new UpgradeButton(upgrade, upgrades.size() - 1);
        upgradePanels.add(upgradeBtn);
        upgradeButtons.add(upgradeBtn); // Add to the upgradeButtons list as well

        // Set initial size for the button
        upgradeBtn.setPreferredSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
        upgradeBtn.setMinimumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
        upgradeBtn.setMaximumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
        
        // Add action listener to the button
        upgradeBtn.addActionListener(e -> {
            if (gamestate.Transact(upgrade, Global.getQuantity(), Global.getMode())) {
                // Update to stage 3 after purchase
                upgradeBtn.updateStage(3, gamestate.getAmount());
                updateDisplay();
            }
        });

        // Create a wrapper panel for the upgrade button with no gaps
        JPanel wrapper = new JPanel(new BorderLayout(0, 0)); // Zero horizontal and vertical gaps
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No border
        wrapper.add(upgradeBtn, BorderLayout.CENTER);
        
        // Initially hide the upgrade
        wrapper.setVisible(false);
        upgradeWrappers.add(wrapper);

        // Add the wrapper to the availablePanel
        row2C.add(wrapper);

        // Make sure the panel is updated
        row2C.revalidate();
        row2C.repaint();

        System.out.println("Added upgrade: " + upgrade.getName());
    }

    // Method to update upgrade visibility based on cookie count
    private void updateUpgradeVisibility(int index, double cookieCount) {
        if (index >= upgradeWrappers.size()) return;
        
        Upgrade upgrade = upgrades.get(index);
        JPanel wrapper = upgradeWrappers.get(index);
        UpgradeButton upgradeBtn = upgradePanels.get(index);
        
        double cost = upgrade.getCost(Global.getQuantity());
        
        // Check if we should move past Stage 1 (regardless of visibility)
        if (cookieCount >= cost * 0.25 && !upgradePastStage1.get(index)) {
            upgradePastStage1.set(index, true);
            // Debug output
            System.out.println("Upgrade " + upgrade.getName() + " passed Stage 1");
        }
        
        // Count how many Stage 1 upgrades are currently visible
        int visibleStage1Count = 0;
        for (int i = 0; i < upgrades.size(); i++) {
            if (i < upgradeWrappers.size() && upgradeWrappers.get(i).isVisible() && !upgradePastStage1.get(i)) {
                visibleStage1Count++;
            }
        }
        
        // If this is the first upgrade or the previous one is visible
        boolean shouldBeVisible = (index == 0) || 
                        (index > 0 && upgradeWrappers.get(index-1).isVisible());
        
        // Only show if it should be visible and we haven't exceeded the Stage 1 limit
        // or if this upgrade has already passed Stage 1
        if (shouldBeVisible && (visibleStage1Count < 2 || upgradePastStage1.get(index))) {
            wrapper.setVisible(true);
            
            // If this upgrade hasn't passed Stage 1 yet
            if (!upgradePastStage1.get(index)) {
                // Stage 1: First reveal with 85% transparency overlay
                upgradeBtn.setEnabled(false);
                upgradeBtn.updateStage(1, cookieCount); // Show ??? for name with 85% overlay
            } 
            // If this upgrade has passed Stage 1
            else {
                if (Global.getMode().equals("SELL")) {
                    if (upgrade.getOwned() >= Global.getQuantity()) {
                        upgradeBtn.setEnabled(true);
                        upgradeBtn.updateStage(3, cookieCount); // No overlay
                    } else {
                        upgradeBtn.setEnabled(false);
                        upgradeBtn.updateStage(2, cookieCount); // 35% overlay
                    }
                } else {
                    // BUY mode logic
                    if (cookieCount >= cost) {
                        upgradeBtn.setEnabled(true);
                        upgradeBtn.updateStage(3, cookieCount); // No overlay
                    } else {
                        upgradeBtn.setEnabled(false);
                        upgradeBtn.updateStage(2, cookieCount); // 35% overlay
                    }
                }
            }
        }
        
        // If this upgrade is visible and past Stage 1, show the next upgrade if it exists
        if (wrapper.isVisible() && upgradePastStage1.get(index) && index + 1 < upgradeWrappers.size()) {
            // Only show the next upgrade if it's not already visible and we have fewer than 2 Stage 1 upgrades
            int currentStage1Count = 0;
            for (int i = 0; i < upgrades.size(); i++) {
                if (i < upgradeWrappers.size() && upgradeWrappers.get(i).isVisible() && !upgradePastStage1.get(i)) {
                    currentStage1Count++;
                }
            }
            
            if (!upgradeWrappers.get(index + 1).isVisible() && currentStage1Count < 2) {
                upgradeWrappers.get(index + 1).setVisible(true);
                upgradePanels.get(index + 1).setEnabled(false);
                upgradePanels.get(index + 1).updateStage(1, cookieCount); // 85% overlay
                // Debug output
                System.out.println("Showing next upgrade: " + upgrades.get(index + 1).getName() + " in Stage 1");
            }
        }
    }
    
    // Method to update the display with current game state
    private void updateDisplay() {
        double amount = gamestate.getAmount();
        double cps = gamestate.GetCPS();

        // Format the numbers for display using NumberFormatter
        String formattedAmount = NumberFormatter.formatNumber(amount);
        String unit = NumberFormatter.getUnit(amount);
        String formattedCPS = NumberFormatter.formatCPS(cps);
        String cpsUnit = NumberFormatter.getUnit(cps);

        // Update the labels
        cookieCountLabel.setText(formattedAmount);
        cookieUnitLabel.setText(unit + " Cookies");
        cpsLabel.setText(formattedCPS + " " + cpsUnit + " per second");

        // Update upgrade buttons
        for (int i = 0; i < upgrades.size(); i++) {
            if (i < upgradeButtons.size()) {
                // Update visibility and appearance based on cookie count
                updateUpgradeVisibility(i, amount);
            }
        }
    }

    // OPTIMIZED: Enhanced FloatingText class with better performance
    public class FloatingText {
        String text;
        double x, y; // Use double for smoother movement
        double vx, vy; // Velocity components
        int lifetime = 0;
        int maxLifetime = 90; // 3 seconds at 30fps
        double scale = 1.0;

        public FloatingText(String text, int startX, int startY) {
            this.text = text;
            this.x = startX;
            this.y = startY;
            
            // Add slight random velocity like original Cookie Clicker
            this.vx = (Math.random() - 0.5) * 2; // Random horizontal drift
            this.vy = -2.5 - Math.random() * 1.5; // Upward movement with variation
            
            // Start slightly larger then shrink (like original)
            this.scale = 1.2;
        }

        public boolean update() {
            // Update position with velocity
            x += vx;
            y += vy;
            
            // Slow down horizontal movement over time
            vx *= 0.98;
            
            // Gravity effect (slow down upward movement)
            vy += 0.05;
            
            // Scale animation (shrink over time like original)
            if (lifetime < 20) {
                scale = 1.2 - (lifetime * 0.01); // Shrink from 1.2 to 1.0
            } else {
                scale = Math.max(0.1, 1.0 - ((lifetime - 20) * 0.015)); // Continue shrinking
            }
            
            lifetime++;
            return lifetime >= maxLifetime;
        }

        // OPTIMIZED: Use pre-calculated alpha composites
        public void drawOptimized(Graphics2D g2d, AlphaComposite[] alphaComposites) {
            if (lifetime >= alphaComposites.length) return;
            
            // Save original transform
            AffineTransform originalTransform = g2d.getTransform();
            
            // Apply scale and position
            g2d.translate(x, y);
            g2d.scale(scale, scale);
            
            // Apply alpha
            g2d.setComposite(alphaComposites[Math.min(lifetime, alphaComposites.length - 1)]);
            
            // Draw text centered
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            
            g2d.drawString(text, -textWidth / 2, textHeight / 4);
            
            // Restore transform and composite
            g2d.setTransform(originalTransform);
            g2d.setComposite(AlphaComposite.SrcOver);
        }
    }

    // OPTIMIZED: Simplified floating text creation (like original Cookie Clicker)
    private void showFloatingTextOptimized(int x, int y, String text) {
        // Simple limit check - remove oldest if too many
        if (floatingTexts.size() >= MAX_FLOATING_TEXTS) {
            floatingTexts.remove(0); // Remove oldest
        }
        
        floatingTexts.add(new FloatingText(text, x, y));
        
        System.out.println("[DEBUG] Added floating text: " + text + " at (" + x + ", " + y + ") - Total: " + floatingTexts.size());
    }
}
