import java.util.HashMap;

public class Gamestate {
    private double CPStotal;
    private double clickPWR;
    public double amount;
    private HashMap<String, Double> items = new HashMap<>();
    private HashMap<String, Double> TotalCPS = new HashMap<>();
    
    public Gamestate(double CPS){
        this.CPStotal = CPS;
        this.amount = 0;
        this.clickPWR = 1;
        this.items = new HashMap<>();
        this.TotalCPS = new HashMap<>();

        TotalCPS.put("StartingCPS", CPS);
    }

    public void upgradeClick(double a){
        clickPWR += a;
    }

    public void tick(double s){
        amount += CPStotal * s;
    }

    public double getAmount(){
        return amount;
    }

    public void Click(){
        amount += clickPWR;
    }

    public double getClickingPower() {
        return clickPWR;
    }

    public void receive(String name, double CPS){
        items.put(name, CPS);

        double Sum = 0;
        for (double itemCPS : items.values()) {
            Sum += itemCPS;
        }

        TotalCPS.put("CPSupgrades", Sum);
    }

    public double GetCPS(){
        double Sum = 0;
        for (double itemCPS : TotalCPS.values()) {
            Sum += itemCPS;
        }
        CPStotal = Sum;
        return CPStotal;
    }

    public void boughtUpgrade(double a){
        amount -= a;
    }

    public boolean Transact(Upgrade upgrade, int Quantity, String Mode) {
        double cost = upgrade.getCost(Quantity);

        if (Mode.equalsIgnoreCase("BUY")) {
            if (amount >= cost) {
                amount -= cost;
                upgrade.buy(Quantity);
                return true;
            }
            return false;
        }

        if (Mode.equalsIgnoreCase("SELL")) {
            if(Quantity <= upgrade.getOwned()){
                upgrade.sell(Quantity);
                amount += cost *0.9;
                return true;
            }
        }

        return false;
    }
}   
