import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(10); 
        GameTick gameTick = new GameTick(gamestate); 
        Window mainwindow = new Window(gamestate);
        Upgrade test1 = new Upgrade("test1", 1.0, 5.0, gamestate);

        mainwindow.addUpgrade(test1);
        mainwindow.addUpgrade(test1);
        mainwindow.addUpgrade(test1);
        mainwindow.addUpgrade(test1);

        new Timer(16, e -> {
            mainwindow.update(gamestate.getAmount());
        }).start();
        
        
    }
}
