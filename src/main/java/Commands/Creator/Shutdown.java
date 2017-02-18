package Commands.Creator;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Shutdown implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        Utility.sendMessage("> Shutting Down.", command.channel);
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
    }

    @Override
    public String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description() {
        return "Shuts the bot down safely.\n" + ownerOnly;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
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
