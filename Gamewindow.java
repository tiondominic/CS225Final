import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
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
        } else if (number < 100000) {
            return formatter1.format(number);
        } else if (number < 1000000) {
            return formatter2.format(number / 1000);
        } else if (number < 1000000000) {
            return formatter3.format(number / 1000000);
        } else if (number < 1000000000000L) {
            return formatter3.format(number / 1000000000);
        } else {
            return formatter3.format(number / 1000000000000L);
        }
    }

    public static String formatCPS(double number) {
        if (number < 1000) {
            return formatter1.format(number); // Always at least one decimal
        } else if (number < 1_000_000) {
            return formatter2.format(number / 1000); // Thousand
        } else if (number < 1_000_000_000) {
            return formatter3.format(number / 1_000_000); // Million
        } else if (number < 1_000_000_000_000L) {
            return formatter3.format(number / 1_000_000_000); // Billion
        } else {
            return formatter3.format(number / 1_000_000_000_000L); // Trillion
        }
    }

    public static String getUnit(double number) {
        if (number < 100000) {
            return "";
        } else if (number < 1000000) {
            return "Thousand";
        } else if (number < 1000000000) {
            return "Million";
        } else if (number < 1000000000000L) {
            return "Billion";
        } else {
            return "Trillion";
        }
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


class ImageButton extends JButton {
    private final Image image;
    private double rotationAngle = 0;

    public ImageButton(String text, Image image) {
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

class ImageBackgroundPanel extends JPanel {
    private final Image backgroundImage;
    private Image scaledBackgroundImage;

    public ImageBackgroundPanel(Image image) {
        this.backgroundImage = image;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        if (scaledBackgroundImage == null || scaledBackgroundImage.getWidth(null) != w || scaledBackgroundImage.getHeight(null) != h) {
            scaledBackgroundImage = backgroundImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        }

        g.drawImage(scaledBackgroundImage, 0, 0, w, h, this);
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
    private DecimalFormat formatter1 = new DecimalFormat("#,###.0");
    private DecimalFormat formatter2 = new DecimalFormat("#,###.00");
    private DecimalFormat formatter3 = new DecimalFormat("#,###.000");
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

        // Set size to 80% width and 80% height of the screen, and center it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 1);
        int height = (int) (screenSize.height * 1);
        setSize(width, height);
        setLocationRelativeTo(null); // Centers the window

        setLayout(new BorderLayout());

        // === WEST PANEL (LEFT) ===
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBackground(new Color(0xFFFFFF));

        // === Row A (Title) ===
        Image WestbgImage1 = new ImageIcon("assets/west_row1.png").getImage();  // Load your background image
        JPanel rowAWest = new ImageBackgroundPanel(WestbgImage1);  // Use the custom panel
        rowAWest.setBackground(new Color(0x39B539));
        rowAWest.setOpaque(false);
        rowAWest.setLayout(new GridBagLayout());  // Add proper layout manager
        
        westPanel.add(rowAWest);

        // === Row B (3 + 5 Buttons) ===
        JPanel rowBWest = new JPanel(new GridLayout(2, 1));
        JPanel rowBTop = new JPanel(new GridLayout(1, 3));
        rowBTop.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 3; i++) {
            JButton btn = new JButton("Top " + (i + 1));
            btn.setBackground(new Color(0xEAE77D));
            btn.addActionListener(e -> { new ChaosElements(gamestate).setUp();}); //TEMPORARY SETUP GOLDEN COOKIE
            topButtons.add(btn); // Store reference
            rowBTop.add(btn);
        }
        JPanel rowBBottom = new JPanel(new GridLayout(1, 5));
        rowBBottom.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 5; i++) {
            JButton btn = new JButton("Bottom " + (i + 1));
            btn.setBackground(new Color(0xF7F4B7));
            bottomButtons.add(btn); // Store reference
            rowBBottom.add(btn);
        }
        rowBWest.add(rowBTop);
        rowBWest.add(rowBBottom);
        westPanel.add(rowBWest);

        // === Row C (Scrollable Purchased Panel) ===
        JPanel purchasedPanel = new JPanel(new GridLayout(purchasedUpgrades, 1));
        purchasedPanel.setBackground(new Color(0x7B89C4));
        for (int i = 0; i < purchasedUpgrades; i++) {
            JLabel lbl = new JLabel("Purchased Upgrade " + (i + 1), SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(new Color(0xF7F4B7));
            lbl.setPreferredSize(new Dimension(300, 250));
            purchasedLabels.add(lbl); // Store reference
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrapper.setOpaque(false);
            wrapper.add(lbl);
            purchasedPanel.add(wrapper);
        }
        JScrollPane scrollPurchased = new JScrollPane(purchasedPanel);
        scrollPurchased.getViewport().setBackground(new Color(0xE59C9C));
        JPanel purchasedWrapper = new JPanel(new BorderLayout());
        purchasedWrapper.add(scrollPurchased, BorderLayout.CENTER);
        westPanel.add(purchasedWrapper);

        // Add to frame
        add(westPanel, BorderLayout.WEST);

        // === Resize listener to apply percentage sizing ===
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = (getWidth() - 16);
                int frameHeight = (getHeight() + 24);

                int panelWidth = (int) (frameWidth * 0.25); // 25% width of frame
                int rowAHeight = (int) (frameHeight * 0.1125); // 11.25% height
                int rowBHeight = (int) (frameHeight * 0.1125);   // 11.25% height
                int rowCHeight = (int) (frameHeight * 0.775); // 77.5% height

                // Set panel & row sizes
                westPanel.setPreferredSize(new Dimension(panelWidth, frameHeight));
                rowAWest.setPreferredSize(new Dimension(panelWidth, rowAHeight));
                rowBWest.setPreferredSize(new Dimension(panelWidth, rowBHeight));
                purchasedWrapper.setPreferredSize(new Dimension(panelWidth, rowCHeight));

                // Resize top buttons (Row B - Top)
                int topBtnWidth = (int) (frameWidth * 0.25);
                int topBtnHeight = (int) (frameHeight * 0.05625); // ~8% of height
                for (JButton btn : topButtons) {
                    btn.setPreferredSize(new Dimension(topBtnWidth, topBtnHeight));
                }

                // Resize bottom buttons (Row B - Bottom)
                int bottomBtnWidth = (int) (frameWidth * 0.25);
                int bottomBtnHeight = (int) (rowBHeight * 0.05625); // 40% of row height
                for (JButton btn : bottomButtons) {
                    btn.setPreferredSize(new Dimension(bottomBtnWidth, bottomBtnHeight));
                }

                // Resize purchased upgrade labels (Row C)
                int purchasedLblWidth = (int) (frameWidth * 0.25);
                int purchasedLblHeight = (int) (frameHeight * 0.25); // ~8% of height
                for (JLabel lbl : purchasedLabels) {
                    lbl.setPreferredSize(new Dimension(purchasedLblWidth, purchasedLblHeight));
                }

                // Revalidate and repaint the panel to apply the changes
                westPanel.revalidate();
                westPanel.repaint();
            }
        });

        // === CENTER PANEL ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0xFFFFFF));

        // === 1st Row (Header) ===

        Image bgImage0 = new ImageIcon("assets/center_rowACenter_1.png").getImage();  // Load your background image
        JPanel rowACenter = new ImageBackgroundPanel(bgImage0);  // Use the custom panel
        rowACenter.setLayout(new BoxLayout(rowACenter, BoxLayout.X_AXIS));
        rowACenter.setBackground(new Color(0xDBD221));
        rowACenter.setOpaque(false);
        centerRows.add(rowACenter);

        int horizontalPadding1 = (int) (screenSize.width * 0.014);
        rowACenter.add(Box.createHorizontalStrut(horizontalPadding1));  // or any pixel value you want

        // === Column 1 (2 Buttons) ===
        JPanel column1A = new JPanel();
        column1A.setLayout(new BoxLayout(column1A, BoxLayout.Y_AXIS));
        column1A.setBackground(new Color(0xA7EAA7));
        column1A.setOpaque(false);
        rowACenter.add(column1A);


        Image staticButtonImage1 = new ImageIcon("assets/center_row1_columnA1.png").getImage();
        StaticImageButton column1AButton1 = new StaticImageButton("Options", staticButtonImage1);
        column1AButton1.addActionListener(e -> System.out.println("Button 1 clicked"));

        firstRowButtons.add(column1AButton1);

        JPanel button1Wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        button1Wrapper.setOpaque(false);
        button1Wrapper.add(column1AButton1);
        column1A.add(button1Wrapper);

        // Button 2
        Image staticButtonImage2 = new ImageIcon("assets/center_row1_columnA2.png").getImage();
        StaticImageButton column1AButton2 = new StaticImageButton("Options", staticButtonImage2);
        column1AButton2.addActionListener(e -> System.out.println("Button 2 clicked"));

        firstRowButtons.add(column1AButton2);

        JPanel button2Wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        button2Wrapper.setOpaque(false);
        button2Wrapper.add(column1AButton2);
        column1A.add(button2Wrapper);

        // === Column 2 (Middle Label) ===

        // === 2nd Row (Content) ===
        // We'll use a panel with BoxLayout for the second row
        Image bgImage2 = new ImageIcon("assets/center_row1_columnB1.png").getImage();  // Load your background image
        JPanel column2A = new ImageBackgroundPanel(bgImage2);  // Use the custom panel
        column2A.setBackground(new Color(0xE59C9C));
        column2A.setOpaque(false);
        rowACenter.add(column2A);

        // === Column 3 (2 Buttons) ===
        JPanel column3A = new JPanel();
        column3A.setLayout(new BoxLayout(column3A, BoxLayout.Y_AXIS));
        column3A.setBackground(new Color(0xA7EAA7));
        column3A.setOpaque(false);
        rowACenter.add(column3A);

        int horizontalPadding2 = (int) (screenSize.width * 0.014);
        rowACenter.add(Box.createHorizontalStrut(horizontalPadding2));  // or any pixel value you want

        // Button 3
        Image staticButtonImage3 = new ImageIcon("assets/center_row1_columnC1.png").getImage();
        StaticImageButton column3AButton1 = new StaticImageButton("Options", staticButtonImage3);
        column3AButton1.addActionListener(e -> System.out.println("Button 3 clicked"));
        
        firstRowButtons.add(column3AButton1);

        JPanel button3Wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        button3Wrapper.setOpaque(false);
        button3Wrapper.add(column3AButton1);
        column3A.add(button3Wrapper);

        // Button 4
        Image staticButtonImage4 = new ImageIcon("assets/center_row1_columnC2.png").getImage();
        StaticImageButton column3AButton2 = new StaticImageButton("Options", staticButtonImage4);
        column3AButton2.addActionListener(e -> System.out.println("Button 4 clicked"));
        firstRowButtons.add(column3AButton2);

        JPanel button4Wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        button4Wrapper.setOpaque(false);
        button4Wrapper.add(column3AButton2);
        column3A.add(button4Wrapper);

        // Add to center panel
        centerPanel.add(rowACenter);

        // === 2nd Row (Content) ===
        // We'll use a panel with BoxLayout for the second row
        Image bgImage = new ImageIcon("assets/main_background_1.png").getImage();  // Load your background image
        JPanel rowBCenter = new ImageBackgroundPanel(bgImage);  // Use the custom panel
        rowBCenter.setBackground(new Color(0xE59C9C));
        centerRows.add(rowBCenter);

        int verticalPadding1 = (int) (screenSize.height * 0.0375);
        rowBCenter.add(Box.createVerticalStrut(verticalPadding1));  // Bottom margin

        // Row 1 (Button with own size)
        JPanel row1B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row1B.setBackground(new Color(0, 0, 0, 64)); // 128 = 50% transparency
        row1B.setOpaque(false);

        // === Create the button with image background ===
        Image staticButtonImage5 = new ImageIcon("assets/center_row2_rowA.png").getImage();
        StaticImageButton row1BButton = new StaticImageButton("Options", staticButtonImage5);
        row1BButton.setLayout(new GridBagLayout());

        // Create constraints for label positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(9, 0, 0, 0); // top, left, bottom, right — pushes label down 10px

        // === Bakery name label ===
        JLabel bakeryNameLabel = new JLabel("Cookie Clicker’s Bakery");
        bakeryNameLabel.setForeground(Color.WHITE);
        bakeryNameLabel.setFont(new Font("Garamond", Font.BOLD, 24)); // <-- Will be updated in resize
        row1BButton.add(bakeryNameLabel, gbc);

        // === Click to rename logic ===
        row1BButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                row1BButton,
                "Enter your bakery's name (max " + maxNameLength + " characters):",
                bakeryNameLabel.getText().replace("’s Bakery", "")
            );
            System.out.println("Button 1 clicked");
            if (input != null) {
                input = input.trim();
                if (!input.isEmpty()) {
                    if (input.length() > maxNameLength) {
                        JOptionPane.showMessageDialog(row1BButton, "Name cannot exceed " + maxNameLength + " characters.");
                        return;
                    }
                    bakeryNameLabel.setText(input + "’s Bakery");
                }
            }
        });
        row1B.add(row1BButton);
        rowBCenter.add(row1B);
        secondRowPanels.add(row1B);

        // Row 2 (Button with own size)
        JPanel row2B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row2B.setBackground(new Color(0, 0, 0, 64)); // 128 = 50% transparency
        row2B.setOpaque(false);

        Image img = new ImageIcon("assets/main_cookie_3.png").getImage();
        ImageButton actionButtonrow2B = new ImageButton(null, img);
        
        // Connect cookie button to gamestate
        actionButtonrow2B.addActionListener(e -> {
            gamestate.Click();
            updateDisplay();

            // Get screen coordinates of mouse
            Point screenPoint = MouseInfo.getPointerInfo().getLocation();

            // Convert to coordinates relative to the frame (not the button)
            Point framePoint = getLayeredPane().getLocationOnScreen();
            int clickX = screenPoint.x - framePoint.x;
            int clickY = screenPoint.y - framePoint.y;

            // Show floating text
            showFloatingText(clickX, clickY, "+" + NumberFormatter.formatNumber(gamestate.getClickingPower()));
        });

        row2B.add(actionButtonrow2B);
        rowBCenter.add(row2B);
        secondRowPanels.add(row2B);

        Image row3BImage = new ImageIcon("assets/center_row2_rowC.png").getImage();  // Load your background image
        JPanel row3B = new ImageBackgroundPanel(row3BImage);  // Use the custom panel
        row3B.setBackground(new Color(0xE59C9C));
        row3B.setAlignmentX(Component.CENTER_ALIGNMENT);
        row3B.setOpaque(false);

        row3B.setLayout(new GridBagLayout());
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.weighty = 1.0;
        gbcBottom.weightx = 1.0;
        gbcBottom.insets = new Insets(16, 0, 0, 0); // top, left, bottom, right — pushes label down 10px
        gbcBottom.anchor = GridBagConstraints.SOUTH; // Align to bottom
        gbcBottom.fill = GridBagConstraints.NONE;

        cookieCountLabel = new JLabel("0.0");
        row3B.add(cookieCountLabel, gbcBottom);
        rowBCenter.add(row3B);
        secondRowPanels.add(row3B);

        Image row4BImage = new ImageIcon("assets/center_row2_rowD.png").getImage();  // Load your background image
        JPanel row4B = new ImageBackgroundPanel(row4BImage);  // Use the custom panel
        
        row4B.setBackground(new Color(0xE59C9C));
        row4B.setAlignmentX(Component.CENTER_ALIGNMENT);
        row4B.setOpaque(false);

        row4B.setLayout(new GridBagLayout());  // Center contents
        GridBagConstraints gbcTop1 = new GridBagConstraints();
        gbcTop1.gridx = 0;
        gbcTop1.gridy = 0;
        gbcTop1.weighty = 1.0;
        gbcTop1.weightx = 1.0;
        gbcTop1.anchor = GridBagConstraints.NORTH; // Align to bottom
        gbcTop1.fill = GridBagConstraints.NONE;

        cookieUnitLabel = new JLabel("Cookies");
        row4B.add(cookieUnitLabel, gbcTop1);
        rowBCenter.add(row4B);
        secondRowPanels.add(row4B);

        Image row5BImage = new ImageIcon("assets/center_row2_rowE.png").getImage();  // Load your background image
        JPanel row5B = new ImageBackgroundPanel(row5BImage);  // Use the custom panel

        row5B.setBackground(new Color(0xE59C9C));
        row5B.setAlignmentX(Component.CENTER_ALIGNMENT);
        row5B.setOpaque(false);
        
        row5B.setLayout(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.weighty = 1.0;
        gbcTop.weightx = 1.0;
        gbcTop.insets = new Insets(0, 0, 20, 0); // top, left, bottom, right — pushes label down 10px
        gbcTop.anchor = GridBagConstraints.NORTH; // Align to top
        gbcTop.fill = GridBagConstraints.NONE;

        cpsLabel = new JLabel("0.0 per second");
        row5B.add(cpsLabel, gbcTop);
        rowBCenter.add(row5B);
        secondRowPanels.add(row5B);
        
        // TO DO: FIX THE SPACINGS LATER, AS OF NOW ITS ACCEPTABLE
        int verticalPadding2 = (int) (screenSize.height * 0.0375);
        rowBCenter.add(Box.createVerticalStrut(verticalPadding2));  // Bottom margin
        rowBCenter.add(Box.createVerticalGlue());  // absorb leftover space here
        System.out.println("verticalPadding2 preferred size: " + verticalPadding2);

        // Add secondRow to centerPanel
        centerPanel.add(rowBCenter);

        // === Add Center Panel to Frame ===
        add(centerPanel, BorderLayout.CENTER);

        // === SEPARATE RESIZE LISTENER FOR CENTER PANEL ===
        centerPanel.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            int frameWidth = (getWidth() - 16);
            int frameHeight = (getHeight() + 24);

            // === CENTER PANEL SIZE ===
            int centerPanelWidth = (int) (frameWidth * 0.5); // 50% width
            centerPanel.setPreferredSize(new Dimension(centerPanelWidth, frameHeight));
            System.out.println("centerPanel preferred size: " + centerPanel.getPreferredSize());

            // === Row A ===
            int rowAHeight = (int) Math.round(frameHeight * 0.15);
            rowACenter.setPreferredSize(new Dimension(centerPanelWidth, rowAHeight));
            rowACenter.setMinimumSize(rowACenter.getPreferredSize());

            // === Row B ===
            int rowBHeight = (int) (frameHeight * 0.85);
            rowBCenter.setPreferredSize(new Dimension(centerPanelWidth, rowBHeight));
            rowBCenter.setMinimumSize(rowBCenter.getPreferredSize());

            // === Row A Columns ===
            int rowAColumnAWidth = (int) (centerPanelWidth * 0.16875);
            int rowAColumnBWidth = (int) (centerPanelWidth * 0.6065);

            column1A.setPreferredSize(new Dimension(rowAColumnAWidth, rowAHeight));
            column1A.setMinimumSize(column1A.getPreferredSize());
            column1A.setMaximumSize(column1A.getPreferredSize());

            column2A.setPreferredSize(new Dimension(rowAColumnBWidth, rowAHeight));
            column2A.setMinimumSize(column2A.getPreferredSize());
            column2A.setMaximumSize(column2A.getPreferredSize());

            column3A.setPreferredSize(new Dimension(rowAColumnAWidth, rowAHeight));
            column3A.setMinimumSize(column3A.getPreferredSize());
            column3A.setMaximumSize(column3A.getPreferredSize());

            System.out.println("Frame w: " + frameWidth);
            System.out.println("Centerpanel w: " + frameWidth);
            System.out.println("Frame h: " + frameHeight);
            System.out.println("Centerpanel h: " + centerPanelWidth);
            System.out.println("column1A preferred size: " + column1A.getPreferredSize());
            System.out.println("column2A preferred size: " + column2A.getPreferredSize());
            System.out.println("column3A preferred size: " + column3A.getPreferredSize());

            // === Row A Buttons ===
            int rowAButtonWidth = rowAColumnAWidth;
            int rowAButtonHeight = (int) (frameHeight * 0.075);
            for (JButton btn : firstRowButtons) {
                btn.setPreferredSize(new Dimension(rowAButtonWidth, rowAButtonHeight));
                btn.setMaximumSize(new Dimension(rowAButtonWidth, rowAButtonHeight));
                System.out.println("column preferred size: " + btn.getPreferredSize());
            }

            // === Row B Panels ===
            int rowBrowAHeight = (int) (frameHeight * 0.075);
            int rowBrowBHeight = (int) (frameHeight * 0.475);
            int rowBWidth = (int) (centerPanelWidth * 0.6625);

            row1B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
            row1B.setMaximumSize(row1B.getPreferredSize());
            System.out.println("row1B preferred size: " + row1B.getPreferredSize());

            row2B.setPreferredSize(new Dimension(rowBWidth, rowBrowBHeight));
            row2B.setMaximumSize(row2B.getPreferredSize());

            row3B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
            row3B.setMaximumSize(row3B.getPreferredSize());
            System.out.println("row3B preferred size: " + row3B.getPreferredSize());

            row4B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
            row4B.setMaximumSize(row4B.getPreferredSize());
            System.out.println("row4B preferred size: " + row4B.getPreferredSize());

            row5B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
            row5B.setMaximumSize(row5B.getPreferredSize());
            System.out.println("row5B preferred size: " + row5B.getPreferredSize());

            // === Name Button in Row 2 ===
            row1BButton.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
            row1BButton.setMaximumSize(new Dimension(rowBWidth, rowBrowAHeight));
            row1BButton.setMinimumSize(new Dimension(rowBWidth, rowBrowAHeight));

            // === Action Button in Row 2 ===
            int row2BButtonWidth = (int) (rowBWidth * 0.9);
            int row2BButtonHeight = (int) (rowBrowBHeight * 0.9);
            actionButtonrow2B.setPreferredSize(new Dimension(row2BButtonWidth, row2BButtonHeight));

            // === Fonts ===
            int baseFontSize = (int) (frameHeight * 0.030);
            Font scaledFont1 = new Font("Garamond", Font.BOLD, baseFontSize);
            Font scaledFont2 = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 1.5));
            Font scaledFont3 = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 0.8));
            Font scaledFont4 = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 1.3));

            for (JButton btn : firstRowButtons) {
                btn.setFont(scaledFont1);
                btn.setForeground(Color.BLACK);
            }

            Color fontColor = Color.WHITE;
            Color fontColor2 = new Color(0x4c2308);
            Color fontColor3 = new Color(0x662c0c);

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

            // Final repaint
            centerPanel.revalidate();
            centerPanel.repaint();
        }
    });

        // === EAST PANEL (RIGHT) ===
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBackground(new Color(0xFFFFFF));

        // === Row A (Title) ===
        Image EastbgImage1 = new ImageIcon("assets/east_row1.png").getImage();  // Load your background image
        JPanel rowAEast = new ImageBackgroundPanel(EastbgImage1);  // Use the custom panel
        rowAEast.setBackground(new Color(0x39B539));
        rowAEast.setOpaque(false);
        rowAEast.setLayout(new GridBagLayout());  // Add proper layout manager
        eastPanel.add(rowAEast);

        // === Row B (2 rows x 5 columns using BoxLayout) ===
        Image rowBEastbgImage = new ImageIcon("assets/east_row2.png").getImage();  // Load your background image
        JPanel rowBEast = new ImageBackgroundPanel(rowBEastbgImage);  // Use the custom panel
        rowBEast.setOpaque(false);
        rowBEast.setBackground(new Color(0x601818));

        // === First row of upgrades ===
        JPanel upgradeRow1 = new JPanel();
        upgradeRow1.setLayout(new BoxLayout(upgradeRow1, BoxLayout.X_AXIS));
        upgradeRow1.setOpaque(false);

        JButton upgrade1 = new JButton("Upgrade 1");
        JButton upgrade2 = new JButton("Upgrade 2");
        JButton upgrade3 = new JButton("Upgrade 3");
        JButton upgrade4 = new JButton("Upgrade 4");
        JButton upgrade5 = new JButton("Upgrade 5");

        upgrade1.setBackground(new Color(0xEAE77D));
        upgrade2.setBackground(new Color(0xEAE77D));
        upgrade3.setBackground(new Color(0xEAE77D));
        upgrade4.setBackground(new Color(0xEAE77D));
        upgrade5.setBackground(new Color(0xEAE77D));

        toolButtons.add(upgrade1);
        toolButtons.add(upgrade2);
        toolButtons.add(upgrade3);
        toolButtons.add(upgrade4);
        toolButtons.add(upgrade5);

        upgradeRow1.add(upgrade1);
        upgradeRow1.add(upgrade2);
        upgradeRow1.add(upgrade3);
        upgradeRow1.add(upgrade4);
        upgradeRow1.add(upgrade5);

        // === Second row of upgrades ===
        JPanel upgradeRow2 = new JPanel();
        upgradeRow2.setLayout(new BoxLayout(upgradeRow2, BoxLayout.X_AXIS));
        upgradeRow2.setOpaque(false);

        JButton upgrade6 = new JButton("Upgrade 6");
        JButton upgrade7 = new JButton("Upgrade 7");
        JButton upgrade8 = new JButton("Upgrade 8");
        JButton upgrade9 = new JButton("Upgrade 9");
        JButton upgrade10 = new JButton("Upgrade 10");

        upgrade6.setBackground(new Color(0xEAE77D));
        upgrade7.setBackground(new Color(0xEAE77D));
        upgrade8.setBackground(new Color(0xEAE77D));
        upgrade9.setBackground(new Color(0xEAE77D));
        upgrade10.setBackground(new Color(0xEAE77D));

        toolButtons.add(upgrade6);
        toolButtons.add(upgrade7);
        toolButtons.add(upgrade8);
        toolButtons.add(upgrade9);
        toolButtons.add(upgrade10);

        upgradeRow2.add(upgrade6);
        upgradeRow2.add(upgrade7);
        upgradeRow2.add(upgrade8);
        upgradeRow2.add(upgrade9);
        upgradeRow2.add(upgrade10);

        // Add both rows to main panel
        rowBEast.add(upgradeRow1);
        rowBEast.add(upgradeRow2);

        // Add to east panel
        eastPanel.add(rowBEast);

        // === Row C (1 row x 5 columns with different widths) ===
        JPanel rowCEast = new JPanel();
        rowCEast.setLayout(new BoxLayout(rowCEast, BoxLayout.X_AXIS));
        rowCEast.setBackground(new Color(0xA7EAA7));

        // Create label and buttons
        JLabel rowCEast_column1 = new JLabel("Buy", SwingConstants.CENTER);
        rowCEast_column1.setOpaque(true);
        rowCEast_column1.setBackground(new Color(0xEAE77D));

        JToggleButton rowCEast_column2 = new JToggleButton("1");
        JToggleButton rowCEast_column3 = new JToggleButton("10");
        JToggleButton rowCEast_column4 = new JToggleButton("100");

        ButtonGroup quantityGroup = new ButtonGroup();
        quantityGroup.add(rowCEast_column2);
        quantityGroup.add(rowCEast_column3);
        quantityGroup.add(rowCEast_column4);

        Color selectedColor = new Color(0xC1FF72); // light green
        Color defaultColor = new Color(0xEAE77D);  // your base yellow

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

        JButton rowCEast_column5 = new JButton("S");
        rowCEast_column5.addActionListener(e -> {
            if (rowCEast_column1.getText().equalsIgnoreCase("Buy")) {
                rowCEast_column1.setText("Sell");
                Global.setMode("SELL");
            } else {
                rowCEast_column1.setText("Buy");
                Global.setMode("BUY");
            }
        });

        rowCEast_column2.setBackground(new Color(0xEAE77D));
        rowCEast_column3.setBackground(new Color(0xEAE77D));
        rowCEast_column4.setBackground(new Color(0xEAE77D));
        rowCEast_column5.setBackground(new Color(0xEAE77D));

        // Add to tracking lists
        genLabel.add(rowCEast_column1);
        genToggleButtons.add(rowCEast_column2);
        genToggleButtons.add(rowCEast_column3);
        genToggleButtons.add(rowCEast_column4);
        genButtons.add(rowCEast_column5);

        // Wrap each component in its own JPanel (needed for consistent sizing)
        JPanel rowCEast_column1_wrapper = new JPanel(new BorderLayout());
        JPanel rowCEast_column2_wrapper = new JPanel(new BorderLayout());
        JPanel rowCEast_column3_wrapper = new JPanel(new BorderLayout());
        JPanel rowCEast_column4_wrapper = new JPanel(new BorderLayout());
        JPanel rowCEast_column5_wrapper = new JPanel(new BorderLayout());

        rowCEast_column1_wrapper.add(rowCEast_column1, BorderLayout.CENTER);
        rowCEast_column2_wrapper.add(rowCEast_column2, BorderLayout.CENTER);
        rowCEast_column3_wrapper.add(rowCEast_column3, BorderLayout.CENTER);
        rowCEast_column4_wrapper.add(rowCEast_column4, BorderLayout.CENTER);
        rowCEast_column5_wrapper.add(rowCEast_column5, BorderLayout.CENTER);

        // Add wrappers to Row C
        rowCEast.add(rowCEast_column1_wrapper);
        rowCEast.add(rowCEast_column2_wrapper);
        rowCEast.add(rowCEast_column3_wrapper);
        rowCEast.add(rowCEast_column4_wrapper);
        rowCEast.add(rowCEast_column5_wrapper);

        eastPanel.add(rowCEast);

        // === Row D (Scrollable available upgrades) ===
        rowDEast = new JPanel(); // this now assigns to the field
        rowDEast.setLayout(new BoxLayout(rowDEast, BoxLayout.Y_AXIS));
        rowDEast.setBackground(new Color(0x7B89C4));
        
        // We'll populate this in the addUpgrade method
        
        JScrollPane scrollableUpgrades = new JScrollPane(rowDEast);
        scrollableUpgrades.getViewport().setBackground(new Color(0xE59C9C));

        JPanel scrollableUpgradesPanel = new JPanel(new BorderLayout());
        scrollableUpgradesPanel.add(scrollableUpgrades, BorderLayout.CENTER);
        eastPanel.add(scrollableUpgradesPanel);

        // === Add to Frame ===
        add(eastPanel, BorderLayout.EAST);

        // === Resize listener ===
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int windowWidth = (getWidth() - 16);
                int windowHeight = (getHeight() + 24);
                System.out.println("DEBUG WINDOW WIDTH: " + windowWidth);
                System.out.println("DEBUG WINDOW HEIGHT: " + windowHeight);

                int eastPanelWidth = (int) (windowWidth * 0.25);  // 25% width of frame
                System.out.println("DEBUG EAST PANEL WIDTH: " + eastPanelWidth);
                int rowAHeight = (int) (windowHeight * 0.1125); // 11.25%
                int rowBHeight = (int) (windowHeight * 0.15);   // 15%
                int rowCHeight = (int) (windowHeight * 0.05);   // 5%
                int rowDHeight = (int) (windowHeight * 0.6875); // Remaining (100 - sum above)

                int rowBRowsHeight = (int) (rowBHeight * 0.5);
                
                // Set panel & row sizes
                eastPanel.setPreferredSize(new Dimension(eastPanelWidth, windowHeight));
                eastPanel.setMinimumSize(eastPanel.getPreferredSize());
                eastPanel.setMaximumSize(eastPanel.getPreferredSize());
                System.out.println("[DEBUG] EAST PANEL SIZE: " + eastPanel.getPreferredSize());

                rowAEast.setPreferredSize(new Dimension(eastPanelWidth, rowAHeight));
                rowAEast.setMinimumSize(rowAEast.getPreferredSize());
                rowAEast.setMaximumSize(rowAEast.getPreferredSize());

                rowBEast.setPreferredSize(new Dimension(eastPanelWidth, rowBHeight));
                rowBEast.setMinimumSize(rowBEast.getPreferredSize());
                rowBEast.setMaximumSize(rowBEast.getPreferredSize());
                System.out.println("[DEBUG] ROW B EAST SIZE: " + rowBEast.getPreferredSize());

                upgradeRow1.setPreferredSize(new Dimension(eastPanelWidth, rowBRowsHeight));
                upgradeRow1.setMinimumSize(upgradeRow1.getPreferredSize());
                upgradeRow1.setMaximumSize(upgradeRow1.getPreferredSize());
                System.out.println("[DEBUG] ROW B UPGRADE ROW 1 EAST SIZE: " + upgradeRow1.getPreferredSize());

                upgradeRow2.setPreferredSize(new Dimension(eastPanelWidth, rowBRowsHeight));
                upgradeRow2.setMinimumSize(upgradeRow2.getPreferredSize());
                upgradeRow2.setMaximumSize(upgradeRow2.getPreferredSize());
                System.out.println("[DEBUG] ROW B UPGRADE ROW 2 EAST SIZE: " + upgradeRow2.getPreferredSize());

                rowCEast.setPreferredSize(new Dimension(eastPanelWidth, rowCHeight));
                rowCEast.setMinimumSize(rowCEast.getPreferredSize());
                rowCEast.setMaximumSize(rowCEast.getPreferredSize());
                System.out.println("[DEBUG] ROW C EAST SIZE: " + rowCEast.getPreferredSize());

                scrollableUpgradesPanel.setPreferredSize(new Dimension(eastPanelWidth, rowDHeight));
                scrollableUpgradesPanel.setMinimumSize(scrollableUpgradesPanel.getPreferredSize());
                scrollableUpgradesPanel.setMaximumSize(scrollableUpgradesPanel.getPreferredSize());
                System.out.println("[DEBUG] ROW C EAST SIZE: " + rowCEast.getPreferredSize());

                // Update button size variables
                upgradeButtonWidth = (int) (eastPanelWidth);
                upgradeButtonHeight = (int) (windowHeight * 0.1);

                // Resize upgrade wrappers
                for (JPanel wrapper : upgradeWrappers) {
                    wrapper.setPreferredSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                    wrapper.setMinimumSize(new Dimension(upgradeButtonWidth, upgradeButtonHeight));
                }

                // === Resize Upgrade Buttons in Row B ===
                int upgradeBtnHeight = (int) (windowHeight * 0.075);  // 7.5% height
                int upgradeBtnWidth = (int) (eastPanelWidth * 0.2);  // 7.5% height

                for (JButton btn : toolButtons) {
                    btn.setPreferredSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
                    btn.setMinimumSize(btn.getPreferredSize());
                    btn.setMaximumSize(btn.getPreferredSize());
                }

                // Resize gen buttons (Row C) with different widths
                int totalWidth = eastPanelWidth;

                int column1to5Height = (int) (windowHeight * 0.05);

                // Calculate widths
                int column1Width = (int)(totalWidth * 0.355);
                int column2to4Width = (int)(totalWidth * 0.1775);
                int column5Width = (int)(totalWidth * 0.1125);

                // Get wrapper components
                Component[] wrappers = rowCEast.getComponents();

                // Resize each wrapper
                wrappers[0].setPreferredSize(new Dimension(column1Width, column1to5Height));
                wrappers[1].setPreferredSize(new Dimension(column2to4Width, column1to5Height));
                wrappers[2].setPreferredSize(new Dimension(column2to4Width, column1to5Height));
                wrappers[3].setPreferredSize(new Dimension(column2to4Width, column1to5Height));
                wrappers[4].setPreferredSize(new Dimension(column5Width, column1to5Height));

                for (Component wrapper : wrappers) {
                    wrapper.setMinimumSize(wrapper.getPreferredSize());
                    wrapper.setMaximumSize(wrapper.getPreferredSize());
                }


                eastPanel.revalidate();
                eastPanel.repaint();
                
            }
        });

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
        System.out.println("westPanel bounds: " + westPanel.getBounds());
        System.out.println("centerPanel bounds: " + centerPanel.getBounds());
        System.out.println("eastPanel bounds: " + eastPanel.getBounds());
        
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
