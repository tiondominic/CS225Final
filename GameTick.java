import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class GameTick {
    private final Timer timer;
    private long lastTime;

    public GameTick(Gamestate gamestate, GoldenCookieCheck GCcheck) {
        lastTime = System.nanoTime();

        timer = new Timer(16, (ActionEvent e) -> {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0; 
            lastTime = now;

            gamestate.tick(deltaTime); 
            GCcheck.updateCounter(16); // next time to return the value instead of doing this
        });

        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
