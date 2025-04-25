public class upgrade {
    private String name;
    private double baseCost;
    private int owned;
    private double baseCPS;
    private double multiplier;

    public upgrade(String name, double baseCost, double baseCPS){
        this.name = name;
        this.baseCost = baseCost;
        this.baseCPS = baseCPS;
        this.multiplier = 1.15;
        this.owned = 0;
    }   

    public void buy(){
        owned++;
        baseCost = baseCost*multiplier;
    }
}
