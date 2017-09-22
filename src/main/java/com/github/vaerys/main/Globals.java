package com.github.vaerys.main;

import com.github.vaerys.commands.CommandInit;
import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.GuildFile;
import com.github.vaerys.objects.discord.GuildObject;
import com.github.vaerys.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

public class Globals {
    public static long creatorID = 153159020528533505L;
    static List<Command> commands;
    public static boolean savingFiles = false;
    public static boolean shuttingDown = false;
    public static boolean isReady = false;


    private static String version = "N/a";

    private static List<GuildObject> guilds = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(Globals.class);
    public static List<IMessage> messages = new ArrayList<>();


    public Globals() {
        commands = CommandInit.get();
    }

    public static List<Command> getCommands() {
        return commands;
    }

    public static void initGuild(GuildObject guild) {
        for (GuildObject g : guilds) {
            if (g.longID == guild.longID) {
                return;
            }
        }
        guilds.add(guild);
    }

    public static void saveFiles() {
        if (shuttingDown) {
            return;
        }
        savingFiles = true;
        logger.debug("Saving Files.");
        for (GuildObject g : guilds) {
            for (GuildFile file : g.guildFiles) {
                file.flushFile();
            }
        }
        savingFiles = false;
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            logger.info("Bot version : " + version);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
    }

    public static GuildObject getGuildObject(long guildID) {
        for (GuildObject g : guilds) {
            if (g.longID == guildID) {
                return g;
            }
        }
        return new GuildObject();
    }

    public static void unloadGuild(long id) {
        ListIterator iterator = guilds.listIterator();
        while (iterator.hasNext()) {
            GuildObject guild = (GuildObject) iterator.next();
            if (guild.longID == id) {
                logger.trace("Guild: " + guild.get().getName() + " unloaded.");
                iterator.remove();
            }
        }
    }

}
