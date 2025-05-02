import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(0); 
        GameTick gameTick = new GameTick(gamestate); 
        Window mainwindow = new Window(gamestate);
        Upgrade test1 = new Upgrade("test1", 1.0, 1.0, gamestate);
        Upgrade test2 = new Upgrade("test2", 1.0, 2.0, gamestate);
        Upgrade test3 = new Upgrade("test3", 1.0, 3.0, gamestate);
        Upgrade test4 = new Upgrade("test4", 1.0, 4.0, gamestate);

        mainwindow.addUpgrade(test1);
        mainwindow.addUpgrade(test2);
        mainwindow.addUpgrade(test3);
        mainwindow.addUpgrade(test4);


        new Timer(16, e -> {
            mainwindow.update(gamestate.getAmount());
        }).start();
    }
}
