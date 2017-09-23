package com.github.vaerys.objects.setup;

import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.discord.*;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.io.FileNotFoundException;

public class CommandObject {
    public GuildObject guild;
    public UserObject user;
    public MessageObject message;
    public ChannelObject channel;
    public ClientObject client;

    String response = "";
    File file = null;
    EmbedBuilder embed = null;

    public CommandObject(IMessage message) {
        if (message == null) {
            throw new IllegalStateException("Message should never be null.");
        }
        if (message.getGuild() == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildObject(message.getGuild().getLongID());
        }
        this.message = new MessageObject(message, guild);
        this.channel = new ChannelObject(message.getChannel(), guild);
        this.user = new UserObject(message.getAuthor(), guild);
        this.client = new ClientObject(message.getClient(), guild);
    }

    public CommandObject(IMessage message, IGuild guild, IChannel channel, IUser author) {
        if (guild == null) {
            this.guild = new GuildObject();
        } else {
            this.guild = Globals.getGuildObject(guild.getLongID());
        }
        this.message = new MessageObject(message, this.guild);
        this.channel = new ChannelObject(channel, this.guild);
        this.user = new UserObject(author, this.guild);
        this.client = new ClientObject(message.getClient(), this.guild);
    }

    public CommandObject setAuthor(IUser author) {
        this.user = new UserObject(author, guild);
        return this;
    }

    public CommandObject setChannel(IChannel channel) {
        this.channel = new ChannelObject(channel, guild);
        return this;
    }

    public CommandObject setGuild(IGuild guild) {
        this.guild = Globals.getGuildObject(guild.getLongID());
        return this;
    }

    public CommandObject setMessage(IMessage message) {
        this.message = new MessageObject(message, guild);
        return this;
    }

    public void send() {
        if (response != null && !response.isEmpty() && embed == null && file == null) {
            Globals.messages.add(RequestBuffer.request(() -> {
                return channel.get().sendMessage(response);
            }).get());
        } else {
            boolean sentEmbed = false;
            if (embed != null) {
                Globals.messages.add(RequestBuffer.request(() -> {
                    return channel.get().sendMessage(response, embed.build());
                }).get());
                sentEmbed = true;
            }
            if (file != null) {
                boolean finalSentEmbed = sentEmbed;
                Globals.messages.add(RequestBuffer.request(() -> {
                    try {
                        if (finalSentEmbed) {
                            return channel.get().sendFile(file);
                        } else {
                            return channel.get().sendFile(response, file);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).get());
            }
        }
        if (Globals.messages.size() > 10) {
            Globals.messages.remove(0);
        }
        response = "";
        file = null;
        embed = null;
    }

    public CommandObject withResponse(String response) {
        this.response = response;
        return this;
    }

    public CommandObject withFile(File file) {
        this.file = file;
        return this;
    }

    public CommandObject withEmbed(EmbedBuilder embed) {
        this.embed = embed;
        return this;
    }
}
