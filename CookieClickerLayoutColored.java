import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

    public ImageBackgroundPanel(Image image) {
        this.backgroundImage = image;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);

            if (imgWidth > 0 && imgHeight > 0) {
                // Scale to cover entire panel (may crop image)
                double scale = Math.max((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);

                // Center the image
                int x = (panelWidth - newWidth) / 2;
                int y = (panelHeight - newHeight) / 2;

                g.drawImage(backgroundImage, x, y, newWidth, newHeight, this);
            }
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
    private final DecimalFormat formatter = new DecimalFormat("#,###.0");
    
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
        
        costLabel = new JLabel("Cost: " + formatter.format(upgrade.getCost(Global.getQuantity())));
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
        
        // Update cost text and color
        costLabel.setText("Cost: " + formatter.format(upgrade.getCost(Global.getQuantity())));
        if (stage == 3) {
            costLabel.setForeground(GREEN_TEXT);
        } else {
            costLabel.setForeground(RED_TEXT);
        }
    }
}

public class CookieClickerLayoutColored extends JFrame {

    // Example dynamic row counts
    private int purchasedUpgrades = 10;
    //private int availableUpgrades = 5;
    private Gamestate gamestate;
    private List<Upgrade> upgrades = new ArrayList<>();
    private Timer uiUpdateTimer;
    private DecimalFormat formatter = new DecimalFormat("#,###.0");
    private JLabel cookieCountLabel;
    private JLabel cookieUnitLabel;
    private JLabel cpsLabel;
    private JPanel availablePanel;
    private int upgradeBtnWidth;
    private int upgradeBtnHeight;
    private List<JPanel> upgradeWrappers = new ArrayList<>();
    private List<UpgradePanel> upgradePanels = new ArrayList<>();
    private int visibleUpgrades = 0;
    private final List<FloatingLabel> floatingLabels = new ArrayList<>();

    // Colors for upgrade stages
    private final Color STAGE1_BG = new Color(0x333333); // Dark gray
    private final Color STAGE2_BG = new Color(0x777777); // Lighter gray
    private final Color STAGE3_BG = new Color(0xF7F4B7); // Original color
    private final Color RED_TEXT = new Color(0xFF0000); // Red
    private final Color GREEN_TEXT = new Color(0x00AA00); // Green
    private final Color WHITE_TEXT = new Color(0xFFFFFF); // White

    List<JButton> upgradeButtons = new ArrayList<>();
    List<JButton> toolButtons = new ArrayList<>();
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
    
    public CookieClickerLayoutColored(Gamestate gamestate) {
        this.gamestate = gamestate;
        setTitle("Cookie Clicker Layout - Colored");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set size to 80% width and 80% height of the screen, and center it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);
        setSize(width, height);
        setLocationRelativeTo(null); // Centers the window

        // Calculate initial button sizes
        upgradeBtnWidth = (int) (width * 0.25);
        upgradeBtnHeight = (int) (height * 0.1);

        setLayout(new BorderLayout());

        // === WEST PANEL (LEFT) ===
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBackground(Color.LIGHT_GRAY);

        // === Row A (Title) ===
        JLabel westTitle = new JLabel("UPGRADES", SwingConstants.CENTER);
        JPanel rowAWest = new JPanel(new BorderLayout());
        rowAWest.setBackground(new Color(0x39B539));
        rowAWest.add(westTitle, BorderLayout.CENTER);
        westPanel.add(rowAWest);

