package com.github.vaerys.utils;

import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;

import java.util.List;

public class MessageHandler {
    public MessageHandler(String content, CommandObject object, boolean isPrivate, List<Command> commands) {
        try {
            if (isPrivate) return;
            for (Command command : commands) {
                if (command.isCall(content, object)) {
                    if (!command.enoughArgs(content, object)) {
                        object.withResponse("Missing arguments.\n" + command.missingArgs(object)).send();
                        return;
                    }
                    String response = command.execute(command.getArgs(content, object), object);
                    object.withResponse(response).send();
                    return;
                } else {
                    object.withResponse("You do not have permission to use that command.").send();
                    return;
                }
            }
        } catch (Exception e) {
            String errorPos = "";
            for (StackTraceElement s : e.getStackTrace()) {
                if (s.toString().contains("com.github.vaerys")) {
                    errorPos = s.toString();
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append("> I caught an Error, Please send this Error message and the message that caused this error " +
                    "to my **Direct Messages** so my developer can look at it and try to solve the issue.\n```\n");
            builder.append(e.getClass().getName());
            builder.append(": " + e.getMessage());
            if (!errorPos.isEmpty()) {
                builder.append("\n" + Command.indent + "at " + errorPos);
            }
            builder.append("```");
            object.withResponse(builder.toString()).send();
            Utility.sendStack(e);
            return;
        }
    }
}
