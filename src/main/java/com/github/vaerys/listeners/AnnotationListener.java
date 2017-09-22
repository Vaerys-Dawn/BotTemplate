package com.github.vaerys.listeners;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.CommandObject;
import com.github.vaerys.utils.MessageHandler;
import com.github.vaerys.utils.Utility;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.RequestBuffer;

public class AnnotationListener {

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        try {
            if (event.getAuthor().isBot()) {
                return;
            }
            CommandObject command = new CommandObject(event.getMessage());
            //message and command handling
            new MessageHandler(command.message.get().getContent(), command, event.getChannel().isPrivate());
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
            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.toString()));
            Utility.sendStack(e);
            return;
        }
    }
}