        // === Row B (3 + 5 Buttons) ===
        JPanel rowBWest = new JPanel(new GridLayout(2, 1));
        JPanel rowBTop = new JPanel(new GridLayout(1, 3));
        rowBTop.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 3; i++) {
            JButton btn = new JButton("Top " + (i + 1));
            btn.setBackground(new Color(0xEAE77D));
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
                int frameWidth = getWidth();
                int frameHeight = getHeight();

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
        JPanel rowACenter = new JPanel();
        rowACenter.setLayout(new BoxLayout(rowACenter, BoxLayout.X_AXIS));
        rowACenter.setBackground(new Color(0xDBD221));
        centerRows.add(rowACenter);

        // Column 1 (2 Buttons)
        JPanel column1A = new JPanel(new GridLayout(2, 1));  // 2 rows in this column
        column1A.setBackground(new Color(0xA7EAA7));
        rowACenterColumnA.add(column1A);

        // === Button 1 ===
        JButton column1AButton1 = new JButton("Options");
        column1AButton1.setBackground(new Color(0xEAE77D));
        // Add individual action listener
        column1AButton1.addActionListener(e -> {
            // Your custom logic for button 1
            System.out.println("Button 1 clicked");
        });
        firstRowButtons.add(column1AButton1);
        column1A.add(column1AButton1);

        // === Button 2 ===
        JButton column1AButton2 = new JButton("Stats");
        column1AButton2.setBackground(new Color(0xEAE77D));
        // Add individual action listener
        column1AButton2.addActionListener(e -> {
            // Your custom logic for button 2
            System.out.println("Button 2 clicked");
        });
        firstRowButtons.add(column1AButton2);
        column1A.add(column1AButton2);

        // Column 2 (1 Label or Text)
        JPanel column2A = new JPanel(new GridBagLayout());
        column2A.setBackground(new Color(0xA53A3A));
        rowACenterColumnB.add(column2A);
        JLabel middleLabel = new JLabel("COOKIE REALMS");
        middleLabel.setForeground(Color.BLACK);
        column2A.add(middleLabel);

        // Column 1 (2 Buttons)
        JPanel column3A = new JPanel(new GridLayout(2, 1));  // 2 rows in this column
        column3A.setBackground(new Color(0xA7EAA7));
        rowACenterColumnA.add(column3A);

        // === Button 1 ===
        JButton column3AButton1 = new JButton("Info");
        column3AButton1.setBackground(new Color(0xEAE77D));
        // Add individual action listener
        column3AButton1.addActionListener(e -> {
            // Your custom logic for button 1
            System.out.println("Button 1 clicked");
        });
        firstRowButtons.add(column3AButton1);
        column3A.add(column3AButton1);

        // === Button 2 ===
        JButton column3AButton2 = new JButton("Help");
        column3AButton2.setBackground(new Color(0xEAE77D));
        // Add individual action listener
        column3AButton2.addActionListener(e -> {
            // Your custom logic for button 2
            System.out.println("Button 2 clicked");
        });
        firstRowButtons.add(column3AButton2);
        column3A.add(column3AButton2);

        rowACenter.add(column1A);
        rowACenter.add(column2A);
        rowACenter.add(column3A);
        centerPanel.add(rowACenter);

        // === 2nd Row (Content) ===
        // We'll use a panel with BoxLayout for the second row
        Image bgImage = new ImageIcon("assets/main_background_3.png").getImage();  // Load your background image
        JPanel rowBCenter = new ImageBackgroundPanel(bgImage);  // Use the custom panel
        rowBCenter.setBackground(new Color(0xE59C9C));
        centerRows.add(rowBCenter);

        // Row 1 (Text or Label)
        JPanel row1B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row1B.setBackground(new Color(0, 0, 0, 64)); // 128 = 50% transparency
        row1B.setOpaque(true);

        JLabel labelrow1B = new JLabel("Cookie Clicker"); //18 Characters Max
        row1B.add(labelrow1B);
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
            showFloatingText(clickX, clickY, "+" + formatter.format(gamestate.getClickingPower()));
        });

        row2B.add(actionButtonrow2B);
        rowBCenter.add(row2B);
        secondRowPanels.add(row2B);

        // Row 3 (Text or Label - Cookie Count)
        JPanel row3B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        // FIX: Make the panel fully opaque to fix the glitching issue
        row3B.setBackground(new Color(0, 0, 0, 255)); 
        row3B.setOpaque(true);

        cookieCountLabel = new JLabel("0.0");
        row3B.add(cookieCountLabel);
        rowBCenter.add(row3B);
        secondRowPanels.add(row3B);

        // Row 4 (Text or Label - Cookie Unit)
        JPanel row4B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row4B.setBackground(new Color(0, 0, 0, 255)); // Make fully opaque
        row4B.setOpaque(true);

        cookieUnitLabel = new JLabel("Cookies");
        row4B.add(cookieUnitLabel);
        rowBCenter.add(row4B);
        secondRowPanels.add(row4B);

        // Row 5 (Text or Label - CPS)
        JPanel row5B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row5B.setBackground(new Color(0, 0, 0, 255)); // Make fully opaque
        row5B.setOpaque(true);
        
        cpsLabel = new JLabel("0.0 per second");
        row5B.add(cpsLabel);
        rowBCenter.add(row5B);
        secondRowPanels.add(row5B);

        int verticalPadding2 = (int) (screenSize.height * 0.1125);
        rowBCenter.add(Box.createVerticalStrut(verticalPadding2));  // Bottom margin

        // Add secondRow to centerPanel
        centerPanel.add(rowBCenter);

        // === Add Center Panel to Frame ===
        add(centerPanel, BorderLayout.CENTER);

        // === SEPARATE RESIZE LISTENER FOR CENTER PANEL ===
        centerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = getWidth();
                int frameHeight = getHeight();
                
                // CENTER PANEL CALCULATIONS
                int centerPanelWidth = (int) (frameWidth * 0.5);  // 75% width (assuming east is 25%)

                int rowAHeight = (int) (frameHeight * 0.15);  // 75% width (assuming east is 25%)
                int rowBHeight = (int) (frameHeight * 0.85);  // 75% width (assuming east is 25%)

                // Set panel & row sizes
                centerPanel.setPreferredSize(new Dimension(centerPanelWidth, frameHeight));

                rowACenter.setPreferredSize(new Dimension(centerPanelWidth, rowAHeight));
                rowBCenter.setPreferredSize(new Dimension(centerPanelWidth, rowBHeight));
    
                rowACenter.setMinimumSize(new Dimension(centerPanelWidth, rowAHeight));

                rowBCenter.setMinimumSize(new Dimension(centerPanelWidth, rowBHeight));
                
                int rowAColumnAWidth = (int) (centerPanelWidth * 0.16875);
                int rowAColumnBWidth = (int) (centerPanelWidth * 0.6625);
                column1A.setPreferredSize(new Dimension(rowAColumnAWidth, rowAHeight));

                column2A.setPreferredSize(new Dimension(rowAColumnBWidth, rowAHeight));

                column3A.setPreferredSize(new Dimension(rowAColumnAWidth, rowAHeight));
                
                // Row A buttons - each button gets appropriate size
                int rowAButtonWidth = (int) (frameWidth * 0.16875);
                int rowAButtonHeight = (int) (frameHeight * 0.075);
                for (JButton btn : firstRowButtons) {
                    btn.setPreferredSize(new Dimension(rowAButtonWidth, rowAButtonHeight));
                    btn.setMaximumSize(new Dimension(rowAButtonWidth, rowAButtonHeight));
                }
                
                // Customize each row in the second row section
                int rowBrowAHeight = (int) (centerPanelWidth * 0.075);
                int rowBrowBHeight = (int) (centerPanelWidth * 0.475);
                int rowBrowCHeight = (int) (centerPanelWidth * 0.0375);
                int rowBWidth = (int) (centerPanelWidth * 0.6625);
                row1B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
                row1B.setMaximumSize(new Dimension(rowBWidth, rowBrowAHeight));

                row2B.setPreferredSize(new Dimension(rowBWidth, rowBrowBHeight));
                row2B.setMaximumSize(new Dimension(rowBWidth, rowBrowBHeight));

                row3B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
                row3B.setMaximumSize(new Dimension(rowBWidth, rowBrowAHeight));

                row4B.setPreferredSize(new Dimension(rowBWidth, rowBrowAHeight));
                row4B.setMaximumSize(new Dimension(rowBWidth, rowBrowAHeight));

                row5B.setPreferredSize(new Dimension(rowBWidth, rowBrowCHeight));
                row5B.setMaximumSize(new Dimension(rowBWidth, rowBrowAHeight));
                
                // Action button in row2B
                int row2BButtonWidth = (int) (rowBWidth * 0.9);
                int row2BButtonHeight = (int) (rowBrowBHeight * 0.9);
                actionButtonrow2B.setPreferredSize(new Dimension(row2BButtonWidth, row2BButtonHeight));

                // Calculate font size based on frame height
                int baseFontSize = (int) (frameHeight * 0.030);  // Adjust multiplier as needed
                Font scaledFont1 = new Font("Garamond", Font.BOLD, baseFontSize);

                // Set font color (foreground) for all firstRowButtons
                for (JButton btn : firstRowButtons) {
                    btn.setFont(scaledFont1);
                    btn.setForeground(new Color(0, 0, 0));  // or any color you want
                }

                // Set font and color for the middle label
                middleLabel.setFont(new Font("Garamond", Font.BOLD, (int) (frameHeight * 0.055)));
                middleLabel.setForeground(new Color(255, 255, 255));  // or another color

                Font scaledFont2 = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 1.5));
                Font scaledFont3 = new Font("Garamond", Font.BOLD, (int) (baseFontSize * 1.125));

                // Set font for all labels
                labelrow1B.setFont(scaledFont2);
                cookieCountLabel.setFont(scaledFont2);
                cookieUnitLabel.setFont(scaledFont2);
                cpsLabel.setFont(scaledFont3);

                // Set font color for all labels
                Color fontColor = new Color(0xFFFFFF);  // Dark slate gray
                labelrow1B.setForeground(fontColor);
                cookieCountLabel.setForeground(fontColor);
                cookieUnitLabel.setForeground(fontColor);
                cpsLabel.setForeground(fontColor);

                // Set font for the action button (if applicable)
                actionButtonrow2B.setFont(scaledFont2);  // Adjust font size for button as well
                actionButtonrow2B.setForeground(fontColor); // Apply same font color
                
                // Refresh center panel
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        // === EAST PANEL (RIGHT) ===
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBackground(Color.LIGHT_GRAY);

        // === Row A (Title) ===
        JLabel eastTitle = new JLabel("STORE", SwingConstants.CENTER);
        JPanel rowAEast = new JPanel(new BorderLayout());
        rowAEast.setBackground(new Color(0x39B539));
        rowAEast.add(eastTitle, BorderLayout.CENTER);
        eastPanel.add(rowAEast);

        // === Row B (2 rows x 5 columns) ===
        JPanel rowBEast = new JPanel(new GridLayout(2, 5));
        rowBEast.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 10; i++) {
            JButton tool = new JButton("Tool " + (i + 1));
            tool.setBackground(new Color(0xEAE77D));
            toolButtons.add(tool);
            rowBEast.add(tool);
        }
        eastPanel.add(rowBEast);

        // === Row C (1 row x 5 columns with different widths) ===
        JPanel rowCEast = new JPanel();
        rowCEast.setLayout(new BoxLayout(rowCEast, BoxLayout.X_AXIS));
        rowCEast.setBackground(new Color(0xA7EAA7));

        // Create label and buttons
        JLabel gen1 = new JLabel("Buy", SwingConstants.CENTER);
        gen1.setOpaque(true);
        gen1.setBackground(new Color(0xEAE77D));

        JButton gen2 = new JButton("1"); gen2.addActionListener(e -> {Global.setQuantity(1);});
        JButton gen3 = new JButton("10"); gen3.addActionListener(e -> {Global.setQuantity(10);});
        JButton gen4 = new JButton("100"); gen4.addActionListener(e -> {Global.setQuantity(100);});
        JButton gen5 = new JButton("S"); // no sell mode  yet

        gen2.setBackground(new Color(0xEAE77D));
        gen3.setBackground(new Color(0xEAE77D));
        gen4.setBackground(new Color(0xEAE77D));
        gen5.setBackground(new Color(0xEAE77D));

        // Add to tracking lists
        genLabel.add(gen1);
        genButtons.add(gen2);
        genButtons.add(gen3);
        genButtons.add(gen4);
        genButtons.add(gen5);

        // Wrap each component in its own JPanel (needed for consistent sizing)
        JPanel gen1Wrapper = new JPanel(new BorderLayout());
        JPanel gen2Wrapper = new JPanel(new BorderLayout());
        JPanel gen3Wrapper = new JPanel(new BorderLayout());
        JPanel gen4Wrapper = new JPanel(new BorderLayout());
        JPanel gen5Wrapper = new JPanel(new BorderLayout());

        gen1Wrapper.add(gen1, BorderLayout.CENTER);
        gen2Wrapper.add(gen2, BorderLayout.CENTER);
        gen3Wrapper.add(gen3, BorderLayout.CENTER);
        gen4Wrapper.add(gen4, BorderLayout.CENTER);
        gen5Wrapper.add(gen5, BorderLayout.CENTER);

        // Add wrappers to Row C
        rowCEast.add(gen1Wrapper);
        rowCEast.add(gen2Wrapper);
        rowCEast.add(gen3Wrapper);
        rowCEast.add(gen4Wrapper);
        rowCEast.add(gen5Wrapper);

        eastPanel.add(rowCEast);

        // === Row D (Scrollable available upgrades) ===
        availablePanel = new JPanel(); // this now assigns to the field
        availablePanel.setLayout(new BoxLayout(availablePanel, BoxLayout.Y_AXIS));
        availablePanel.setBackground(new Color(0x7B89C4));
        
        // We'll populate this in the addUpgrade method
        
        JScrollPane scrollAvailable = new JScrollPane(availablePanel);
        scrollAvailable.getViewport().setBackground(new Color(0xE59C9C));
        JPanel availableWrapper = new JPanel(new BorderLayout());
        availableWrapper.add(scrollAvailable, BorderLayout.CENTER);
        eastPanel.add(availableWrapper);

        // === Add to Frame ===
        add(eastPanel, BorderLayout.EAST);

        // === Resize listener ===
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = getWidth();
                int frameHeight = getHeight();

                int panelWidth = (int) (frameWidth * 0.25);  // 25% width of frame
                int rowAHeight = (int) (frameHeight * 0.1125); // 11.25%
                int rowBHeight = (int) (frameHeight * 0.15);   // 15%
                int rowCHeight = (int) (frameHeight * 0.05);   // 5%
                int rowDHeight = (int) (frameHeight * 0.6875); // Remaining (100 - sum above)

                // Set panel & row sizes
                eastPanel.setPreferredSize(new Dimension(panelWidth, frameHeight));
                rowAEast.setPreferredSize(new Dimension(panelWidth, rowAHeight));
                rowBEast.setPreferredSize(new Dimension(panelWidth, rowBHeight));
                rowCEast.setPreferredSize(new Dimension(panelWidth, rowCHeight));
                availableWrapper.setPreferredSize(new Dimension(panelWidth, rowDHeight));

                // Update button size variables
                upgradeBtnWidth = (int) (frameWidth * 0.25);
                upgradeBtnHeight = (int) (frameHeight * 0.1);

                // Resize upgrade wrappers
                for (JPanel wrapper : upgradeWrappers) {
                    wrapper.setPreferredSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
                    wrapper.setMinimumSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
                }

                // Resize tool buttons (Row B)
                int toolBtnWidth = (int) (frameWidth * 0.25);
                int toolBtnHeight = (int) (frameHeight * 0.075); // ~8% of height
                for (JButton btn : toolButtons) {
                    btn.setPreferredSize(new Dimension(toolBtnWidth, toolBtnHeight));
                }

                // Resize gen buttons (Row C) with different widths
                int totalWidth = panelWidth;

                int genBtnHeight = (int) (frameHeight * 0.05);

                // Calculate widths
                int gen1Width = (int)(totalWidth * 0.355);
                int gen2to4Width = (int)(totalWidth * 0.1775);
                int gen5Width = (int)(totalWidth * 0.1125);

                // Get wrapper components
                Component[] wrappers = rowCEast.getComponents();

                // Resize each wrapper
                wrappers[0].setPreferredSize(new Dimension(gen1Width, genBtnHeight));
                wrappers[1].setPreferredSize(new Dimension(gen2to4Width, genBtnHeight));
                wrappers[2].setPreferredSize(new Dimension(gen2to4Width, genBtnHeight));
                wrappers[3].setPreferredSize(new Dimension(gen2to4Width, genBtnHeight));
                wrappers[4].setPreferredSize(new Dimension(gen5Width, genBtnHeight));

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
        upgradeBtn.setPreferredSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
        upgradeBtn.setMinimumSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
        upgradeBtn.setMaximumSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));

        // Set initial background color
        upgradePanel.setBackground(STAGE1_BG);
        
        // Add action listener to the button
        upgradeBtn.addActionListener(e -> {
            if (gamestate.tryBuyUpgrade(upgrade, Global.getQuantity())) {
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
        availablePanel.add(wrapper);

        // Make sure the panel is updated
        availablePanel.revalidate();
        availablePanel.repaint();

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
                // Stage 3: Can afford (100%+ of cost)
                if (cookieCount >= cost) {
                    upgradeButtons.get(index).setEnabled(true);
                    upgradePanel.setBackground(STAGE3_BG); // Original color
                    upgradePanel.updateStage(3, cookieCount); // Show real name with green cost
                } 
                // Stage 2: Can't afford but has seen Stage 1
                else {
                    upgradeButtons.get(index).setEnabled(false);
                    upgradePanel.setBackground(STAGE2_BG); // Lighter gray
                    upgradePanel.updateStage(2, cookieCount); // Show real name with red cost
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
        
        // Format the numbers for display
        String formattedAmount = formatNumber(amount);
        String unit = getUnit(amount);
        String formattedCPS = formatNumber(cps);
        String cpsUnit = getUnit(cps);
        
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
    
    // Helper method to format large numbers
    private String formatNumber(double number) {
        if (number < 100000) {
            return formatter.format(number);
        } else if (number < 1000000) {
            return formatter.format(number / 1000);
        } else if (number < 1000000000) {
            return formatter.format(number / 1000000);
        } else if (number < 1000000000000L) {
            return formatter.format(number / 1000000000);
        } else {
            return formatter.format(number / 1000000000000L);
        }
    }
    
    // Helper method to get the unit for large numbers
    private String getUnit(double number) {
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
