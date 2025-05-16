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
        int width = (int) (screenSize.width * 0.75);
        int height = (int) (screenSize.height * 0.75);
        setSize(width, height);
        setLocationRelativeTo(null); // Centers the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        System.out.println("DEBUG SCREEN WIDTH: " + screenSize.width);
        System.out.println("DEBUG SCREEN HEIGHT: " + screenSize.height);

        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        // Calculate panel widths
        int westPanelWidth = (int) (width * 0.275);  // 25% of frame width
        int eastPanelWidth = (int) (width * 0.275);  // 25% of frame width

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
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        
        // === Row A (Title) ===
        Image westBgImage1 = new ImageIcon("assets/west_row1.png").getImage();
        ImagePanel rowAWest = new ImagePanel(westBgImage1);
        rowAWest.setLayout(new GridBagLayout());
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1125; // 11.25% height
        westPanel.add(rowAWest, gbc);
        
        // === Row B (3 + 5 Buttons) ===
        JPanel rowBWest = new JPanel(new GridLayout(2, 1));
        rowBWest.setBackground(new Color(0xA7EAA7));
        
        JPanel rowBTop = new JPanel(new GridLayout(1, 3));
        rowBTop.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 3; i++) {
            JButton btn = new JButton("Top " + (i + 1));
            btn.setBackground(new Color(0xEAE77D));
            btn.addActionListener(e -> { new ChaosElements(gamestate).setUp(); }); // TEMPORARY SETUP GOLDEN COOKIE
            topButtons.add(btn);
            rowBTop.add(btn);
        }
        
        JPanel rowBBottom = new JPanel(new GridLayout(1, 5));
        rowBBottom.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 5; i++) {
            JButton btn = new JButton("Bottom " + (i + 1));
            btn.setBackground(new Color(0xF7F4B7));
            bottomButtons.add(btn);
            rowBBottom.add(btn);
        }
        
        rowBWest.add(rowBTop);
        rowBWest.add(rowBBottom);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.1125; // 11.25% height
        westPanel.add(rowBWest, gbc);
        
        // === Row C (Scrollable Purchased Panel) ===
        JPanel purchasedPanel = new JPanel(new GridLayout(purchasedUpgrades, 1));
        purchasedPanel.setBackground(new Color(0x7B89C4));
        
        for (int i = 0; i < purchasedUpgrades; i++) {
            JLabel lbl = new JLabel("Purchased Upgrade " + (i + 1), SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(new Color(0xF7F4B7));
            purchasedLabels.add(lbl);
            
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.add(lbl, BorderLayout.CENTER);
            purchasedPanel.add(wrapper);
        }
        
        JScrollPane scrollPurchased = new JScrollPane(purchasedPanel);
        scrollPurchased.getViewport().setBackground(new Color(0xE59C9C));
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.775; // 77.5% height
        westPanel.add(scrollPurchased, gbc);
        
        // Add resize listener to handle component sizing
        westPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = westPanel.getWidth();
                int panelHeight = westPanel.getHeight();
                
                // Update button sizes
                for (JButton btn : topButtons) {
                    btn.setPreferredSize(new Dimension(panelWidth / 3, (int)(panelHeight * 0.05625)));
                }
                
                for (JButton btn : bottomButtons) {
                    btn.setPreferredSize(new Dimension(panelWidth / 5, (int)(panelHeight * 0.05625)));
                }
                
                for (JLabel lbl : purchasedLabels) {
                    lbl.setPreferredSize(new Dimension(panelWidth, (int)(panelHeight * 0.25)));
                }
                
                westPanel.revalidate();
                westPanel.repaint();
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
        rowAGbc.weightx = 0.014; // 1.4% width
        rowACenter.add(Box.createHorizontalStrut(1), rowAGbc);
        
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
        rowAGbc.weightx = 0.16875; // 16.875% width
        rowACenter.add(column1A, rowAGbc);
        
        // === Column 2 (Middle Label) ===
        Image bgImage2 = new ImageIcon("assets/center_row1_columnB1.png").getImage();
        ImagePanel column2A = new ImagePanel(bgImage2);
        column2A.setOpaque(false);
        
        rowAGbc.gridx = 2;
        rowAGbc.weightx = 0.6065; // 60.65% width
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
        rowAGbc.weightx = 0.16875; // 16.875% width
        rowACenter.add(column3A, rowAGbc);
        
        // Add horizontal padding
        rowAGbc.gridx = 4;
        rowAGbc.weightx = 0.014; // 1.4% width
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
        
        // Set a fixed width for the east panel (25% of frame width)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.75);
        int eastPanelWidth = (int) (width * 0.25);
        
        // Set preferred and maximum size to prevent infinite stretching
        eastPanel.setPreferredSize(new Dimension(eastPanelWidth, 0));
        eastPanel.setMaximumSize(new Dimension(eastPanelWidth, Integer.MAX_VALUE));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        // === Row A (Title) ===
        Image eastBgImage1 = new ImageIcon("assets/east_row1.png").getImage();
        ImagePanel rowAEast = new ImagePanel(eastBgImage1);
        rowAEast.setLayout(new GridBagLayout());
        
        gbc.gridy = 0;
        gbc.weighty = 0.1125; // 11.25% height
        eastPanel.add(rowAEast, gbc);
        
        // === Row B (2 rows x 5 columns) ===
        Image rowBEastbgImage = new ImageIcon("assets/east_row2.png").getImage();
        ImagePanel rowBEast = new ImagePanel(rowBEastbgImage);
        rowBEast.setLayout(new GridLayout(2, 1));
        
        // First row of upgrades
        JPanel upgradeRow1 = new JPanel(new GridLayout(1, 5));
        upgradeRow1.setOpaque(false);
        
        for (int i = 0; i < 5; i++) {
            JButton upgrade = new JButton("Upgrade " + (i + 1));
            upgrade.setBackground(new Color(0xEAE77D));
            toolButtons.add(upgrade);
            upgradeRow1.add(upgrade);
        }
        
        // Second row of upgrades
        JPanel upgradeRow2 = new JPanel(new GridLayout(1, 5));
        upgradeRow2.setOpaque(false);
        
        for (int i = 5; i < 10; i++) {
            JButton upgrade = new JButton("Upgrade " + (i + 1));
            upgrade.setBackground(new Color(0xEAE77D));
            toolButtons.add(upgrade);
            upgradeRow2.add(upgrade);
        }
        
        rowBEast.add(upgradeRow1);
        rowBEast.add(upgradeRow2);
        
        gbc.gridy = 1;
        gbc.weighty = 0.15; // 15% height
        eastPanel.add(rowBEast, gbc);
        
        // === Row C (1 row x 5 columns with different widths) ===
        Image rowCEastImage = new ImageIcon("assets/east_row3.png").getImage();
        ImagePanel rowCEast = new ImagePanel(rowCEastImage);
        rowCEast.setLayout(new GridBagLayout());
        
        GridBagConstraints rowCGbc = new GridBagConstraints();
        rowCGbc.fill = GridBagConstraints.BOTH;
        rowCGbc.gridy = 0;
        rowCGbc.weighty = 1.0;
        
        // Column 1 (Buy/Sell)
        Image buyImage = new ImageIcon("assets/east_row3_column1_Buy.png").getImage();
        Image sellImage = new ImageIcon("assets/east_row3_column1_Sell.png").getImage();
        
        ImagePanel rowCEast_column1 = new ImagePanel(buyImage);
        rowCEast_column1.setLayout(new BorderLayout());
        
        JLabel modeLabel = new JLabel("Buy", SwingConstants.CENTER);
        rowCEast_column1.add(modeLabel, BorderLayout.CENTER);
        modeLabel.setVisible(false);
        
        rowCGbc.gridx = 0;
        rowCGbc.weightx = 0.355; // 35.5% width
        rowCEast.add(rowCEast_column1, rowCGbc);
        
        // Column 2 (1)
        Image rowCEast_column2_button = new ImageIcon("assets/east_row3_column2.png").getImage();
        StaticImageToggleButton rowCEast_column2 = new StaticImageToggleButton("1", rowCEast_column2_button);
        
        rowCGbc.gridx = 1;
        rowCGbc.weightx = 0.1775; // 17.75% width
        rowCEast.add(rowCEast_column2, rowCGbc);
        
        // Column 3 (10)
        Image rowCEast_column3_button = new ImageIcon("assets/east_row3_column3.png").getImage();
        StaticImageToggleButton rowCEast_column3 = new StaticImageToggleButton("10", rowCEast_column3_button);
        
        rowCGbc.gridx = 2;
        rowCGbc.weightx = 0.1775; // 17.75% width
        rowCEast.add(rowCEast_column3, rowCGbc);
        
        // Column 4 (100)
        Image rowCEast_column4_button = new ImageIcon("assets/east_row3_column4.png").getImage();
        StaticImageToggleButton rowCEast_column4 = new StaticImageToggleButton("100", rowCEast_column4_button);
        
        rowCGbc.gridx = 3;
        rowCGbc.weightx = 0.1775; // 17.75% width
        rowCEast.add(rowCEast_column4, rowCGbc);
        
        // Column 5 (Switch)
        Image rowCEast_column5_button = new ImageIcon("assets/east_row3_column5.png").getImage();
        StaticImageButton rowCEast_column5 = new StaticImageButton("S", rowCEast_column5_button);
        
        rowCGbc.gridx = 4;
        rowCGbc.weightx = 0.1125; // 11.25% width
        rowCEast.add(rowCEast_column5, rowCGbc);
        
        // Set up button group and listeners
        ButtonGroup quantityGroup = new ButtonGroup();
        quantityGroup.add(rowCEast_column2);
        quantityGroup.add(rowCEast_column3);
        quantityGroup.add(rowCEast_column4);
        
        Color selectedColor = new Color(0xC1FF72); // light green
        Color defaultColor = new Color(0xEAE77D);  // base yellow
        
        ActionListener quantityListener = e -> {
            AbstractButton source = (AbstractButton) e.getSource();
            
            // Set Global quantity
            if (source == rowCEast_column2) Global.setQuantity(1);
            else if (source == rowCEast_column3) Global.setQuantity(10);
            else if (source == rowCEast_column4) Global.setQuantity(100);
            
            // Update background colors
            rowCEast_column2.setBackground(rowCEast_column2.isSelected() ? selectedColor : defaultColor);
            rowCEast_column3.setBackground(rowCEast_column3.isSelected() ? selectedColor : defaultColor);
            rowCEast_column4.setBackground(rowCEast_column4.isSelected() ? selectedColor : defaultColor);
        };
        
        rowCEast_column2.addActionListener(quantityListener);
        rowCEast_column3.addActionListener(quantityListener);
        rowCEast_column4.addActionListener(quantityListener);
        
        rowCEast_column5.addActionListener(e -> {
            if ("Buy".equalsIgnoreCase(modeLabel.getText())) {
                modeLabel.setText("Sell");
                rowCEast_column1.setImage(sellImage);
                Global.setMode("SELL");
            } else {
                modeLabel.setText("Buy");
                rowCEast_column1.setImage(buyImage);
                Global.setMode("BUY");
            }
        });
        
        // Add to tracking lists
        genLabel.add(modeLabel);
        genToggleButtons.add(rowCEast_column2);
        genToggleButtons.add(rowCEast_column3);
        genToggleButtons.add(rowCEast_column4);
        genButtons.add(rowCEast_column5);
        
        gbc.gridy = 2;
        gbc.weighty = 0.05; // 5% height
        eastPanel.add(rowCEast, gbc);
        
        // === Row D (Scrollable available upgrades) ===
        Image rowDEastImage = new ImageIcon("assets/east_row5.png").getImage();
        rowDEast = new ImagePanel(rowDEastImage);
        rowDEast.setLayout(new GridLayout(0, 1)); // Dynamic row count
        
        JScrollPane scrollableUpgrades = new JScrollPane(rowDEast);
        scrollableUpgrades.getViewport().setBackground(new Color(0xE59C9C));
        
        gbc.gridy = 3;
        gbc.weighty = 0.6875; // 68.75% height
        eastPanel.add(scrollableUpgrades, gbc);
        
        // Add resize listener to handle component sizing
        eastPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = eastPanel.getWidth();
                int panelHeight = eastPanel.getHeight();
                
                // Update upgrade button dimensions
                upgradeButtonWidth = panelWidth;
                upgradeButtonHeight = (int)(panelHeight * 0.1);
                
                // Resize upgrade wrappers
                for (JPanel wrapper : upgradeWrappers) {
                    wrapper.setPreferredSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                    wrapper.setMinimumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                }
                
                // Resize upgrade buttons in Row B
                int upgradeBtnHeight = (int)(panelHeight * 0.075);
                int upgradeBtnWidth = (int)(panelWidth * 0.2);
                
                for (JButton btn : toolButtons) {
                    btn.setPreferredSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
                    btn.setMinimumSize(btn.getPreferredSize());
                    btn.setMaximumSize(btn.getPreferredSize());
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