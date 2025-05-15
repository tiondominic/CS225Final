public class Global {
    public static int Quantity = 1;
    public static String Mode = "BUY";

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
}
