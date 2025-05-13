import java.util.HashMap;

public class Gamestate {
    private double CPStotal;
    private double clickPWR;
    public double amount;
    private HashMap<String, Double> items = new HashMap<>();
    
    public Gamestate(double CPS){
        this.CPStotal = CPS;
        this.amount = 0;
        this.clickPWR = 1;
        this.items = new HashMap<>();
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

        CPStotal = Sum;
    }

    public double GetCPS(){
        return CPStotal;
    }

    public void boughtUpgrade(double a){
        amount -= a;
    }

    public boolean tryBuyUpgrade(Upgrade upgrade, int Quantity) {
        double cost = upgrade.getCost(Quantity);
        if (amount >= cost) {
            amount -= cost;
            upgrade.buy(Quantity);
            return true;
        }
        return false;
    }

    
}   
