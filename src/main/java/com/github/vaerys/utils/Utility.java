package com.github.vaerys.utils;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.setup.Client;
import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;
import com.github.vaerys.objects.discord.UserObject;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.lang.Enum;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    //Logger
    final static Logger logger = LoggerFactory.getLogger(Utility.class);

    //Discord Utils
    public static IRole getRoleFromName(String roleName, IGuild guild) {
        IRole role = null;
        for (IRole r : guild.getRoles()) {
            if (r.getName().equalsIgnoreCase(roleName)) {
                role = r;
            }
        }
        return role;
    }

    public static boolean testForPerms(Permissions[] perms, IUser user, IGuild guild) {
        if (perms.length == 0) {
            return true;
        }
        if (guild == null) {
            return true;
        }
        if (user.getLongID() == Globals.creatorID) {
            return true;
        }
        if (user.getLongID() == guild.getOwnerLongID()) {
            return true;
        }
        if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            return true;
        }
        EnumSet<Permissions> toMatch = EnumSet.noneOf(Permissions.class);
        toMatch.addAll(Arrays.asList(perms));
        //Debug code.
        List<String> toMatchList = new ArrayList<String>() {{
            addAll(toMatch.stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        List<String> userList = new ArrayList<String>() {{
            addAll(user.getPermissionsForGuild(guild).stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        //end Debug
        return user.getPermissionsForGuild(guild).containsAll(toMatch);
    }

    //File Utils
    public static String getFilePath(long guildID, String type) {
        return Constants.DIR_STORAGE + guildID + "/" + type;
    }

    public static String getFilePath(long guildID, String type, boolean isBackup) {
        return Constants.DIR_BACKUPS + guildID + "/" + type;
    }

    public static String getDirectory(long guildID) {
        return Constants.DIR_STORAGE + guildID + "/";
    }

    public static String getDirectory(long guildID, boolean isBackup) {
        return Constants.DIR_BACKUPS + guildID + "/";
    }

    public static void updateAvatar(Image avatar) {
        RequestBuffer.request(() -> Client.client.changeAvatar(avatar));
    }

    public static void updateUsername(String botName) {
        RequestBuffer.request(() -> Client.client.changeUsername(botName));
    }

    public static Color getUsersColour(IUser user, IGuild guild) {
        //before
        List<IRole> userRoles = guild.getRolesForUser(user);
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return null;
    }

    public static Color getUsersColour(List<IRole> userRoles, IGuild guild) {
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return Color.black;
    }

    //Time Utils
    public static String formatTimeSeconds(long timeMillis) {
        long second = (timeMillis) % 60;
        long minute = (timeMillis / 60) % 60;
        long hour = (timeMillis / (60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    public static Boolean testModifier(String modifier) {
        switch (modifier.toLowerCase()) {
            case "+":
                return true;
            case "-":
                return false;
            case "add":
                return true;
            case "del":
                return false;
            default:
                return null;
        }
    }

    public static boolean canBypass(IUser author, IGuild guild, boolean logging) {
        if (author.getLongID() == Globals.creatorID) {
            if (logging) {
                logger.trace("User is Creator, BYPASSING.");
            }
            return true;
        }
        if (guild == null) {
            return false;
        }
        if (author.getLongID() == guild.getOwnerLongID()) {
            if (logging) {
                logger.trace("User is Guild Owner, GUILD : \"" + guild.getLongID() + "\", BYPASSING.");
            }
            return true;
        }
        if (author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            return true;
        }
        return false;
    }

    public static boolean canBypass(IUser author, IGuild guild) {
        return canBypass(author, guild, true);
    }

    public static long getMentionUserID(String content) {
        if (content.contains("<@")) {
            long userID = stringLong(StringUtils.substringBetween(content, "<@!", ">"));
            if (userID == -1) {
                userID = stringLong(StringUtils.substringBetween(content, "<@", ">"));
            }
            IUser user = Client.client.getUserByID(userID);
            if (user != null) {
                return userID;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static String convertMentionToText(String from) {
        String last;
        if (from == null || from.isEmpty()) {
            return from;
        }
        do {
            last = from;
            if (from.contains("<@") || from.contains("<!@")) {
                long userID = getMentionUserID(from);
                if (userID != -1) {
                    IUser mentioned = Client.client.getUserByID(userID);
                    from = from.replace("<@!" + userID + ">", mentioned.getName() + "#" + mentioned.getDiscriminator());
                    from = from.replace("<@" + userID + ">", mentioned.getName() + "#" + mentioned.getDiscriminator());
                }
            }
        } while (!last.equals(from));
        return from;
    }

    public static void listFormatterEmbed(String title, EmbedBuilder builder, List<String> list,
                                          boolean horizontal) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.spacer;
        }
        if (formattedList.isEmpty()) {
            builder.appendField(title, Command.spacer, false);
            return;
        }
        if (horizontal) {
            builder.appendField(title, "`" + formattedList + "`", false);
        } else {
            builder.appendField(title, "```\n" + formattedList + "```", false);
        }
    }

    public static void listFormatterEmbed(String title, EmbedBuilder builder, List<String> list,
                                          boolean horizontal, String suffix) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.spacer;
        }
        if (formattedList.isEmpty()) {
            builder.appendField(title, Command.spacer + suffix, false);
            return;
        }
        if (horizontal) {
            builder.appendField(title, "`" + formattedList + "`\n" + suffix, false);
        } else {
            builder.appendField(title, "```\n" + formattedList + "```\n" + suffix, false);
        }
    }

    public static String listFormatter(List<String> list, boolean horizontal) {
        StringBuilder formattedList = new StringBuilder();
        if (list.size() == 0) {
            return "";
        }
        if (horizontal) {
            for (String s : list) {
                formattedList.append(s + ", ");
            }
            formattedList.delete(formattedList.length() - 2, formattedList.length());
            formattedList.append(".");
            return formattedList.toString();
        } else {
            for (String s : list) {
                formattedList.append(s + "\n");
            }
            return formattedList.toString();
        }
    }

    public static List<IRole> getRolesByName(IGuild guild, String name) {
        List<IRole> roles = guild.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        return roles;
    }

    public static String formatTimeDifference(long difference) {
        String formatted = "";
        try {
            long days = TimeUnit.SECONDS.toDays(difference);
            long hours = TimeUnit.SECONDS.toHours(difference);
            hours -= days * 24;
            long mins = TimeUnit.SECONDS.toMinutes(difference);
            mins -= (days * 24 + hours) * 60;

            if (days > 0) {
                if (days > 1) {
                    formatted = formatted + days + " days, ";
                } else {
                    formatted = formatted + days + " day, ";
                }
            }
            if (hours > 0) {
                if (hours > 1) {
                    formatted = formatted + hours + " hours and ";
                } else {
                    formatted = formatted + hours + " hour and ";
                }
            }
            if (mins > 1) {
                formatted = formatted + mins + " minutes ago";
            } else if (mins != 0) {
                formatted = formatted + mins + " minute ago";
            }
            if (difference < 60) {
                formatted = "less than a minute ago";
            }
        } catch (NoSuchElementException e) {
            logger.error("Error getting Edited Message Timestamp.");
        }
        return formatted;
    }

    public static String removeFun(String from) {
        String last;
        boolean exit;
        do {
            last = from;
            exit = false;
            if (from.contains("***")) {
                from = replaceFun(from, "***");
                exit = true;
            }
            if (from.contains("**") && !exit) {
                from = replaceFun(from, "**");
                exit = true;
            }
            if (from.contains("*") && !exit) {
                from = replaceFun(from, "*");
            }
            exit = false;
            if (from.contains("```")) {
                from = replaceFun(from, "```");
                exit = true;
            }
            if (from.contains("`") && !exit) {
                from = replaceFun(from, "`");
            }
            exit = false;
            if (from.contains("~~")) {
                from = replaceFun(from, "~~");
            }
            if (from.contains("__")) {
                from = replaceFun(from, "__");
                exit = true;
            }
            if (from.contains("_") && !exit) {
                from = replaceFun(from, "_");
            }
        } while (last != from);
        return from;
    }

    public static String replaceFun(String from, String fun) {
        String noFun = StringUtils.substringBetween(from, fun, fun);
        if (noFun != null) {
            from = from.replace(fun + noFun + fun, noFun);
        }
        return from;
    }

    public static boolean isImageLink(String link) {
        List<String> suffixes = new ArrayList<String>() {{
            add(".png");
            add(".gif");
            add(".jpg");
            add(".webp");
        }};
        if (link.contains("\n") || link.contains(" ")) {
            return false;
        }
        for (String s : suffixes) {
            if (link.toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean testUserHierarchy(IUser higherUser, IUser lowerUser, IGuild guild) {
        List<IRole> lowerRoles = lowerUser.getRolesForGuild(guild);
        List<IRole> higherRoles = higherUser.getRolesForGuild(guild);
        IRole topRole = null;
        int topRolePos = 0;
        for (IRole role : higherRoles) {
            if (topRole == null) {
                topRole = role;
                topRolePos = role.getPosition();
            } else {
                if (role.getPosition() > topRolePos) {
                    topRole = role;
                    topRolePos = role.getPosition();
                }
            }
        }
        for (IRole role : lowerRoles) {
            if (role.getPosition() > topRolePos) {
                return false;
            }
        }
        return true;
    }

    public static boolean testUserHierarchy(IUser author, IRole toTest, IGuild guild) {
        boolean roleIsLower = false;
        for (IRole r : author.getRolesForGuild(guild)) {
            if (toTest.getPosition() < r.getPosition()) {
                roleIsLower = true;
            }
        }
        return roleIsLower;
    }

    public static long textToSeconds(String time) {
        try {
            String sub = time.substring(0, time.length() - 1);
            long timeSecs = Long.parseLong(sub);
            if (time.toLowerCase().endsWith("s")) {
                return timeSecs;
            } else if (time.toLowerCase().endsWith("m")) {
                return timeSecs * 60;
            } else if (time.toLowerCase().endsWith("h")) {
                return timeSecs * 60 * 60;
            } else if (time.toLowerCase().endsWith("d")) {
                return timeSecs * 60 * 60 * 24;
            } else {
                timeSecs = Long.parseLong(time);
                return timeSecs;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String unFormatMentions(IMessage message) {
        String from = message.getContent();
        for (IUser user : message.getMentions()) {
            if (user == null) {
                break;
            }
            String mention = "<@" + user.getLongID() + ">";
            String mentionNic = "<@!" + user.getLongID() + ">";
            from = from.replace(mention, "__@" + user.getDisplayName(message.getGuild()) + "__");
            from = from.replace(mentionNic, "__@" + user.getDisplayName(message.getGuild()) + "__");
        }
        for (IRole role : message.getRoleMentions()) {
            String roleMention = "<@&" + role.getLongID() + ">";
            from = from.replace(roleMention, "__**@" + role.getName() + "**__");
        }
        return from;
    }

    public static String formatTimestamp(ZonedDateTime time) {
        StringBuilder content = new StringBuilder();
        content.append(time.getYear());
        content.append("/" + time.getMonthValue());
        content.append("/" + time.getDayOfMonth());
        content.append(" - " + time.getHour());
        content.append(":" + time.getMinute());
        content.append(":" + time.getSecond());
        return content.toString();
    }

    public static List<String> getChannelMentions(ArrayList<Long> channelIDs, CommandObject command) {
        List<String> channelNames = new ArrayList<>();
        if (channelIDs != null) {
            for (long s : channelIDs) {
                IChannel channel = command.guild.get().getChannelByID(s);
                if (channel != null) {
                    channelNames.add(channel.mention());
                }
            }
        }
        return channelNames;
    }

    public static void sendStack(Exception e) {
        String s = ExceptionUtils.getStackTrace(e);
        s = s.substring(0, s.length() - 2);
        if (!s.endsWith(")")) {
            s = s + ")";
        }

        logger.error(s);
    }

    public static List<IChannel> getVisibleChannels(List<IChannel> channels, UserObject user) {
        List<IChannel> newSet = new ArrayList<>();
        for (IChannel c : channels) {
            if (c.getModifiedPermissions(user.get()).contains(Permissions.READ_MESSAGES)
                    && c.getModifiedPermissions(user.get()).contains(Permissions.SEND_MESSAGES)) {
                newSet.add(c);
            }
        }
        return newSet;
    }

    public static List<String> getChannelMentions(List<IChannel> channels) {
        List<String> mentions = new ArrayList<>();
        for (IChannel c : channels) {
            mentions.add(c.mention());
        }
        return mentions;
    }

    public static UserObject getUser(CommandObject command, String args, boolean doContains) {
        if (args != null && !args.isEmpty()) {
            IUser user = null;
            IUser conUser = null;
            String toTest;
            if (args.split(" ").length != 1) {
                toTest = escapeRegex(args);
            } else {
                toTest = escapeRegex(args).replace("_", "[_| ]");
            }
            for (IUser u : command.guild.get().getUsers()) {
                if (user != null) {
                    break;
                }
                try {
                    if ((u.getName() + "#" + u.getDiscriminator()).matches("(?i)" + toTest)) {
                        user = u;
                    }
                    if (u.getName().matches("(?i)" + toTest) && user == null) {
                        user = u;
                    }
                    String displayName = u.getDisplayName(command.guild.get());
                    if (displayName.matches("(?i)" + toTest) && user == null) {
                        user = u;
                    }
                    if (doContains && conUser == null) {
                        if (u.getName().matches("(?i).*" + toTest + ".*")) {
                            conUser = u;
                        }
                        if (displayName.matches("(?i).*" + toTest + ".*") && conUser == null) {
                            conUser = u;
                        }
                    }
                } catch (PatternSyntaxException e) {
                    //continue.
                }
            }
            try {
                long uID = Long.parseLong(args);
                user = command.client.get().getUserByID(uID);
            } catch (NumberFormatException e) {
                if (command.message.get().getMentions().size() > 0) {
                    user = command.message.get().getMentions().get(0);
                }
            }
            if (user == null && doContains) {
                user = conUser;
            }
            if (user != null) {
                UserObject userObject = new UserObject(user, command.guild);
                return userObject;
            }
        }
        return null;
    }

    private static String escapeRegex(String args) {
        //[\^$.|?*+(){}
        args = args.replace("\\", "\\u005C");
        args = args.replace("[", "\\u005B");
        args = args.replace("^", "\\u005E");
        args = args.replace("$", "\\u0024");
        args = args.replace(".", "\\u002E");
        args = args.replace("|", "\\u007C");
        args = args.replace("?", "\\u003F");
        args = args.replace("*", "\\u002A");
        args = args.replace("+", "\\u002B");
        args = args.replace("(", "\\u0028");
        args = args.replace(")", "\\u0029");
        args = args.replace("{", "\\u007B");
        args = args.replace("}", "\\u007D");
        return args;
    }

    public static long stringLong(String string) {
        try {
            return Long.parseUnsignedLong(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    public static ReactionEmoji getReaction(String emojiName) {
        Emoji emoji = EmojiManager.getForAlias(emojiName);
        if (emoji == null) {
            throw new IllegalStateException("Invalid unicode call: " + emojiName);
        }
        return ReactionEmoji.of(emoji.getUnicode());
    }
}