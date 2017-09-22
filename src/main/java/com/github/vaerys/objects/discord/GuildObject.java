package com.github.vaerys.objects.discord;

import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.setup.Client;
import com.github.vaerys.objects.setup.Command;
import com.github.vaerys.objects.setup.GuildFile;
import com.github.vaerys.pogos.GuildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.List;

public class GuildObject {
    public ClientObject client;
    private IGuild object;
    public long longID;
    public GuildData data;
    public List<GuildFile> guildFiles;
    public List<Command> commands;

    private final static Logger logger = LoggerFactory.getLogger(GuildObject.class);

    public GuildObject(IGuild object) {
        this.object = object;
        this.longID = object.getLongID();
        this.data = (GuildData) GuildData.create(GuildData.FILE_PATH, longID, new GuildData());
        this.guildFiles = new ArrayList<GuildFile>() {{
            add(data);
        }};
        this.client = new ClientObject(object.getClient(), this);
        loadCommandData();
    }

    public void loadCommandData() {
        this.commands = new ArrayList<>(Globals.getCommands());
//        checkToggles();
    }

    public GuildObject() {
        this.client = new ClientObject(Client.client, this);
        this.object = null;
        this.longID = -1;
        this.guildFiles = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.commands = new ArrayList<>(Globals.getCommands());
    }

    public IGuild get() {
        return object;
    }
}
