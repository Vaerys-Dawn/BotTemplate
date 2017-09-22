package com.github.vaerys.listeners;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.discord.GuildObject;
import com.github.vaerys.utils.FileHandler;
import com.github.vaerys.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;

public class GuildCreateListener {

    final static Logger logger = LoggerFactory.getLogger(GuildCreateListener.class);

    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        long guildID = guild.getLongID();
        logger.debug("Starting Initialisation process for Guild with ID: " + guildID);

        FileHandler.createDirectory(Utility.getDirectory(guildID));
        FileHandler.createDirectory(Utility.getDirectory(guildID, true));

        GuildObject guildObject = new GuildObject(guild);
        Globals.initGuild(guildObject);
        logger.info("Finished Initialising Guild With ID: " + guildID);
    }

}
