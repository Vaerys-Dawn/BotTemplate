package com.github.vaerys.listeners;


import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.setup.CommandObject;
import com.github.vaerys.utils.MessageHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CreatorHandler {
    @EventSubscriber
    public void onCreatorMessageEvent(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getAuthor().getLongID() != Globals.creatorID) {
            return;
        }
        CommandObject command = new CommandObject(event.getMessage());
        //message and command handling
        new MessageHandler(command.message.get().getContent(), command, event.getChannel().isPrivate(), Globals.getCreatorCommands());
    }
}
