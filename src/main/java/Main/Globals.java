package Main;

import Commands.Admin.ChannelHere;
import Commands.Admin.Shutdown;
import Commands.Admin.UpdateAvatar;
import Commands.Command;
import Commands.General.Hello;
import Commands.Help.Help;
import Commands.Help.Info;
import Handlers.FileHandler;
import Objects.DailyObject;
import Objects.GuildContentObject;
import POGOs.Config;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Globals {

    public static String botName = null;
    public static String creatorID = null;
    public static String defaultPrefixCommand = null;
    public static String defaultAvatarFile = null;
    public static boolean doDailyAvatars = false;
    public static String dailyAvatarName = null;
    public static String playing = null;
    public static boolean isReady = false;
    public static String version;
    public static IDiscordClient client;
    public static boolean isModifingFiles = false;
    private static ArrayList<GuildContentObject> guildContentObjects = new ArrayList<>();
    private static ArrayList<Command> commands = new ArrayList<>();
    private static ArrayList<DailyObject> dailyObjects = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(Globals.class);

    public static void initConfig(IDiscordClient ourClient, Config config) {
        client = ourClient;
        botName = config.botName;
        creatorID = config.creatorID;
        defaultPrefixCommand = config.defaultPrefixCommand;
        defaultAvatarFile = config.defaultAvatarFile;
        doDailyAvatars = config.doDailyAvatars;
        dailyAvatarName = config.dailyAvatarName;
        playing = config.playing;
        dailyObjects = config.dailyObjects;
        initCommands();
    }

    private static void initCommands() {
        //Admin commands
        commands.add(new ChannelHere());
        commands.add(new Shutdown());
        commands.add(new UpdateAvatar());
        //General commands
        commands.add(new Hello());
        //Help commands
        commands.add(new Help());
        commands.add(new Info());
        //RoleSelect commands
        logger.info(commands.size() + " Commands Loaded.");

        //DM commands
        //validate commands
        validate();
    }

    private static void validate() throws IllegalArgumentException {
        for (Command c : commands) {
            logger.debug("Initialising Command: " + c.getClass().getName());
            if (c.names().length == 0)
                throw new IllegalArgumentException(c.getClass().getName() + " Command Name cannot be null.");
            if (c.type() == null || c.type().isEmpty())
                throw new IllegalArgumentException(c.getClass().getName() + " Command Type cannot be null.");
            if (c.description() == null || c.description().isEmpty())
                throw new IllegalArgumentException(c.getClass().getName() + " Command Desc cannot be null.");
            if (c.requiresArgs() && (c.usage() == null || c.usage().isEmpty()))
                throw new IllegalArgumentException(c.getClass().getName() + " Command Usage cannot be null if RequiresArgs is true.");
        }
    }

    public static void validateConfig() throws IllegalArgumentException {
        IUser creator = client.getUserByID(creatorID);
        if (creator == null)
            throw new IllegalArgumentException("Creator ID is invalid.");
        if (botName == null || botName.isEmpty())
            throw new IllegalArgumentException("Bot name cannot be empty.");
        if (botName.length() > 32)
            throw new IllegalArgumentException("botName cannot be longer than 32 chars.");
        if (defaultPrefixCommand == null || defaultPrefixCommand.isEmpty())
            throw new IllegalArgumentException("defaultPrefixCommand cannot be empty.");
        if (defaultPrefixCommand.contains(" "))
            throw new IllegalArgumentException("defaultPrefixCommand cannot contain spaces.");
        if (defaultPrefixCommand.contains("\n"))
            throw new IllegalArgumentException("defaultPrefixCommand cannot contain Newlines.");
        if (doDailyAvatars) {
            if (!dailyAvatarName.contains("#day#"))
                throw new IllegalArgumentException("dailyAvatarName must contain #day# for the feature to work as intended.");
            for (DailyObject d : dailyObjects) {
                if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName())))
                    throw new IllegalArgumentException("File " + Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName() + " does not exist.");
                else if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile)))
                    throw new IllegalArgumentException("File" + Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile + " does not exist.");
            }
        }
    }

    public static void initGuild(String guildID) {
        for (GuildContentObject contentObject : guildContentObjects) {
            System.out.println(guildID);
            System.out.println(contentObject.getGuildID());
            if (guildID.equals(contentObject.getGuildID())) {
                return;
            }
        }
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);

        IGuild guild = client.getGuildByID(guildID);
        guildConfig.updateVariables(guild);

        GuildContentObject guildContentObject = new GuildContentObject(guildID, guildConfig);
        guildContentObjects.add(guildContentObject);
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            logger.info("Bot version : " + version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static IDiscordClient getClient() {
        return client;
    }


    public static GuildContentObject getGuildContent(String guildID) {
        for (GuildContentObject storage : guildContentObjects) {
            if (storage.getGuildID().equals(guildID)) {
                return storage;
            }
        }
        return null;
    }

    public static ArrayList<GuildContentObject> getGuildContentObjects() {
        return guildContentObjects;
    }

    public static void saveFiles() {
        logger.debug("Saving Files.");
        Globals.isModifingFiles = true;
        Globals.getGuildContentObjects().forEach(GuildContentObject::saveFiles);
        Globals.isModifingFiles = false;
    }

    public static void unloadGuild(String id) {
        for (int i = 0; i < guildContentObjects.size(); i++) {
            if (guildContentObjects.get(i).getGuildID().equals(id)) {
                logger.info("> Disconnected from Guild with ID : " + id);
                guildContentObjects.remove(i);
            }
        }
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    public static ArrayList<DailyObject> getDailyObjects() {
        return dailyObjects;
    }
}
