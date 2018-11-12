package CarpoolingApp;


public class ConversationHelper {
    private static int currentID = 0;

    public static String getNextID() {
        return "conv_".concat(String.valueOf(++currentID));
    }
}
