package com.github.vaerys.listeners;

import com.github.vaerys.main.Globals;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class InitEvent {

    @EventSubscriber
    public void initBot(ReadyEvent event) {
        //makes sure that nothing in the config file will cause an error
        Globals.setVersion();
        Globals.isReady = true;
    }
}
