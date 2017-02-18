package Commands.Help;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Vaerys on 29/01/2017.
 */
public class Info implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<Command> commands = command.commands;

        String error = "> Command with the name " + args + " not found.";
        for (Command c : commands) {
            for (String s : c.names()) {
                if (args.equalsIgnoreCase(s)) {

                    if (c.type().equals(Command.TYPE_CREATOR) && !command.authorID.equals(Globals.creatorID)){
                        return error;
                    }

                    EmbedBuilder infoEmbed = new EmbedBuilder();
                    Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), command.guild);
                    if (color != null) {
                        infoEmbed.withColor(color);
                    }

                    //command info
                    StringBuilder builder = new StringBuilder();
                    String primaryCommand = command.guildConfig.getPrefixCommand() + c.names()[0];
                    if (c.usage() != null) {
                        primaryCommand += " " + c.usage();
                    }
                    builder.append("**" + primaryCommand + "**\n");
                    builder.append("Desc: **" + c.description() + "**\n");
                    builder.append("Type: **" + c.type() + "**\n");
                    if (c.perms().length != 0) {
                        builder.append("Perms: **");
                        ArrayList<String> permList = new ArrayList<>();
                        for (Permissions p : c.perms()) {
                            permList.add(p.toString());
                        }
                        builder.append(Utility.listFormatter(permList, true));
                    }
                    infoEmbed.appendField("> Info - " + c.names()[0], builder.toString(), false);
                    IChannel commandChannel = command.client.getChannelByID(command.guildConfig.getChannelTypeID(c.channel()));
                    //channel
                    if (commandChannel != null) {
                        infoEmbed.appendField("Channel: ", commandChannel.mention(), false);
                    }

                    //aliases
                    if (c.names().length > 1) {
                        StringBuilder aliasBuilder = new StringBuilder();
                        for (int i = 1; i < c.names().length; i++) {
                            aliasBuilder.append(command.guildConfig.getPrefixCommand() + c.names()[i] + ", ");
                        }
                        aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
                        aliasBuilder.append(".\n");
                        infoEmbed.appendField("Aliases:", aliasBuilder.toString(), false);
                    }
                    Utility.sendEmbededMessage("", infoEmbed.build(), command.channel);
                    return "";
                }
            }

        }
        return error;
    }

    @Override
    public String[] names() {
        return new String[]{"Info"};
    }

    @Override
    public String description() {
        return "Gives information about a command.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }
}
