package Handlers;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Annotations.ToggleAnnotation;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.Image;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * This Class Handles all of the commands that the bot can run not incluting custom commands.
 */

/*
 * Annotation order:
 *
 * @AliasAnnotation(alias)
 * @CommandAnnotation(
 * name, description, usage,
 * type, channel, permissions, requiresArgs, doGeneralLogging, doResponseGeneral)
 */

@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class MessageHandler {

    private IMessage message;
    private IGuild guild;
    private IChannel channel;
    private IUser author;
    private String guildID;
    private String command;
    private String args;
    private String noAllowed;

    private GuildConfig guildConfig = new GuildConfig();

    private FileHandler handler = new FileHandler();

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(String command, String args, IMessage message) {
        this.command = command;
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        noAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
        guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        if (author.isBot()) {
            return;
        }
        if (command.toLowerCase().startsWith(guildConfig.getPrefixCommand().toLowerCase())) {
            handleCommand();
        }
    }


    //File handlers

    private void flushFiles() {
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
    }

    /**
     * used to handle the sending of logging to the logging channels if they exist.
     */
    private void handleLogging(IChannel loggingChannel, CommandAnnotation commandAnno) {
        StringBuilder builder = new StringBuilder();
        builder.append("> **" + author.getDisplayName(guild) + "** Has Used Command `" + command + "`");
        if (!commandAnno.usage().equals("") && !args.equals("")) {
            builder.append(" with args: `" + args + "`");
        }
        builder.append(" in channel " + channel.mention() + " .");
        Utility.sendMessage(builder.toString(), loggingChannel);
    }

    //Command Handler
    private void handleCommand() {
        boolean channelCorrect;
        boolean permsCorrect;

        Method[] methods = this.getClass().getMethods();

        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class)) {
                CommandAnnotation commandAnno = m.getAnnotation(CommandAnnotation.class);
                List<String> aliases = new ArrayList<>();
                if (m.isAnnotationPresent(AliasAnnotation.class)) {
                    AliasAnnotation aliasAnno = m.getAnnotation(AliasAnnotation.class);
                    aliases = new ArrayList<>(Arrays.asList(aliasAnno.alias()));
                }
                aliases.add(commandAnno.name());
                for (String s : aliases) {
                    if ((guildConfig.getPrefixCommand() + s).equalsIgnoreCase(command)) {

                        //test for args required
                        if (commandAnno.requiresArgs() && args.equals("")) {
                            Utility.sendMessage("Command is missing Arguments: \n" + Utility.getCommandInfo(commandAnno, guildConfig), channel);
                            return;
                        }

                        //Logging Handling
                        if (guildConfig.doAdminLogging()) {
                            IChannel loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_ADMIN_LOG));
                            if (commandAnno.doAdminLogging() && loggingChannel != null) {
                                handleLogging(loggingChannel, commandAnno);
                            }
                        }
                        if (guildConfig.doGeneralLogging()) {
                            IChannel loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_SERVER_LOG));
                            if (!commandAnno.doAdminLogging() && loggingChannel != null) {
                                handleLogging(loggingChannel, commandAnno);
                            }
                        }

                        //Channel Validation
                        if (!commandAnno.channel().equals(Constants.CHANNEL_ANY)) {
                            if (guildConfig.getChannelTypeID(commandAnno.channel()) == null || channel.getID().equals(guildConfig.getChannelTypeID(commandAnno.channel()))) {
                                channelCorrect = true;
                            } else channelCorrect = false;
                        } else channelCorrect = true;

                        //Owner bypass
                        if (guild.getOwner().equals(author) || author.getID().equals(Globals.creatorID)) {
                            channelCorrect = true;
                        }

                        //Permission Validation
                        if (channelCorrect) {
                            if (!Arrays.equals(commandAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                                permsCorrect = Utility.testForPerms(commandAnno.perms(), author, guild);
                            } else permsCorrect = true;

                            //Owner bypass
                            if (guild.getOwner().equals(author) || author.getID().equals(Globals.creatorID)) {
                                permsCorrect = true;
                            }

                            //message sending
                            if (permsCorrect) {
                                try {
                                    if (commandAnno.doResponseGeneral()) {
                                        channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
                                    }
                                    Utility.sendMessage((String) m.invoke(this), channel);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            } else
                                Utility.sendMessage(noAllowed, channel);
                        } else {
                            //if the current channel is wrong for this.
                            Utility.sendMessage("> Command must be performed in the " + guild.getChannelByID(guildConfig.getChannelTypeID(commandAnno.channel())).mention() + " channel.", channel);
                        }
                        // SHOULD ALWAYS BE RUN AT ALL COST. THIS PART SAVES THE DATA.
                        flushFiles();
                    }
                }
            }
        }
    }

    //---------Beginning of commands-------------

    //
    //
    //
    //
    //
    //Help commands
    @CommandAnnotation(
            name = "Help", description = "Gives information about Sail, including the commands it can run.", usage = "[Command Type]",
            type = Constants.TYPE_HELP)
    public String help() {
        Method[] methods = this.getClass().getMethods();
        ArrayList<String> types = new ArrayList<>();
        EmbedBuilder helpEmbed = new EmbedBuilder();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> commands = new ArrayList<>();
        String spacer = Globals.tagSpacer("#spacer#");

        //setting embed colour to match Bot's Colour
        Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), guild);
        if (color != null) {
            helpEmbed.withColor(color);
        }

        //getting Types of commands.
        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class)) {
                boolean typeFound = false;
                CommandAnnotation anno = m.getAnnotation(CommandAnnotation.class);
                for (String s : types) {
                    if (s.equalsIgnoreCase(anno.type())) {
                        typeFound = true;
                    }
                }
                if (!typeFound) {
                    types.add(anno.type());
                }
            }
        }
        //sort types
        Collections.sort(types);

        //building the embed
        if (args.equals("")) {
            builder.append("```\n");
            for (String s : types) {
                builder.append(s + "\n");
            }
            builder.append("```\n");
            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(">>**`" + spacer + Utility.getCommandInfo("help", guildConfig) + spacer + "`**<<\n");
            builder.append("Support Discord - https://discord.gg/XSyQQrR\n");
            builder.append("[Bot's GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)");
            helpEmbed.withDescription(builder.toString());
            helpEmbed.withFooterText("Bot Version: " + Globals.version);
        } else {
            boolean isFound = false;
            for (String s : types) {
                if (args.equalsIgnoreCase(s)) {
                    isFound = true;
                    helpEmbed.withTitle("> Here are all of the " + s + " Commands I have available.");
                    for (Method m : methods) {
                        if (m.isAnnotationPresent(CommandAnnotation.class)) {
                            CommandAnnotation anno = m.getAnnotation(CommandAnnotation.class);
                            if (anno.type().equalsIgnoreCase(s)) {
                                commands.add(guildConfig.getPrefixCommand() + anno.name() + "\n");
                            }
                        }
                    }
                    Collections.sort(commands);
                    builder.append("```\n");
                    commands.forEach(builder::append);
                    builder.append("```\n");
                    builder.append(">>**`" + spacer + Utility.getCommandInfo("info", guildConfig) + spacer + "`**<<");
                    helpEmbed.withDescription(builder.toString());
                }
            }
            if (!isFound) {
                return "> There are no commands with the type: " + args + ".\n" + Constants.PREFIX_INDENT + Utility.getCommandInfo("help", guildConfig);
            }
        }
        Utility.sendEmbededMessage("", helpEmbed.build(), channel, true);
        return "";
    }

    @CommandAnnotation(name = "Info", description = "Gives information about a command.", usage = "[Command Name]",
            type = Constants.TYPE_HELP, requiresArgs = true)
    public String info() {
        Method[] methods = this.getClass().getMethods();
        StringBuilder commandAliases = new StringBuilder();
        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class)) {
                ArrayList<String> aliases = new ArrayList<>();
                CommandAnnotation cAnno = m.getAnnotation(CommandAnnotation.class);
                aliases.add(cAnno.name());
                if (m.isAnnotationPresent(AliasAnnotation.class)) {
                    AliasAnnotation aAnno = m.getAnnotation(AliasAnnotation.class);
                    Collections.addAll(aliases, aAnno.alias());
                }
                for (String s : aliases) {
                    if (args.equalsIgnoreCase(s)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("> **" + guildConfig.getPrefixCommand() + cAnno.name() + " " + cAnno.usage() + "**\n");
                        builder.append(Constants.PREFIX_INDENT + cAnno.description() + "\n");
                        builder.append(Constants.PREFIX_INDENT + "Type: " + cAnno.type() + "\n");
                        if (!cAnno.channel().equals(Constants.CHANNEL_ANY) && guildConfig.getChannelTypeID(cAnno.channel()) != null) {
                            builder.append(Constants.PREFIX_INDENT + "Channel: " + guild.getChannelByID(guildConfig.getChannelTypeID(cAnno.channel())).mention() + ".\n");
                        }
                        if (!Arrays.equals(cAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                            builder.append(Constants.PREFIX_INDENT + "Permissions: ");
                            for (Permissions p : cAnno.perms()) {
                                builder.append(p.name() + ", ");
                            }
                            builder.delete(builder.length() - 2, builder.length());
                            builder.append(".\n");
                        }
                        if (m.isAnnotationPresent(AliasAnnotation.class)) {
                            builder.append(Constants.PREFIX_INDENT + "Aliases: ");
                            for (String alias : aliases) {
                                builder.append(alias + ", ");
                            }
                            builder.delete(builder.length() - 2, builder.length());
                            builder.append(".\n");
                        }
                        return builder.toString();
                    }
                }
            }
        }
        return "> Command with the name " + args + " not found.";
    }

    @CommandAnnotation(
            name = "GetGuildInfo", description = "Sends Information about the server to your Direct Messages.",
            type = Constants.TYPE_HELP)
    public String getGuildInfo() {
        return guildConfig.getInfo(guild, author);
    }

    //
    //
    //
    //
    //
    //General commands
    @CommandAnnotation(
            name = "Hello", description = "Says Hello.",
            type = Constants.TYPE_GENERAL)
    public String hello() {
        return "> Hello " + author.getDisplayName(guild) + ".";
    }

    //
    //
    //
    //
    //
    //admin commands
    @CommandAnnotation(
            name = "Toggle", description = "Toggles Certain Parts of the Guild Config", usage = "[Toggle Type]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER}, doAdminLogging = true)
    public String toggles() {
        if (args.equals("")) {
            StringBuilder builder = new StringBuilder();
            Method[] methods = GuildConfig.class.getMethods();
            builder.append("> Here are the Types of toggles you have at your disposal:\n");
            ArrayList<String> types = new ArrayList<>();
            for (Method m : methods) {
                if (m.isAnnotationPresent(ToggleAnnotation.class)) {
                    ToggleAnnotation toggleAnno = m.getAnnotation(ToggleAnnotation.class);
                    types.add(toggleAnno.name());
                }
            }
            Collections.sort(types);
            for (String s : types) {
                builder.append(Constants.PREFIX_INDENT + s + "\n");
            }
            builder.append("> You can Toggle those types by using the command\n");
            builder.append(Constants.PREFIX_INDENT + Utility.getCommandInfo("toggles", guildConfig));
            return builder.toString();
        } else {
            Method[] methods = GuildConfig.class.getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(ToggleAnnotation.class)) {
                    ToggleAnnotation toggleAnno = m.getAnnotation(ToggleAnnotation.class);
                    if (args.equalsIgnoreCase(toggleAnno.name())) {
                        try {
                            m.invoke(guildConfig);
                            return "> Toggled **" + toggleAnno.name() + "**.";
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return "> You cannot toggle " + args + " as that is not a valid toggle.\n" + Constants.PREFIX_INDENT +
                    "A list of toggles you can use can be found by performing the following command:\n" + Constants.PREFIX_INDENT +
                    Utility.getCommandInfo("toggleTypes", guildConfig);
        }
    }

    @AliasAnnotation(alias = {"SetupChannel"})
    @CommandAnnotation(
            name = "ChannelHere", description = "Sets the current channel as the channel type you select.", usage = "[Channel Type]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_CHANNELS}, requiresArgs = true, doAdminLogging = true)
    public String channelHere() {
        if (args.equals("")) {
            StringBuilder builder = new StringBuilder();
            builder.append("> Here are the channel types you can set up.\n`");
            try {
                for (Field f : Constants.class.getDeclaredFields()) {
                    if (f.getName().contains("CHANNEL_") && f.getType() == String.class && !f.get(null).equals(Constants.CHANNEL_ANY)) {
                        builder.append(f.get(null) + ", ");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            builder.delete(builder.length() - 2, builder.length());
            builder.append("`.");
            return builder.toString();
        } else {
            try {
                for (Field f : Constants.class.getDeclaredFields()) {
                    if (f.getName().contains("CHANNEL_") && f.getType() == String.class && !f.get(null).equals(Constants.CHANNEL_ANY)) {
                        try {
                            if (args.equalsIgnoreCase((String) f.get(null))) {
                                guildConfig.setUpChannel((String) f.get(null), channel.getID());
                                return "> This channel is now the Server's **" + f.get(null) + "** channel.";
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return "> Channel type with that name not found, you can see the channel types you can choose from " +
                    "by running the command\n" + Utility.getCommandInfo("channelTypes", guildConfig);
        }
    }

    @CommandAnnotation(name = "Shutdown", description = "Shuts the bot down safely.",
            type = Constants.TYPE_ADMIN, doAdminLogging = true)
    public String logoff() {
        if (author.getID().equals(Globals.creatorID)) {
            Utility.sendMessage("> Shutting Down.", channel);
            try {
                Thread.sleep(4000);
                Globals.getClient().logout();
                Runtime.getRuntime().exit(0);
            } catch (DiscordException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return noAllowed;
        }
    }

    @CommandAnnotation(name = "UpdateAvatar", description = "Shuts the bot down safely.",
            type = Constants.TYPE_ADMIN, doAdminLogging = true)
    public String updateAvatar() {
        if (author.getID().equals(Globals.creatorID)) {
            File avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.avatarFile);
            if (avatarFile.exists()) {
                final Image avatar = Image.forFile(avatarFile);
                Utility.updateAvatar(avatar);
                return "> Avatar updated.";
            } else {
                return "> Failed to update avatar, Image path invalid.";
            }
        } else {
            return noAllowed;
        }
    }
}