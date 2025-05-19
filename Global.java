public class Global {
    public static int Quantity = 1;
    public static String Mode = "BUY";
    public static boolean GoldenCookie = false;
    public static double Counter = 0;
    // Set a more reasonable default threshold (5 minutes)
    private static double nextGCThreshold = 300_000;

    // Rest of the code remains the same
    public static void setQuantity(int newQuantity){
        Quantity = newQuantity;
    }

    public static int getQuantity(){
        return Quantity;
    }

    public static void setMode(String mode){
        Mode = mode;
    }

    public static String getMode(){
        return Mode;
    }

    public static void setGCbool(boolean a){
        GoldenCookie = a;
    }

    public static boolean getGCstate(){
        return GoldenCookie;
    }


    public static void setCounter(double s){
        Counter = s;
    }

    public static void addCounter(double s){
        Counter += s;
    }

    public static double getCounter(){
        return Counter;
    }
    
    public static void resetCounter(){
        Counter = 0;
    }

    public static double getNextGCThreshold() {
        return nextGCThreshold;
    }

    public static void setNextGCThreshold(double threshold) {
        nextGCThreshold = threshold;
    }
}