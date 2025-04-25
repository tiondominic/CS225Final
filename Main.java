import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(1); 
        GameTick gameTick = new GameTick(gamestate); 
        Window mainwindow = new Window(gamestate); // Problem don't know how to fix

        new Timer(16, e -> {
            mainwindow.update(gamestate.getAmount());
        }).start();
        
        
    }
}
