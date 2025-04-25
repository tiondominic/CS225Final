public class Gamestate {
    private double cookies;
    public double amount;
    
    public Gamestate(double cookies){
        this.cookies = cookies;
        this.amount = 0;
        

    }

    public void tick(double s){
        amount += cookies * s;
    }

    public double getAmount(){
        return amount;
    }
}   
