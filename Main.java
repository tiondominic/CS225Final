import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        Gamestate gamestate = new Gamestate(10.0); // 10 cookies per second
        GameTick gameTick = new GameTick(gamestate); // updates 60 times/second

        // Timer to print cookie amount every 1 second
        Timer printTimer = new Timer(1000, e -> {
            System.out.printf("Cookies: %.0f%n", gamestate.getAmount());
        });
        printTimer.start();

        
        try {
            Thread.sleep(11000);  
        } catch (InterruptedException ignored) {}

        
        gameTick.stop();
        printTimer.stop();
        System.out.println("Game stopped after 10 seconds.");
    }
}
