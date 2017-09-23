package com.github.vaerys.utils;

import com.github.vaerys.main.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class EventHandler {

    final static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private static long keepAliveFiveMin;

    public static void checkKeepAlive() {
        if (keepAliveFiveMin - System.currentTimeMillis() > 60 * 5 * 4 * 1000) {
            logger.error("Five Min Timer Failed to respond to keep alive. resetting.");
            doEventFiveMin(ZonedDateTime.now(ZoneOffset.UTC));
        }
    }

    public EventHandler() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        keepAliveFiveMin = System.currentTimeMillis();
        doEventFiveMin(nowUTC);
        doEventDaily(nowUTC);
    }

    private static void doEventFiveMin(ZonedDateTime nowUTC) {
        ZonedDateTime nextTimeUTC;
        long initialDelay;
        if (nowUTC.getMinute() != 59) {
            nextTimeUTC = nowUTC.withSecond(0).withMinute(nowUTC.getMinute() + 1);
        } else {
            if (nowUTC.getHour() == 23 && nowUTC.getMinute() > 54) {
                nextTimeUTC = nowUTC.withDayOfYear(nowUTC.getDayOfYear() + 1).withMinute(0).withHour(0).withSecond(0);
            } else {
                nextTimeUTC = nowUTC.withSecond(0).withHour(nowUTC.getHour() + 1).withMinute(0);
            }
        }
        initialDelay = (nextTimeUTC.toEpochSecond() - nowUTC.toEpochSecond());
        if (initialDelay < 30) {
            initialDelay += 60;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Globals.saveFiles();
            }
        }, initialDelay * 1000, 5 * 60 * 1000);

    }

    private static void doEventDaily(ZonedDateTime nowUTC) {
        ZonedDateTime midnightUTC = ZonedDateTime.now(ZoneOffset.UTC);
        midnightUTC = midnightUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
        long initialDelay = midnightUTC.toEpochSecond() - nowUTC.toEpochSecond() + 4;

        if (initialDelay < 120) {
            initialDelay += 24 * 60 * 60;
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkKeepAlive();
                //do backups
                Globals.getGuilds().forEach(
                        guildObject -> guildObject.guildFiles.forEach(
                                guildFile -> guildFile.backUp()));
                logger.info("Files Backed Up.");
            }
        }, initialDelay * 1000, 24 * 60 * 60 * 1000);
    }
}
