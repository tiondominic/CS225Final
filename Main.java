
public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(0); 
        GameTick gameTick = new GameTick(gamestate);
        Upgrade test0 = new Upgrade("Cursor", 20, 1, gamestate, true);
        Upgrade test1 = new Upgrade("Baker", 100, 1, gamestate, false);
        Upgrade test2 = new Upgrade("Grandma", 500, 6, gamestate, false);
        Upgrade test3 = new Upgrade("Factory", 2000.0, 30, gamestate, false);
        Upgrade test4 = new Upgrade("Plane", 10000, 200, gamestate, false);
        
        CookieClickerLayoutColored mainwindow = new CookieClickerLayoutColored(gamestate);

        // new Timer(16, e -> {
        //     mainwindow.update(gamestate.getAmount());
        // }).start();
    }
}
