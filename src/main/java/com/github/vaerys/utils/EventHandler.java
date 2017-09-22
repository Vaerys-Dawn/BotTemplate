package com.github.vaerys.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class EventHandler {

    final static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private static long keepAliveTenSec;
    private static long keepAliveMin;
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


            }
        }, initialDelay * 1000, 24 * 60 * 60 * 1000);
    }

}
