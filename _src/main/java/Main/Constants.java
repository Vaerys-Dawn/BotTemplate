package Main;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class Constants {

    //Command prefix constants
    public static final String PREFIX_COMMAND = "$";
    public static final String PREFIX_INDENT = "    ";
    public static final String PREFIX_EDT_LOGGER_INDENT = "                                     ";

    //-------Command Constants---------

    //Type Constants
    public static final String TYPE_GENERAL = "General";
    public static final String TYPE_ADMIN = "Admin";
    public static final String TYPE_HELP = "Help";

    //Tag Types
    public static final String TAG_TYPE_ALL = "all";
    public static final String TAG_TYPE_CC = "CC";
    public static final String TAG_TYPE_INFO = "Info";

    //Channel Constants
    public static final String CHANNEL_ANY = "Any";
    public static final String CHANNEL_GENERAL = "General";
    public static final String CHANNEL_SERVER_LOG = "ServerLog";
    public static final String CHANNEL_ADMIN_LOG = "AdminLog";
    //-------FilePath Constants--------

    //Directories
    public static final String DIRECTORY_STORAGE = "Storage/";
    public static final String DIRECTORY_BACKUPS = DIRECTORY_STORAGE + "Backups/";
    public static final String DIRECTORY_GLOBAL_IMAGES = DIRECTORY_STORAGE + "Images/";
    public static final String DIRECTORY_GUILD_IMAGES = "Images/";
    public static final String DIRECTORY_TEMP = DIRECTORY_STORAGE + "Temp/";
    public static final String DIRECTORY_ERROR = DIRECTORY_STORAGE + "Error/";


    //Files
    public static final String FILE_TOKEN = DIRECTORY_STORAGE + "Token.txt";
    public static final String FILE_GUILD_CONFIG = "Guild_Config.json";

}
