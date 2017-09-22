package com.github.vaerys.main;

import com.github.vaerys.listeners.AnnotationListener;
import com.github.vaerys.listeners.CreatorHandler;
import com.github.vaerys.listeners.GuildCreateListener;
import com.github.vaerys.listeners.InitEvent;
import com.github.vaerys.objects.setup.Client;
import com.github.vaerys.utils.EventHandler;
import com.github.vaerys.utils.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        new Globals();
        String token = "";


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info(">>> Running Shutdown Process <<<");
                if (Globals.savingFiles) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Globals.saveFiles();
                Globals.shuttingDown = true;
            }
        });


        try {
            token = FileHandler.readFromFile(Constants.FILE_TOKEN).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error("!!!BOT TOKEN NOT VALID PLEASE CHECK \"Storage/Token.txt\" AND UPDATE THE TOKEN!!!");
            System.exit(-1);
        }

        IDiscordClient client = Client.generateClient(token,false);

        ThreadGroup group = new ThreadGroup("GuildCreateGroup");
        final int[] count = new int[]{0};

        //login + register listener.
        client.login();
        ExecutorService guildService = new ThreadPoolExecutor(2, 50, 1,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
                r -> new Thread(group, r, group.getName() + "-Thread-" + ++count[0]));
        ExecutorService commandService = new ThreadPoolExecutor(2, 50, 1,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
                r -> new Thread(group, r, group.getName() + "-Thread-" + ++count[0]));
        ExecutorService creatorService = new ThreadPoolExecutor(2, 50, 1,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
                r -> new Thread(group, r, group.getName() + "-Thread-" + ++count[0]));

        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(guildService, new GuildCreateListener());
        dispatcher.registerListener(commandService, new AnnotationListener());
        dispatcher.registerListener(creatorService, new CreatorHandler());
        dispatcher.registerTemporaryListener(new InitEvent());

        new EventHandler();
    }
}
