import javax.swing.Timer;
import javax.swing.JFrame;

public class DecoyGoldenCookieCheck {
    private final Gamestate gamestate;
    private final Gamewindow gameWindow;
    private long lastUpdateTime;
    
    public DecoyGoldenCookieCheck(Gamestate gamestate, Gamewindow gameWindow){
        this.gamestate = gamestate;
        this.gameWindow = gameWindow;
        this.lastUpdateTime = System.currentTimeMillis();
        
        // Initialize with a proper threshold on creation
        Global.setNextDecoyGCThreshold(getNextDecoyGoldenCookieSpawnTimeMillis());
        System.out.println("Initial decoy golden cookie will appear in: " + 
            (Global.getNextDecoyGCThreshold() / 1000 / 60) + " minutes");
    }

    public void updateCounter(double deltaSeconds) {
        // Only proceed if the game window is not minimized and decoy golden cookie is not active
        if (!Global.getDecoyGCstate()) {
            // Calculate actual elapsed time since last update to prevent timing issues
            long currentTime = System.currentTimeMillis();
            long actualElapsedMs = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            
            // Cap the elapsed time to prevent huge jumps if the game was paused
            if (actualElapsedMs > 5000) actualElapsedMs = 5000;
            
            // Add the actual elapsed time to the counter
            Global.addDecoyCounter(actualElapsedMs);

            // Only log occasionally to reduce console spam
            if (Global.getDecoyCounter() % 30000 < 20) {
                System.out.println("Decoy Counter (ms): " + Global.getDecoyCounter() + 
                                ", Decoy Threshold (ms): " + Global.getNextDecoyGCThreshold() + 
                                ", Time remaining: " + 
                                ((Global.getNextDecoyGCThreshold() - Global.getDecoyCounter()) / 1000 / 60) + " minutes");
            }

            if (Global.getDecoyCounter() >= Global.getNextDecoyGCThreshold()) {
                Global.setDecoyGCbool(true);

                // Check if game window is minimized
                boolean isMinimized = !gameWindow.isVisible() || 
                      (gameWindow.getExtendedState() & JFrame.ICONIFIED) != 0;

                // Only spawn if window is not minimized
                if (!isMinimized) {
                    // Create DecoyChaosElements
                    DecoyChaosElements decoyChao = new DecoyChaosElements(gamestate);
                    decoyChao.setUp();

                    // Decoy golden cookie stays for 15 seconds
                    Timer timer = new Timer(15000, e -> {
                        decoyChao.exit();
                        Global.setDecoyGCbool(false);
                        Global.resetDecoyCounter();

                        // Set the next threshold
                        Global.setNextDecoyGCThreshold(getNextDecoyGoldenCookieSpawnTimeMillis());
                        
                        System.out.println("Decoy golden cookie disappeared. Next appearance in: " + 
                            (Global.getNextDecoyGCThreshold() / 1000 / 60) + " minutes");
                    });

                    timer.setRepeats(false);
                    timer.start();
                } else {
                    // If window is minimized, just reset and try again later
                    Global.setDecoyGCbool(false);
                    Global.resetDecoyCounter();
                    Global.setNextDecoyGCThreshold(getNextDecoyGoldenCookieSpawnTimeMillis());
                }
            }
        }
    }

    public static double getNextDecoyGoldenCookieSpawnTimeMillis() {
        // Minimum time: 7 minutes, Maximum time: 20 minutes (longer intervals than regular golden cookies)
        double Tmin = 420_000; // 7 minutes
        double Tmax = 1_200_000; // 20 minutes
        
        // Use a more balanced distribution
        double randomFactor = Math.random();
        
        double threshold;
        
        if (randomFactor < 0.1) {
            // 10% chance of minimum time
            threshold = Tmin;
        } else if (randomFactor > 0.9) {
            // 10% chance of maximum time
            threshold = Tmax;
        } else {
            // 80% chance of a value between 10 and 17 minutes
            double normalizedRandom = (randomFactor - 0.1) / 0.8;
            threshold = 30_000;//Tmin + 180_000 + normalizedRandom * 420_000;
        }
        
        System.out.println("Next decoy golden cookie will appear in: " + 
            (threshold / 1000 / 60) + " minutes");
            
        return threshold;
    }
}