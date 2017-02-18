package Commands.Creator;

import Commands.Command;
import Commands.CommandObject;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.DailyObject;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.Image;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateAvatar implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        if (Globals.doDailyAvatars == true) {
            for (DailyObject d : Globals.getDailyObjects()) {
                if (d.getDayOfWeek().equals(nowUTC.getDayOfWeek())) {
                    Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName()));
                    Utility.updateAvatar(avatar);
                }
            }
        } else {
            Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile));
            Utility.updateAvatar(avatar);
        }
        return "> Avatar Updated.";
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateAvatar"};
    }

    @Override
    public String description() {
        return "Update's the bot's Avatar.\n" + ownerOnly;
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
