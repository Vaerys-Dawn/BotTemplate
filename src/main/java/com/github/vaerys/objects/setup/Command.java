package com.github.vaerys.objects.setup;

import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.utils.Utility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by Vaerys on 29/01/2017.
 */
public interface Command {
    //Type Constants

    String spacer = "\u200B";
    String indent = "    ";
    String codeBlock = "```";
    String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";

    Logger logger = LoggerFactory.getLogger(Command.class);

    String execute(String args, CommandObject command);

    //descriptors
    String[] names();

    String description();

    String usage();

    Permissions[] perms();

    int minArgs();

    default String getCommand(CommandObject command) {
        return command.guild.data.getPrefixCommand() + names()[0];
    }

    default String getUsage(CommandObject command) {
        if (usage() == null || usage().isEmpty()) {
            return getCommand(command);
        } else {
            return getCommand(command) + " " + usage();
        }
    }

    default String missingArgs(CommandObject command) {
        return ">> **" + getUsage(command) + "** <<";
    }

    default boolean isCall(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        for (String s : names()) {
            if ((command.guild.data.getPrefixCommand() + s).equalsIgnoreCase(call.getFirstWord())) {
                return true;
            }
        }
        return false;
    }

    default String getArgs(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }

    default EmbedBuilder getCommandInfo(CommandObject command) {
        EmbedBuilder infoEmbed = new EmbedBuilder();
        Color color = command.client.color;
        if (color != null) {
            infoEmbed.withColor(color);
        }

        //command info
        StringBuilder builder = new StringBuilder();
        builder.append("**" + getUsage(command) + "**\n");
        builder.append("**Desc: **" + description() + "\n");
        if (perms().length != 0) {
            builder.append("**Perms: **");
            ArrayList<String> permList = new ArrayList<>();
            for (Permissions p : perms()) {
                permList.add(p.toString());
            }
            builder.append(Utility.listFormatter(permList, true));
        }
        infoEmbed.appendField("> Info - " + names()[0], builder.toString(), false);

        //aliases
        if (names().length > 1) {
            StringBuilder aliasBuilder = new StringBuilder();
            for (int i = 1; i < names().length; i++) {
                aliasBuilder.append(command.guild.data.getPrefixCommand() + names()[i] + ", ");
            }
            aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
            aliasBuilder.append(".\n");
            infoEmbed.appendField("Aliases", aliasBuilder.toString(), false);
        }
        return infoEmbed;
    }

    default String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append(this.getClass().getName() + "\n");
        if (names().length == 0 || names()[0].isEmpty()) {
            response.append("> NAME IS EMPTY.\n");
            isErrored = true;
        }
        if (description() == null || description().isEmpty()) {
            response.append("> DESCRIPTION IS EMPTY.\n");
            isErrored = true;
        }
        if (minArgs() != 0 && (usage() == null || usage().isEmpty())) {
            response.append("> USAGE IS NULL WHEN REQUIRES_ARGS IS TRUE.\n");
            isErrored = true;
        }
        response.replace(response.length() - 1, response.length(), "");
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }

    default boolean enoughArgs(String args, CommandObject object) {
        if (minArgs() == 0) {
            return true;
        }
        int length = StringUtils.countMatches(args, " ");
        return minArgs() <= length;
    }
}
