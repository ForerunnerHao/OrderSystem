package menu.utils;

public class ColorfulConsole {
    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_BOLD = "\u001B[1m";
//    public static final String ANSI_UNDERLINE = "\u001B[4m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_CYAN = "\u001B[36m";

    public static String printSuccessInfo(String str){
        return ANSI_GREEN + str + ANSI_RESET;
    }

    public static String printFailInfo(String str){
        return ANSI_RED + str + ANSI_RESET;
    }
}