import javax.swing.Timer;
import javax.swing.JFrame;
import java.awt.Frame; // Add this import for Frame.getFrames()

public class GoldenCookieCheck {
    private final Gamestate gamestate;
    private long lastUpdateTime; // Track the last update time
    
    public GoldenCookieCheck(Gamestate gamestate){
        this.gamestate = gamestate;
        this.lastUpdateTime = System.currentTimeMillis();
        
        // Initialize with a proper threshold on creation
        Global.setNextGCThreshold(getNextGoldenCookieSpawnTimeMillis());
        System.out.println("Initial golden cookie will appear in: " + 
            (Global.getNextGCThreshold() / 1000 / 60) + " minutes");
    }

    public void updateCounter(double deltaSeconds) {
        // Only proceed if the game window is not minimized and golden cookie is not active
        if (!Global.getGCstate()) {
            // Calculate actual elapsed time since last update to prevent timing issues
            long currentTime = System.currentTimeMillis();
            long actualElapsedMs = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            
            // Cap the elapsed time to prevent huge jumps if the game was paused
            if (actualElapsedMs > 5000) actualElapsedMs = 5000;
            
            // Add the actual elapsed time to the counter
            Global.addCounter(actualElapsedMs);

            // Only log occasionally to reduce console spam
            if (Global.getCounter() % 30000 < 20) {
                System.out.println("Counter (ms): " + Global.getCounter() + 
                                ", Threshold (ms): " + Global.getNextGCThreshold() + 
                                ", Time remaining: " + 
                                ((Global.getNextGCThreshold() - Global.getCounter()) / 1000 / 60) + " minutes");
            }

            if (Global.getCounter() >= Global.getNextGCThreshold()) {
                Global.setGCbool(true);

                // Check if any JFrame is minimized
                boolean anyMinimized = false;
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof JFrame && frame.isVisible() && 
                        ((JFrame)frame).getState() == JFrame.ICONIFIED) {
                        anyMinimized = true;
                        break;
                    }
                }

                // Only spawn if no window is minimized
                if (!anyMinimized) {
                    // Create ChaosElements with just the gamestate
                    ChaosElements chaos = new ChaosElements(gamestate);
                    chaos.setUp();

                    // Golden cookie stays for 15 seconds
                    Timer timer = new Timer(15000, e -> {
                        chaos.exit();
                        Global.setGCbool(false);
                        Global.resetCounter();

                        // Set the next threshold
                        Global.setNextGCThreshold(getNextGoldenCookieSpawnTimeMillis());
                        
                        System.out.println("Golden cookie disappeared. Next appearance in: " + 
                            (Global.getNextGCThreshold() / 1000 / 60) + " minutes");
                    });

                    timer.setRepeats(false);
                    timer.start();
                } else {
                    // If window is minimized, just reset and try again later
                    Global.setGCbool(false);
                    Global.resetCounter();
                    Global.setNextGCThreshold(getNextGoldenCookieSpawnTimeMillis());
                }
            }
        }
    }

    public static double getNextGoldenCookieSpawnTimeMillis() {
        // Minimum time: 5 minutes, Maximum time: 15 minutes
        double Tmin = 300_000; // 5 minutes
        double Tmax = 900_000; // 15 minutes
        
        // Use a more balanced distribution
        double randomFactor = Math.random();
        
        // This creates a distribution that favors values in the middle range
        // but still allows for both minimum and maximum values
        double threshold;
        
        if (randomFactor < 0.1) {
            // 10% chance of minimum time (5 minutes)
            threshold = Tmin;
        } else if (randomFactor > 0.9) {
            // 10% chance of maximum time (15 minutes)
            threshold = Tmax;
        } else {
            // 80% chance of a value between 7 and 13 minutes
            double normalizedRandom = (randomFactor - 0.1) / 0.8; // rescale to 0-1
            threshold = Tmin + 120_000 + normalizedRandom * 360_000;
        }
        
        System.out.println("Next golden cookie will appear in: " + 
            (threshold / 1000 / 60) + " minutes");
            
        return threshold;
    }
}