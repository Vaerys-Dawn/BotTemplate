package Main;

import sx.blah.discord.api.IDiscordClient;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Globals {

    //Console Bot message Variables
    public static String consoleMessageCID = null;
    public static String creatorID = "153159020528533505";
    public static String botName = "Template Bot";
    public static IDiscordClient client;
    public static boolean isReady = false;
    public static String version;
    public static String avatarFile;

    public static IDiscordClient getClient() {
        return client;
    }

    public static void setClient(IDiscordClient client) {
        Globals.client = client;
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            System.out.println("Bot Version : " + version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String tagSpacer(String from) {
        return from.replace("#spacer#", "\u200b");
    }
}
