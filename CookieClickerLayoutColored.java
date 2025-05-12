import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.ArrayList;

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

public class CookieClickerLayoutColored extends JFrame {

    // Example dynamic row counts
    private int purchasedUpgrades = 10;
    private int availableUpgrades = 5;

    List<JButton> upgradeButtons = new ArrayList<>();
    List<JButton> toolButtons = new ArrayList<>();
    List<JButton> genButtons = new ArrayList<>();

    List<JButton> topButtons = new ArrayList<>();
    List<JButton> bottomButtons = new ArrayList<>();
    List<JLabel> purchasedLabels = new ArrayList<>();

    List<JPanel> centerRows = new ArrayList<>();
    List<JButton> firstRowButtons = new ArrayList<>();
    List<JPanel> rowACenterColumnA = new ArrayList<>();
    List<JPanel> rowACenterColumnB = new ArrayList<>();
    List<JPanel> secondRowPanels = new ArrayList<>();

    
    public CookieClickerLayoutColored() {
        setTitle("Cookie Clicker Layout - Colored");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set size to 80% width and 80% height of the screen, and center it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);
        setSize(width, height);
        setLocationRelativeTo(null); // Centers the window

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

        JLabel labelrow1B = new JLabel("Grandma Slavery" + "'s" + " " + "Bakery"); //18 Characters Max
        row1B.add(labelrow1B);
        rowBCenter.add(row1B);
        secondRowPanels.add(row1B);

        // Row 2 (Button with own size)
        JPanel row2B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row2B.setBackground(new Color(0, 0, 0, 64)); // 128 = 50% transparency
        row2B.setOpaque(false);

        Image img = new ImageIcon("assets/main_cookie_3.png").getImage();
        ImageButton actionButtonrow2B = new ImageButton(null, img);

        row2B.add(actionButtonrow2B);
        rowBCenter.add(row2B);
        secondRowPanels.add(row2B);

        // Row 3 (Text or Label)
        JPanel row3B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row3B.setBackground(new Color(0, 0, 0, 64)); // 128 = 50% transparency
        row3B.setOpaque(true);

        JLabel labelrow3B = new JLabel("1.970");
        row3B.add(labelrow3B);
        rowBCenter.add(row3B);
        secondRowPanels.add(row3B);

        // Row 4 (Text or Label)
        JPanel row4B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row4B.setBackground(new Color(0, 0, 0, 64));
        row4B.setOpaque(true);

        JLabel labelrow4B = new JLabel("Trillion" + " " + "Cookies");
        row4B.add(labelrow4B);
        rowBCenter.add(row4B);
        secondRowPanels.add(row4B);

        // Row 5 (Text or Label)
        JPanel row5B = new JPanel(new GridBagLayout()); // Instead of default FlowLayout
        row5B.setBackground(new Color(0, 0, 0, 64)); // 128 = 50% transparency
        row5B.setOpaque(true);
        
        JLabel labelrow5B = new JLabel("140.591" + " " + "Million" + " " + "per second");
        row5B.add(labelrow5B);
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
                labelrow3B.setFont(scaledFont2);
                labelrow4B.setFont(scaledFont2);
                labelrow5B.setFont(scaledFont3);

                // Set font color for all labels
                Color fontColor = new Color(0xFFFFFF);  // Dark slate gray
                labelrow1B.setForeground(fontColor);
                labelrow3B.setForeground(fontColor);
                labelrow4B.setForeground(fontColor);
                labelrow5B.setForeground(fontColor);

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

        // === Row C (1 row x 5 columns) ===
        JPanel rowCEast = new JPanel(new GridLayout(1, 5));
        rowCEast.setBackground(new Color(0xA7EAA7));
        for (int i = 0; i < 5; i++) {
            JButton gen = new JButton("Gen " + (i + 1));
            gen.setBackground(new Color(0xEAE77D));
            genButtons.add(gen);
            rowCEast.add(gen);
        }
        eastPanel.add(rowCEast);

        // === Row D (Scrollable available upgrades) ===
        JPanel availablePanel = new JPanel();
        availablePanel.setLayout(new BoxLayout(availablePanel, BoxLayout.Y_AXIS));
        availablePanel.setBackground(new Color(0x7B89C4));
        for (int i = 0; i < availableUpgrades; i++) {
            JButton upgradeBtn = new JButton("Available Upgrade " + (i + 1));
            upgradeBtn.setBackground(new Color(0xF7F4B7));
            upgradeButtons.add(upgradeBtn); // store reference

            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrapper.setOpaque(false);
            wrapper.add(upgradeBtn);
            availablePanel.add(wrapper);
        }
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

                // Resize upgrade buttons
                int upgradeBtnWidth = (int) (frameWidth * 0.25);
                int upgradeBtnHeight = (int) (frameHeight * 0.1); // ~8% of height
                for (JButton btn : upgradeButtons) {
                    btn.setPreferredSize(new Dimension(upgradeBtnWidth, upgradeBtnHeight));
                }

                // Resize tool buttons (Row B)
                int toolBtnWidth = (int) (frameWidth * 0.25);
                int toolBtnHeight = (int) (frameHeight * 0.075); // ~8% of height
                for (JButton btn : toolButtons) {
                    btn.setPreferredSize(new Dimension(toolBtnWidth, toolBtnHeight));
                }

                // Resize gen buttons (Row C)
                int genBtnWidth = (int) (frameWidth * 0.25);
                int genBtnHeight = (int) (frameHeight * 0.05); // ~8% of height
                for (JButton btn : genButtons) {
                    btn.setPreferredSize(new Dimension(genBtnWidth, genBtnHeight));
                }

                eastPanel.revalidate();
                eastPanel.repaint();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CookieClickerLayoutColored::new);
    }
}

