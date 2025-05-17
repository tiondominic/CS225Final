import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
//import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import javax.imageio.ImageIO;
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
        if (number < 1_000) {
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
            scale = 0.8;
        } else if (isHovered) {
            scale = 0.95;
        } else if (isToggled) {
            scale = 0.9; // Slight visual feedback for toggled-on
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
        updateLabelFontScale(); // 🔥 Apply animation scale to label text

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
    private final Image backgroundImage;

    public TiledImagePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
class UpgradePanel extends JPanel {
    private final JLabel imageLabel;
    private final JLabel nameLabel;
    private final JLabel costLabel;
    private final JLabel ownedLabel;
    private final Color RED_TEXT = new Color(0xFF0000);
    private final Color GREEN_TEXT = new Color(0x00AA00);
    private final Upgrade upgrade;
    
    public UpgradePanel(Upgrade upgrade) {
        this.upgrade = upgrade;
        setLayout(new BorderLayout());

        // Create a panel with GridLayout for the three columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 3));
        contentPanel.setOpaque(false);
        
        // Column 1: Image placeholder (20% width)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        imageLabel = new JLabel("[IMG]");
        imageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        imageLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Column 2: Name and cost (50% width)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        nameLabel = new JLabel(upgrade.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        
        double cost = upgrade.getCost(Global.getQuantity());
        String formattedCost = NumberFormatter.formatNumber(cost);
        String unit = NumberFormatter.getUnit(cost);
        costLabel = new JLabel("Cost: " + formattedCost + " " + unit);

        costLabel.setHorizontalAlignment(SwingConstants.CENTER);
        costLabel.setForeground(RED_TEXT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(costLabel);
        
        // Column 3: Owned count (30% width)
        JPanel ownedPanel = new JPanel(new BorderLayout());
        ownedPanel.setOpaque(false);
        ownedLabel = new JLabel(String.valueOf(upgrade.getOwned()));
        ownedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ownedLabel.setFont(ownedLabel.getFont().deriveFont(Font.BOLD, 18f));
        ownedLabel.setBorder(new EmptyBorder(5, 5, 5, 10));
        ownedPanel.add(ownedLabel, BorderLayout.CENTER);
        
        // Add the three columns to the content panel with appropriate weights
        contentPanel.add(imagePanel);
        contentPanel.add(infoPanel);
        contentPanel.add(ownedPanel);
        
        // Add the content panel to this panel
        add(contentPanel, BorderLayout.CENTER);
        
        // Set initial opacity
        setOpaque(true);
    }
    
    public void updateStage(int stage, double cookieCount) {
        // Update owned count
        ownedLabel.setText(String.valueOf(upgrade.getOwned()));

        // Update name based on stage
        if (stage == 1) {
            nameLabel.setText("???");
        } else {
            nameLabel.setText(upgrade.getName());
        }

        // Use shared NumberFormatter for cost formatting
        double cost = upgrade.getCost(Global.getQuantity());
        String formattedCost = NumberFormatter.formatNumber(cost);
        String unit = NumberFormatter.getUnit(cost);
        costLabel.setText("Cost: " + formattedCost + " " + unit);

        // Set cost color based on stage
        if (stage == 3) {
            costLabel.setForeground(GREEN_TEXT);
        } else {
            costLabel.setForeground(RED_TEXT);
        }
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
    private JPanel rowDEast;
    private int upgradeButtonWidth;
    private int upgradeButtonHeight;
    private List<JPanel> upgradeWrappers = new ArrayList<>();
    private List<UpgradePanel> upgradePanels = new ArrayList<>();
    private final List<FloatingLabel> floatingLabels = new ArrayList<>();

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

        // Set size to 75% width and 75% height of the screen, and center it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int modifiedwidth = (int) (screenSize.width - 16);
        int modifiedheight = (int) (screenSize.height - 39);
        
        int width = (int) (modifiedwidth * 0.75);
        int height = (int) (modifiedheight * 0.75);

        setSize(width, height);
        setLocationRelativeTo(null); // Centers the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        System.out.println("[DEBUG] MINIMIZED WINDOW DIMENSIONS: " + width + "x" + height);

        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        // Create the three main panels with GridBagLayout
        JPanel westPanel = createWestPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel eastPanel = createEastPanel();

        // Add panels to the frame
        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);

        // Set up UI update timer (16ms = ~60fps)
        uiUpdateTimer = new Timer(16, e -> updateDisplay());
        uiUpdateTimer.start();

        Timer floatingLabelTimer = new Timer(16, e -> {
            Iterator<FloatingLabel> iter = floatingLabels.iterator();
            while (iter.hasNext()) {
                FloatingLabel fl = iter.next();
                if (fl.update()) {
                    getLayeredPane().remove(fl.label);
                    iter.remove();
                }
            }
            getLayeredPane().repaint();
        });
        floatingLabelTimer.start();

        setVisible(true);

        // After visible: dynamically size the side panels
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int currentWidth = (getContentPane().getWidth() - 16);
                int currentHeight = (getContentPane().getHeight() + 24);

                System.out.println("[DEBUG] WINDOW DIMENSIONS: " + currentWidth + "x" + currentHeight);
                
                int sidePanelWidth = (int) (currentWidth * 0.25);

                westPanel.setPreferredSize(new Dimension(sidePanelWidth, currentHeight));
                westPanel.setMinimumSize(new Dimension(sidePanelWidth, currentHeight));
                westPanel.setMaximumSize(new Dimension(sidePanelWidth, Integer.MAX_VALUE));
                eastPanel.setPreferredSize(new Dimension(sidePanelWidth, currentHeight));
                eastPanel.setMinimumSize(new Dimension(sidePanelWidth, currentHeight));
                eastPanel.setMaximumSize(new Dimension(sidePanelWidth, Integer.MAX_VALUE));

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
        });
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
        Image bgImage0 = new ImageIcon("assets/center_rowACenter_1.png").getImage();
        ImagePanel rowACenter = new ImagePanel(bgImage0);
        rowACenter.setLayout(new GridBagLayout());
        centerRows.add(rowACenter);
        
        GridBagConstraints rowAGbc = new GridBagConstraints();
        rowAGbc.fill = GridBagConstraints.BOTH;
        rowAGbc.weightx = 1.0;
        rowAGbc.weighty = 1.0;
        
        // Add horizontal padding
        rowAGbc.gridx = 0;
        rowAGbc.gridy = 0;
        rowAGbc.weightx = 0.005; // 1.4% width
        rowACenter.add(Box.createHorizontalStrut(0), rowAGbc);
        
        // === Column 1 (2 Buttons) ===
        JPanel column1A = new JPanel(new GridLayout(2, 1));
        column1A.setOpaque(false);
        
        Image staticButtonImage1 = new ImageIcon("assets/center_row1_columnA1.png").getImage();
        StaticImageButton column1AButton1 = new StaticImageButton("Options", staticButtonImage1);
        column1AButton1.addActionListener(e -> System.out.println("Button 1 clicked"));
        firstRowButtons.add(column1AButton1);
        
        JPanel button1Wrapper = new JPanel(new BorderLayout());
        button1Wrapper.setOpaque(false);
        button1Wrapper.add(column1AButton1, BorderLayout.CENTER);
        column1A.add(button1Wrapper);
        
        Image staticButtonImage2 = new ImageIcon("assets/center_row1_columnA2.png").getImage();
        StaticImageButton column1AButton2 = new StaticImageButton("Options", staticButtonImage2);
        column1AButton2.addActionListener(e -> System.out.println("Button 2 clicked"));
        firstRowButtons.add(column1AButton2);
        
        JPanel button2Wrapper = new JPanel(new BorderLayout());
        button2Wrapper.setOpaque(false);
        button2Wrapper.add(column1AButton2, BorderLayout.CENTER);
        column1A.add(button2Wrapper);
        
        rowAGbc.gridx = 1;
        rowAGbc.weightx = 0.175; // 16.875% width
        rowACenter.add(column1A, rowAGbc);
        
        // === Column 2 (Middle Label) ===
        Image bgImage2 = new ImageIcon("assets/center_row1_columnB1.png").getImage();
        ImagePanel column2A = new ImagePanel(bgImage2);
        column2A.setOpaque(false);
        
        rowAGbc.gridx = 2;
        rowAGbc.weightx = 0.55; // 60.65% width
        rowACenter.add(column2A, rowAGbc);
        
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
        rowAGbc.weightx = 0.175; // 16.875% width
        rowACenter.add(column3A, rowAGbc);
        
        // Add horizontal padding
        rowAGbc.gridx = 4;
        rowAGbc.weightx = 0.005; // 1.4% width
        rowACenter.add(Box.createHorizontalStrut(1), rowAGbc);
        
        gbc.gridy = 0;
        gbc.weighty = 0.15; // 15% height
        centerPanel.add(rowACenter, gbc);
        
        // === Row B (Content) ===
        Image bgImage = new ImageIcon("assets/main_background_1.png").getImage();
        ImagePanel rowBCenter = new ImagePanel(bgImage);
        rowBCenter.setLayout(new GridBagLayout());
        centerRows.add(rowBCenter);
        
        GridBagConstraints rowBGbc = new GridBagConstraints();
        rowBGbc.fill = GridBagConstraints.HORIZONTAL;
        rowBGbc.gridx = 0;
        rowBGbc.anchor = GridBagConstraints.CENTER;
        rowBGbc.weightx = 1.0;
        
        // Add vertical padding
        rowBGbc.gridy = 0;
        rowBGbc.weighty = 0.0375; // 3.75% height
        rowBCenter.add(Box.createVerticalStrut(1), rowBGbc);
        
        // Row 1 (Bakery name)
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
        rowBGbc.weighty = 0.075; // 7.5% height
        rowBCenter.add(row1B, rowBGbc);
        secondRowPanels.add(row1B);
        
        // Row 2 (Cookie button)
        JPanel row2B = new JPanel(new BorderLayout());
        row2B.setOpaque(false);
        
        Image img = new ImageIcon("assets/main_cookie_3.png").getImage();
        RotatingImageButton actionButtonrow2B = new RotatingImageButton(null, img);
        
        actionButtonrow2B.addActionListener(e -> {
            gamestate.Click();
            updateDisplay();
            
            Point screenPoint = MouseInfo.getPointerInfo().getLocation();
            Point framePoint = getLayeredPane().getLocationOnScreen();
            int clickX = screenPoint.x - framePoint.x;
            int clickY = screenPoint.y - framePoint.y;
            
            showFloatingText(clickX, clickY, "+" + NumberFormatter.formatNumber(gamestate.getClickingPower()));
        });
        
        row2B.add(actionButtonrow2B, BorderLayout.CENTER);
        
        rowBGbc.gridy = 2;
        rowBGbc.weighty = 0.475; // 47.5% height
        rowBCenter.add(row2B, rowBGbc);
        secondRowPanels.add(row2B);
        
        // Row 3 (Cookie count)
        ImagePanel row3B = new ImagePanel(new ImageIcon("assets/center_row2_rowC.png").getImage());
        row3B.setLayout(new GridBagLayout());
        
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.weighty = 1.0;
        gbcBottom.weightx = 1.0;
        gbcBottom.insets = new Insets(16, 0, 0, 0);
        gbcBottom.anchor = GridBagConstraints.SOUTH;
        
        cookieCountLabel = new JLabel("0.0");
        row3B.add(cookieCountLabel, gbcBottom);
        
        rowBGbc.gridy = 3;
        rowBGbc.weighty = 0.075; // 7.5% height
        rowBCenter.add(row3B, rowBGbc);
        secondRowPanels.add(row3B);
        
        // Row 4 (Cookie unit)
        ImagePanel row4B = new ImagePanel(new ImageIcon("assets/center_row2_rowD.png").getImage());
        row4B.setLayout(new GridBagLayout());
        
        GridBagConstraints gbcTop1 = new GridBagConstraints();
        gbcTop1.gridx = 0;
        gbcTop1.gridy = 0;
        gbcTop1.weighty = 1.0;
        gbcTop1.weightx = 1.0;
        gbcTop1.anchor = GridBagConstraints.NORTH;
        
        cookieUnitLabel = new JLabel("Cookies");
        row4B.add(cookieUnitLabel, gbcTop1);
        
        rowBGbc.gridy = 4;
        rowBGbc.weighty = 0.075; // 7.5% height
        rowBCenter.add(row4B, rowBGbc);
        secondRowPanels.add(row4B);
        
        // Row 5 (CPS)
        ImagePanel row5B = new ImagePanel(new ImageIcon("assets/center_row2_rowE.png").getImage());
        row5B.setLayout(new GridBagLayout());
        
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.weighty = 1.0;
        gbcTop.weightx = 1.0;
        gbcTop.insets = new Insets(0, 0, 20, 0);
        gbcTop.anchor = GridBagConstraints.NORTH;
        
        cpsLabel = new JLabel("0.0 per second");
        row5B.add(cpsLabel, gbcTop);
        
        rowBGbc.gridy = 5;
        rowBGbc.weighty = 0.075; // 7.5% height
        rowBCenter.add(row5B, rowBGbc);
        secondRowPanels.add(row5B);
        
        // Add vertical padding
        rowBGbc.gridy = 6;
        rowBGbc.weighty = 0.0375; // 3.75% height
        rowBCenter.add(Box.createVerticalStrut(1), rowBGbc);
        
        // Add vertical glue to absorb extra space
        rowBGbc.gridy = 7;
        rowBGbc.weighty = 0.15; // 15% height
        rowBCenter.add(Box.createVerticalGlue(), rowBGbc);
        
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
                
                // Calculate column widths for Row A
                int rowAColumnAWidth = (int)(panelWidth * 0.16875);
                int rowAColumnBWidth = (int)(panelWidth * 0.6065);
                
                // Update button sizes for Row A
                for (JButton btn : firstRowButtons) {
                    btn.setPreferredSize(new Dimension(rowAColumnAWidth, (int)(panelHeight * 0.075)));
                }
                
                // Calculate sizes for Row B elements
                int rowBWidth = (int)(panelWidth * 0.6625);
                
                // Update Row B elements
                row1BButton.setPreferredSize(new Dimension(rowBWidth, (int)(panelHeight * 0.075)));
                actionButtonrow2B.setPreferredSize(new Dimension((int)(rowBWidth * 0.9), (int)(rowBHeight * 0.475 * 0.9)));
                
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
        System.out.println("[DEBUG] IN WEST PANEL DIMENSIONS: " + eastPanelWidth + " x " + eastPanelHeight);
        
        // Set preferred and maximum size to maintain the 25% scaling
        eastPanel.setPreferredSize(new Dimension(eastPanelWidth, 0));
        eastPanel.setMaximumSize(new Dimension(eastPanelHeight, Integer.MAX_VALUE));
        
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
        Image row2BImage = new ImageIcon("assets/east_row3.png").getImage();
        ImagePanel row2B = new ImagePanel(row2BImage);
        row2B.setLayout(new GridBagLayout());
        
        GridBagConstraints row2BGbc = new GridBagConstraints();
        row2BGbc.fill = GridBagConstraints.BOTH;
        row2BGbc.gridy = 0;
        row2BGbc.weighty = 1.0;
        
        // Column 1 (Buy/Sell) - 32.5% width
        Image buyImage = new ImageIcon("assets/east_row3_column1_Buy.png").getImage();
        Image sellImage = new ImageIcon("assets/east_row3_column1_Sell.png").getImage();
        
        ImagePanel row2BColumn1 = new ImagePanel(buyImage);
        row2BColumn1.setLayout(new BorderLayout());
        
        JLabel modeLabel = new JLabel("Buy", SwingConstants.CENTER);
        row2BColumn1.add(modeLabel, BorderLayout.CENTER);
        modeLabel.setVisible(false);
        
        row2BGbc.gridx = 0;
        row2BGbc.weightx = 0.325; // 32.5% width
        row2B.add(row2BColumn1, row2BGbc);
        
        // Column 2 (1) - 15% width
        Image row2BColumn2Image = new ImageIcon("assets/east_row3_column2.png").getImage();
        StaticImageToggleButton row2BColumn2 = new StaticImageToggleButton("1", row2BColumn2Image);
        
        row2BGbc.gridx = 1;
        row2BGbc.weightx = 0.15; // 15% width
        row2B.add(row2BColumn2, row2BGbc);
        
        // Column 3 (10) - 17.5% width
        Image row2BColumn3Image = new ImageIcon("assets/east_row3_column3.png").getImage();
        StaticImageToggleButton row2BColumn3 = new StaticImageToggleButton("10", row2BColumn3Image);
        
        row2BGbc.gridx = 2;
        row2BGbc.weightx = 0.175; // 17.5% width
        row2B.add(row2BColumn3, row2BGbc);
        
        // Column 4 (100) - 20% width
        Image row2BColumn4Image = new ImageIcon("assets/east_row3_column4.png").getImage();
        StaticImageToggleButton row2BColumn4 = new StaticImageToggleButton("100", row2BColumn4Image);
        
        row2BGbc.gridx = 3;
        row2BGbc.weightx = 0.2; // 20% width
        row2B.add(row2BColumn4, row2BGbc);
        
        // Column 5 (Switch) - 15% width
        Image row2BColumn5Image = new ImageIcon("assets/east_row3_column5.png").getImage();
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
        Image rowDEastImage = new ImageIcon("assets/east_row5.png").getImage();
        rowDEast = new ImagePanel(rowDEastImage);
        rowDEast.setLayout(new GridLayout(0, 1)); // Dynamic row count
        
        JScrollPane scrollableUpgrades = new JScrollPane(rowDEast);
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
                    }
                }
                
                // Update upgrade button dimensions for row2C (rowDEast)
                upgradeButtonWidth = eastWidth;
                upgradeButtonHeight = (int)(row2CHeight * 0.14);
                
                // Resize upgrade wrappers
                for (JPanel wrapper : upgradeWrappers) {
                    wrapper.setPreferredSize(new Dimension(eastWidth - 18, upgradeButtonHeight));
                    wrapper.setMinimumSize(new Dimension(eastWidth - 18, upgradeButtonHeight));
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

        // Create a custom panel for the upgrade
        UpgradePanel upgradePanel = new UpgradePanel(upgrade);
        upgradePanels.add(upgradePanel);
        
        // Create a button for the upgrade
        JButton upgradeBtn = new JButton();
        upgradeBtn.setContentAreaFilled(false);
        upgradeBtn.setBorderPainted(false);
        upgradeBtn.setLayout(new BorderLayout());
        upgradeBtn.add(upgradePanel, BorderLayout.CENTER);
        
        upgradeButtons.add(upgradeBtn);

        // Set initial size for the button
        upgradeBtn.setPreferredSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
        upgradeBtn.setMinimumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
        upgradeBtn.setMaximumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));

        // Set initial background color
        upgradePanel.setBackground(STAGE1_BG);
        
        // Add action listener to the button
        upgradeBtn.addActionListener(e -> {
            if (gamestate.Transact(upgrade, Global.getQuantity(), Global.getMode())) {
                // Update to stage 3 after purchase
                upgradePanel.updateStage(3, gamestate.getAmount());
                upgradePanel.setBackground(STAGE3_BG);
                updateDisplay();
            }
        });

        // Create a wrapper panel for the upgrade button
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(upgradeBtn);
        
        // Initially hide the upgrade
        wrapper.setVisible(false);
        upgradeWrappers.add(wrapper);

        // Add the wrapper to the availablePanel
        rowDEast.add(wrapper);

        // Make sure the panel is updated
        rowDEast.revalidate();
        rowDEast.repaint();

        System.out.println("Added upgrade: " + upgrade.getName());
    }

    // Method to update upgrade visibility based on cookie count
    private void updateUpgradeVisibility(int index, double cookieCount) {
        if (index >= upgradeWrappers.size()) return;
        
        Upgrade upgrade = upgrades.get(index);
        JPanel wrapper = upgradeWrappers.get(index);
        UpgradePanel upgradePanel = upgradePanels.get(index);
        
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
                // Stage 1: First reveal
                upgradeButtons.get(index).setEnabled(false);
                upgradePanel.setBackground(STAGE1_BG); // Dark gray
                upgradePanel.updateStage(1, cookieCount); // Show ??? for name
            } 
            // If this upgrade has passed Stage 1
            else {
                if (Global.getMode().equals("SELL")) {
                    if (upgrade.getOwned() >= Global.getQuantity()) {
                        upgradeButtons.get(index).setEnabled(true);
                        upgradePanel.setBackground(STAGE3_BG); // Selling is available
                        upgradePanel.updateStage(3, cookieCount); // Show real name, maybe different label?
                    } else {
                        upgradeButtons.get(index).setEnabled(false);
                        upgradePanel.setBackground(STAGE2_BG); // Still gray
                        upgradePanel.updateStage(2, cookieCount); // Show real name
                    }
                } else {
                    // BUY mode logic
                    if (cookieCount >= cost) {
                        upgradeButtons.get(index).setEnabled(true);
                        upgradePanel.setBackground(STAGE3_BG); // Original color
                        upgradePanel.updateStage(3, cookieCount); // Show real name with green cost
                    } else {
                        upgradeButtons.get(index).setEnabled(false);
                        upgradePanel.setBackground(STAGE2_BG); // Lighter gray
                        upgradePanel.updateStage(2, cookieCount); // Show real name with red cost
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
                upgradeButtons.get(index + 1).setEnabled(false);
                upgradePanels.get(index + 1).setBackground(STAGE1_BG);
                upgradePanels.get(index + 1).updateStage(1, cookieCount);
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

    private class FloatingLabel {
        JLabel label;
        int lifetime = 0;
        int maxLifetime = 60;

        public FloatingLabel(JLabel label) {
            this.label = label;
        }

        public boolean update() {
            int dy = 3;
            float opacity = 1.0f - (lifetime / (float) maxLifetime);
            label.setLocation(label.getX(), label.getY() - dy);
            label.setForeground(new Color(255, 255, 255, Math.max(0, (int) (255 * opacity))));
            lifetime++;
            return lifetime >= maxLifetime;
        }
    }

    private void showFloatingText(int x, int y, String text) {
        int frameHeight = getHeight();
        
        // Calculate font size as a percentage of the frame height
        int baseFontSize = (int) (frameHeight * 0.03);  // Adjust this percentage as needed
        
        // Create label and set properties
        JLabel label = new JLabel(text);
        label.setFont(new Font("Garamond", Font.BOLD, baseFontSize));  // Use dynamically calculated font size
        label.setForeground(new Color(255, 255, 255));
        label.setSize(label.getPreferredSize());
        label.setLocation(x, y);
        label.setOpaque(false);
        label.setBorder(null);
        
        // Add label to layered pane
        getLayeredPane().add(label, JLayeredPane.POPUP_LAYER);
        floatingLabels.add(new FloatingLabel(label));
    }
}
