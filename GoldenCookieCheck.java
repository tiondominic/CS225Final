import javax.swing.Timer;

public class GoldenCookieCheck {
    private final Gamestate gamestate;

    public GoldenCookieCheck(Gamestate gamestate){
        this.gamestate = gamestate;
    }

    public void updateCounter(double s){
        if(Global.getGCstate() == false){
            Global.setCounter(s);
        }

        if (Global.getCounter() >= 16000) { //after some seconds golden cookie appears
            Global.setGCbool(true);

            ChaosElements chaos = new ChaosElements(gamestate);
            chaos.setUp();

            Timer timer = new Timer(5000, e -> { // after 5 seconds exit window
                chaos.exit(); 
                Global.setGCbool(false);
            });
            timer.setRepeats(false);
            timer.start();

            Global.resetCounter();
        }
    }
    
}
