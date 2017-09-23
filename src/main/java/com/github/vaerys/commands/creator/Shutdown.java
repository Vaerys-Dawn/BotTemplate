package com.github.vaerys.commands.creator;

import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

public class Shutdown implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        command.withResponse("Shutting Down.").send();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(2);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description() {
        return "Shuts the bot down";
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
