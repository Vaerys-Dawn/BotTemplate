package Listeners;

import Handlers.DMHandler;
import Handlers.FileHandler;
import Handlers.MessageHandler;
import Main.Constants;
import Main.Globals;
import Main.TimedEvents;
import Main.Utility;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class AnnotationListener {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    /**
     * Sets up the relevant files for each guild.
     */
    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getID();
        logger.info("Starting Guild init proccess for Guild with ID: " + guildID);

        //Init Cooldowns
        TimedEvents.addGuildCoolDown(guildID);

        //Create POGO templates
        GuildConfig guildConfig = new GuildConfig();

        //Init Files
        guildConfig.initConfig();
        guildConfig.setProperlyInit(true);
        FileHandler.createDirectory(Utility.getDirectory(guildID));
        FileHandler.createDirectory(Utility.getDirectory(guildID, true));
        FileHandler.createDirectory(Utility.getGuildImageDir(guildID));
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);

        //Update Variables.
        //Guild Config
        updateVariables(guild);

        logger.info("Finished Initialising Guild With ID: " + guildID);
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        try {
            Globals.isReady = true;
            event.getClient().changeStatus(Status.game("Starbound"));
            if (!event.getClient().getOurUser().getName().equals(Globals.botName)) {
                event.getClient().changeUsername(Globals.botName);
            }

        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }


    @EventSubscriber
    public void onMessageRecivedEvent(MessageReceivedEvent event) {
        if (event.getMessage().getChannel().isPrivate()) {
            new DMHandler(event.getMessage());
            return;
        }
        IMessage message = event.getMessage();
        IGuild guild = message.getGuild();
        IChannel channel = message.getChannel();
        IUser author = message.getAuthor();
        String messageLC = message.toString().toLowerCase();
        String args = "";
        String command = "";
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guild.getID(), Constants.FILE_GUILD_CONFIG, GuildConfig.class);

        //Set Console Response Channel.
        if (author.getID().equals(Globals.creatorID)) {
            Globals.consoleMessageCID = channel.getID();
        }

        if (messageLC.startsWith(guildConfig.getPrefixCommand().toLowerCase())) {
            String[] splitMessage = message.toString().split(" ");
            command = splitMessage[0];
            StringBuilder getArgs = new StringBuilder();
            getArgs.append(message.toString());
            getArgs.delete(0, splitMessage[0].length() + 1);
            args = getArgs.toString();
        }

        //message and command handling
        new MessageHandler(command, args, message);
    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        IGuild guild = event.getMessage().getGuild();
        IUser author = event.getMessage().getAuthor();
        String guildOwnerID = guild.getOwner().getID();
        IChannel channel = event.getMessage().getChannel();
        String sailMentionID = event.getClient().getOurUser().mention();
        String prefix = Constants.PREFIX_COMMAND;
        String message = event.getMessage().toString();
        String[] splitMessage;
        if (channel.isPrivate()) {
            new DMHandler(event.getMessage());
            return;
        }
        if (event.getMessage().mentionsEveryone() || event.getMessage().mentionsHere()) {
            return;
        }
        if (author.getID().equals(Globals.getClient().getOurUser().getID())) {
            return;
        }

        /**This lets you set the guild's Prefix if you run "@Bot SetCommandPrefix [New Prefix]"*/
        if (author.getID().equals(guildOwnerID) || author.getID().equals(Globals.creatorID)) {
            splitMessage = message.split(" ");
            if (splitMessage[0] != null && splitMessage[0].equals(sailMentionID)){
                if (splitMessage[1] != null && splitMessage[1].toLowerCase().equals("setcommandprefix")){
                    if (splitMessage[2] != null){
                        prefix = splitMessage[2];
                        String guildID = guild.getID();
                        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
                        guildConfig.setPrefixCommand(prefix);
                        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
                    }
                }
            }
        }
    }

    @EventSubscriber
    public void onRoleUpdateEvent(RoleUpdateEvent event) {
        updateVariables(event.getGuild());
    }

    @EventSubscriber
    public void onRoleDeleteEvent(RoleDeleteEvent event) {
        updateVariables(event.getGuild());
    }

    @EventSubscriber
    public void onChannelUpdateEvent(ChannelUpdateEvent event) {
        updateVariables(event.getNewChannel().getGuild());
    }

    @EventSubscriber
    public void onChannelDeleteEvent(ChannelDeleteEvent event){
        updateVariables(event.getChannel().getGuild());
    }

    private void updateVariables(IGuild guild) {
        String guildID = guild.getID();
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        guildConfig.updateVariables(guild);
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
    }
}
