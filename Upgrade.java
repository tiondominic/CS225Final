public class Upgrade {
    private final String name;
    private final double baseCost;
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

    public void buy(int quantity){
        double totalCPS = baseCPS * (owned + quantity);
        owned += quantity;

        if(CursorUpgrade){
            gamestate.upgradeClick(baseCPS);
        } else {
            gamestate.receive(name, totalCPS);
        }
    }

    public void sell(int quantity){
        owned -= quantity;

        double totalCPS = baseCPS * owned;

        gamestate.receive(name, totalCPS);
    }

    public double getCost(int quantity) {
        double cost = 0;
        for (int i = 0; i < quantity; i++) {
            cost += baseCost * Math.pow(multiplier, owned + i);
        }
        return cost;
    }

    public double getSellValue(int quantity) {
        double value = 0;
        for (int i = 0; i < quantity; i++) {
            value += baseCost * Math.pow(multiplier, owned - i - 1);
        }
        return value * 0.9;
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
