public class Gamestate {
    private double CPStotal;
    private double clickPWR;
    public double amount;
    
    public Gamestate(double CPS){
        this.CPStotal = CPS;
        this.amount = 0;
        this.clickPWR = 1;
        

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

    public double GetCPS(){
        return CPStotal;
    }
}   
