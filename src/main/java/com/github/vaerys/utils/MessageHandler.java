package com.github.vaerys.utils;

import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;

public class MessageHandler {
    public MessageHandler(String content, CommandObject object, boolean isPrivate) {
        if (isPrivate) return;
        for (Command command : Globals.getCommands()) {
            if (command.isCall(content, object)) {
                if (Utility.testForPerms(command.perms(), object.user.get(), object.guild.get())) {
                    if (!command.enoughArgs(content, object)) {
                        object.withResponse("Missing arguments.\n" + command.missingArgs(object)).buildMessage();
                        return;
                    }
                    command.execute(command.getArgs(content, object), object);
                    return;
                } else {
                    object.withResponse("You do not have permission to use that command.").buildMessage();
                    return;
                }
            }
        }
    }
}
