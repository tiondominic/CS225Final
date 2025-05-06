import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(0); 
        GameTick gameTick = new GameTick(gamestate);
        Window mainwindow = new Window(gamestate);
        Upgrade test0 = new Upgrade("Cursor", 20, 1, gamestate, true);
        Upgrade test1 = new Upgrade("Baker", 100, 1, gamestate, false);
        Upgrade test2 = new Upgrade("Grandma", 500, 6, gamestate, false);
        Upgrade test3 = new Upgrade("Factory", 2000.0, 30, gamestate, false);
        Upgrade test4 = new Upgrade("Plane", 10000, 200, gamestate, false);

        mainwindow.addUpgrade(test0);
        mainwindow.addUpgrade(test1);
        mainwindow.addUpgrade(test2);
        mainwindow.addUpgrade(test3);
        mainwindow.addUpgrade(test4);


        new Timer(16, e -> {
            mainwindow.update(gamestate.getAmount());
        }).start();
    }
}
