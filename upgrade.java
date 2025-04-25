public class Upgrade {
    private String name;
    private double baseCost;
    private int owned;
    private double baseCPS;
    private double multiplier;
    private double clickPWR;
    private Gamestate gamestate;

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
        gamestate.receive(baseCPS*owned);
    }

    // public void updateCPS(){
    //     gamestate.receive(baseCPS*owned); // potential problem where cps is updated by how many buy upgrade -> 
    //                                       // cps now 5 -> buy new upgrade basecps is 10 -> cps is now 15 expected 1
    // }

    public String getName(){
        return name;
    }

}
