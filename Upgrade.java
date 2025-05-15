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
        this.CursorUpgrade = CursorUpgrade; //maybe get rid of cursor upgrade
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

    public void sell(int quantity){
        owned -= quantity;

        double totalCPS = baseCPS * owned;
        for(int i=1; i <= quantity; i++){
            baseCost = baseCost/multiplier;
        }

        gamestate.receive(name, totalCPS);
    }

    public double getCost(int quantity) {
        double totalCost = 0;
        for (int i = 0; i < quantity; i++) {
            totalCost += baseCost * Math.pow(multiplier, i); 
        }
        return totalCost;
    }

    public double getSellValue(int quantity) {

        double value = 0;
        for (int i = 0; i < quantity; i++) {
            value += baseCost * Math.pow(multiplier, i);
        }
        return value * 0.25;
    }

    public String getName(){
        return name;
    }

    public int getOwned(){
        return owned;
    }

    public int getUpgradeOwnedCount(Upgrade upgrade) {
        return upgrade.getOwned();
    }
}
