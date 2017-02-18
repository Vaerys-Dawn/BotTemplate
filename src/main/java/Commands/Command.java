package Commands;

import sx.blah.discord.handle.obj.Permissions;


/**
 * Created by Vaerys on 29/01/2017.
 */
public interface Command {

    //Type Constants
    String TYPE_GENERAL = "General";
    String TYPE_ADMIN = "Admin";
    String TYPE_HELP = "Help";
    String TYPE_CREATOR = "Creator";

    //Channel Constants
    String CHANNEL_GENERAL = "General";
    String CHANNEL_BOT_COMMANDS = "BotCommands";

    String spacer = "\u200B";
    String indent = "    ";
    String codeBlock = "```";
    String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";

    String execute(String args, CommandObject command);

    //descriptors
    String[] names();
    String description();
    String usage();
    String type();
    String channel();
    Permissions[] perms();
    boolean requiresArgs();
}
