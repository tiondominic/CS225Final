import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class GameTick {
    private final Timer timer;
    private long lastTime;

    public GameTick(Gamestate gamestate) {
        lastTime = System.nanoTime();

        timer = new Timer(16, (ActionEvent e) -> {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0; 
            lastTime = now;

            gamestate.tick(deltaTime); 
        });

        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
