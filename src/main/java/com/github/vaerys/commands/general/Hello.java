package com.github.vaerys.commands.general;

import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

public class Hello implements Command {
    @Override
    public void execute(String args, CommandObject command) {
        command.withResponse("Hello " + command.user.displayName + ".").buildMessage();
    }

    @Override
    public String[] names() {
        return new String[]{"Hello"};
    }

    @Override
    public String description() {
        return "Says Hello";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public int minArgs() {
        return 0;
    }
}
