public class Upgrade {
    private final String name;
    private double baseCost;
    private int owned;
    private final double baseCPS;
    private final double multiplier;
    private final Gamestate gamestate;
    private final boolean CursorUpgrade;

    public Upgrade(String name, double baseCost, double baseCPS, Gamestate gamestate, boolean CursorUpgrade){
        this.gamestate = gamestate;
        this.name = name;
        this.baseCost = baseCost;
        this.baseCPS = baseCPS;
        this.multiplier = 1.15;
        this.owned = 0;
        this.CursorUpgrade = CursorUpgrade;
    }   
    

    public void buy(int Quantity){
        owned += Quantity;
        double totalCPS = baseCPS * owned;

        for(int i=1; i <= Quantity; i++){
            baseCost = baseCost*multiplier;
        }
        
        if(CursorUpgrade){
            gamestate.upgradeClick(baseCPS);
        }
        else{
            gamestate.receive(name, totalCPS);
        }
    }

    public void sell(int Quantity){
        owned -= Quantity;

        for(int i=1; i <= Quantity; i++){
            baseCost = baseCost/multiplier;
        }

        double totalCPS = baseCPS * owned;

        gamestate.receive(name, totalCPS);

    }

    public String getName(){
        return name;
    }

    public int getOwned(){
        return owned;
    }

    public double getCost(int quantity) {
        double totalCost = 0;
        for (int i = 0; i < quantity; i++) {
            totalCost += baseCost * Math.pow(multiplier, i); 
        }
        return totalCost;
    }


}


