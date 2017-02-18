package Handlers;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;


/**
 * This Class Handles all of the commands that the bot can run not incluting custom commands.
 */


@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class MessageHandler {

    private FileHandler handler = new FileHandler();

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(String command, String args, CommandObject commandObject) {
        if (Globals.isModifingFiles) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (commandObject.message.getAuthor().isBot()) {
            return;
        }
        if (command.toLowerCase().startsWith(commandObject.guildConfig.getPrefixCommand().toLowerCase())) {
            handleCommand(commandObject, command, args);
        }
    }

    //Command Handler
    private void handleCommand(CommandObject commandObject, String command, String args) {
        IChannel channel = commandObject.channel;
        GuildConfig guildConfig = commandObject.guildConfig;
        ArrayList<Command> commands = commandObject.commands;
        IDiscordClient client = commandObject.client;
        for (Command c : commands) {
            for (String name : c.names()) {
                if (command.equalsIgnoreCase(guildConfig.getPrefixCommand() + name)) {

                    //hides creator commands from anyone but the bot owner.
                    if (c.type().equals(Command.TYPE_CREATOR) && !commandObject.authorID.equals(Globals.creatorID)){
                        return ;
                    }
                    //command logging
                    logger.debug(Utility.loggingFormatter("COMMAND",command,args,commandObject));

                    if (c.requiresArgs() && args.isEmpty()) {
                        Utility.sendMessage(Utility.getCommandInfo(c, commandObject), channel);
                        return;
                    }
                    if (c.channel() != null && !Utility.canBypass(commandObject.author, commandObject.guild)) {
                        IChannel correctChannel = client.getChannelByID(guildConfig.getChannelTypeID(c.channel()));
                        if (correctChannel != null) {
                            if (!channel.getID().equals(guildConfig.getChannelTypeID(c.channel()))) {
                                Utility.sendMessage("> Command must be performed in: " + correctChannel.mention(), channel);
                                return;
                            }
                        }
                    }
                    if (c.perms().length != 0 && !Utility.canBypass(commandObject.author, commandObject.guild)) {
                        if (!Utility.testForPerms(c.perms(), commandObject.author, commandObject.guild)) {
                            Utility.sendMessage(commandObject.notAllowed, channel);
                            return;
                        }
                    }
                    Utility.sendMessage(c.execute(args, commandObject), channel);
                }
            }
        }
    }

}