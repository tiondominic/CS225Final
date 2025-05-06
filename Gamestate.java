public class Gamestate {
    private double CPStotal;
    private double clickPWR;
    public double amount;
    
    public Gamestate(double CPS){
        this.CPStotal = CPS;
        this.amount = 0;
        this.clickPWR = 1;
        
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

    public void receive(double a){
        CPStotal += a;
    }

    public double GetCPS(){
        return CPStotal;
    }

    public void boughtUpgrade(double a){
        amount -= a;
    }

    public boolean tryBuyUpgrade(Upgrade upgrade) {
        double cost = upgrade.getCost();
        if (amount >= cost) {
            amount -= cost;
            upgrade.buy();
            return true;
        }
        return false;
    }
}   
