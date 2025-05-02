public class Upgrade {
    private final String name;
    private double baseCost;
    private int owned;
    private final double baseCPS;
    private final double multiplier;
    private final Gamestate gamestate;

    public Upgrade(String name, double baseCost, double baseCPS, Gamestate gamestate){
        this.gamestate = gamestate;
        this.name = name;
        this.baseCost = baseCost;
        this.baseCPS = baseCPS;
        this.multiplier = 1.15;
        this.owned = 0;
    }   

    public void buy(){
        owned++;
        baseCost = baseCost*multiplier;
        gamestate.receive(baseCPS);
    }

    public String getName(){
        return name;
    }

    public int getOwned(){
        return owned;
    }

    public double getCost(){
        return baseCost;
    }

}
