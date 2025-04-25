import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


public class GameTick {
    private Timer timer;

    public GameTick(Gamestate gamestate){
        
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            gamestate.tick(0.03); 
            }
        });
        timer.start();
    }
    
    public void stop(){
        timer.stop();
    }
}
