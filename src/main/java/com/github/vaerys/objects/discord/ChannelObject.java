package com.github.vaerys.objects.discord;

import sx.blah.discord.handle.obj.IChannel;

public class ChannelObject {
    public ClientObject client;
    private IChannel object;
    public long longID;
    public String name;
    public long position;

    public ChannelObject(IChannel channel, GuildObject guild) {
        this.client = new ClientObject(channel.getClient(), guild);
        this.object = channel;
        this.longID = channel.getLongID();
        this.name = channel.getName();
        this.position = channel.getPosition();
    }

    public IChannel get() {
        return object;
    }
}
