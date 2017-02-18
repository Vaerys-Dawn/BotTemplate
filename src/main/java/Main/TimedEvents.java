package Main;

import Objects.TimedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.File;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class TimedEvents {

    static ArrayList<TimedObject> TimerObjects = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(TimedEvents.class);

    public TimedEvents() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        doEventSec();
        doEventMin(nowUTC);
        doEventFiveMin(nowUTC);
        dailyTasks(nowUTC);
    }

    public static void addGuild(String guildID) {
        for (TimedObject t : TimerObjects) {
            if (t.getGuildID().equals(guildID)) {
                return;
            }
        }
        TimerObjects.add(new TimedObject(guildID));
        logger.debug("Timed Events initiated for guild with ID: " + guildID);
    }

    private static void dailyTasks(ZonedDateTime nowUTC) {
        ZonedDateTime midnightUTC = ZonedDateTime.now(ZoneOffset.UTC);
        midnightUTC = midnightUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
        long initialDelay = midnightUTC.toEpochSecond() - nowUTC.toEpochSecond() + 4;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
                String dailyFileName = Globals.dailyAvatarName.replace("#day#", timeNow.getDayOfWeek().toString());
                DayOfWeek day = timeNow.getDayOfWeek();
                File avatarFile;

                logger.info("Running Daily tasks for " + day);

                //sets Avatar.
                if (Globals.doDailyAvatars) {
                    avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + dailyFileName);
                } else {
                    avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile);
                }
                Image avatar = Image.forFile(avatarFile);
                Utility.updateAvatar(avatar);

                //backups
                Utility.backupConfigFile(Constants.FILE_CONFIG, Constants.FILE_CONFIG_BACKUP);
                for (TimedObject g : TimerObjects) {
                    Utility.backupFile(g.getGuildID(), Constants.FILE_GUILD_CONFIG);
                }
            }
        }, initialDelay * 1000, 24 * 60 * 60 * 1000);
    }


    private static void doEventSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // timed task per 1 sec
            }
        }, 1000, 1000);
    }


    private static void doEventMin(ZonedDateTime nowUTC) {
        ZonedDateTime nextTimeUTC;
        nextTimeUTC = nowUTC.withSecond(0).withMinute(nowUTC.getMinute() + 1);
        long initialDelay = (nextTimeUTC.toEpochSecond() - nowUTC.toEpochSecond());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //runs this code once every min
            }
        }, initialDelay * 1000, 1000 * 60);
    }

    private static void doEventHour(ZonedDateTime nowUTC) {
        ZonedDateTime nextTimeUTC;
        nextTimeUTC = nowUTC.withSecond(0).withMinute(0).withHour(nowUTC.getHour() + 1);
        long initialDelay = (nextTimeUTC.toEpochSecond() - nowUTC.toEpochSecond());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //runs this code once every Hour on the hour mark as of UTC time
            }
        }, initialDelay * 1000, 1000 * 60 * 60);
    }

    private static void doEventFiveMin(ZonedDateTime nowUTC) {
        while (!Globals.getClient().isReady());
        ZonedDateTime nextTimeUTC;
        nextTimeUTC = nowUTC.withSecond(0).withMinute(nowUTC.getMinute() + 1);
        long initialDelay = (nextTimeUTC.toEpochSecond() - nowUTC.toEpochSecond());
        logger.info("Delay till first Backup: " + initialDelay);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Sending isAlive Check.
                try {
                    Globals.getClient().checkLoggedIn("IsAlive");
                    Globals.saveFiles();
                } catch (DiscordException e) {
                    logger.error(e.getErrorMessage());
                    logger.info("Logging back in.");
                    try {
                        Globals.getClient().login();
                        return;
                    } catch (IllegalStateException ex) {
                        //ignore exception
                    }
                }
            }
        }, initialDelay * 1000 , 5 * 60 * 1000);
    }

}
