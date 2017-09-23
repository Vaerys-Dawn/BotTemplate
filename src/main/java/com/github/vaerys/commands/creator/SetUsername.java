package com.github.vaerys.commands.creator;

import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;
import com.github.vaerys.utils.Utility;
import sx.blah.discord.handle.obj.Permissions;

public class SetUsername implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        Utility.updateUsername(args);
        return "Avatar Updated.";
    }

    @Override
    public String[] names() {
        return new String[]{"SetUsername"};
    }

    @Override
    public String description() {
        return "Sets the bot's username.";
    }

    @Override
    public String usage() {
        return "[Username]";
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
