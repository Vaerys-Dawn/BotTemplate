package com.github.vaerys.objects.discord;

import com.github.vaerys.utils.Utility;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserObject {
    public ClientObject client;
    private IUser object;
    public long longID;
    public String name;
    public String displayName;
    public String username;
    public List<IRole> roles;
    public Color color;
    public String notAllowed;


    public UserObject(IUser object, GuildObject guild) {
        this.client = new ClientObject(object.getClient(), guild);
        this.object = object;
        this.longID = object.getLongID();
        this.name = object.getName();
        this.username = object.getName() + "#" + object.getDiscriminator();
        if (guild.get() != null) {
            this.displayName = object.getDisplayName(guild.get());
            this.roles = object.getRolesForGuild(guild.get());
            this.color = Utility.getUsersColour(get(), guild.get());
        } else {
            this.displayName = name;
            this.roles = new ArrayList<>();
            this.color = Color.white;
        }
        notAllowed = "> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
    }

    public IUser get() {
        return object;
    }
}
