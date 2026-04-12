package ans;

import java.awt.*;

public class CommonConstants 
{
    // color hex values
    public static final Color PRIMARY_COLOR = Color.decode("#000000");
    public static final Color SECONDARY_COLOR = Color.decode("#14213d");
    public static final Color TEXT_COLOR = Color.decode("#fca311");
    public static final Color BORDER_ORANGE = Color.decode("#fca311");

    // mysql credentials
    public static final String DB_URL = ""; // here you should have your passwords and information
    public static final String DB_USERNAME = ""; // here you should have your passwords and information
    public static final String DB_PASSWORD = ""; // here you should have your passwords and information
    public static final String DB_USERS_TABLE = "users"; // users table
    public static final String DB_ITEMS_TABLE = "items"; // items table
    
    public static String currentUser = ""; // tracker of who is logged in

}
