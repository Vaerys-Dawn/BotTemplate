package com.github.vaerys.commands.creator;

import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;
import com.github.vaerys.utils.Utility;
import org.apache.commons.io.FilenameUtils;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.Image;

public class SetAvatar implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (Utility.isImageLink(args)) {
            String fileType = FilenameUtils.getExtension(args);
            Image avatar = Image.forUrl(fileType, args);
            Utility.updateAvatar(avatar);
            return "Avatar updated.";
        } else {
            return "ERROR: Invalid Url.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetAvatar"};
    }

    @Override
    public String description() {
        return "Sets the bot's avatar.";
    }

    @Override
    public String usage() {
        return "[URL]";
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public int minArgs() {
        return 1;
    }
}
