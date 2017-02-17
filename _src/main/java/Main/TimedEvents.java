package Main;

import Objects.TimedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;

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
        doEventDaily(nowUTC);
        doEventHour(nowUTC);
        doEventMin(nowUTC);
        doEventSec();
    }

    public static void addGuildCoolDown(String guildID) {
        TimerObjects.add(new TimedObject(guildID));
        logger.debug("Timed Events initiated for guild with ID: " + guildID);
    }

    private static void doEventDaily(ZonedDateTime nowUTC) {
        ZonedDateTime nextMidnightUTC;
        nextMidnightUTC = nowUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
        long initialDelay = nextMidnightUTC.toEpochSecond() - nowUTC.toEpochSecond();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //event run once a day at 0:00 UTC
                for (TimedObject g : TimerObjects) {
                    //File Backups
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
                //runs this code once every second
            }
        }, 1000, 1000);
    }


    private static void doEventMin(ZonedDateTime nowUTC) {
        ZonedDateTime nextMinUTC;
        nextMinUTC = nowUTC.withSecond(0).withMinute(nowUTC.getMinute() + 1);
        long initialDelay = nextMinUTC.toEpochSecond() - nowUTC.toEpochSecond();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //runs this code once every min
            }
        }, initialDelay, 1000 * 60);
    }

    private static void doEventHour(ZonedDateTime nowUTC) {
        ZonedDateTime nextHourUTC;
        nextHourUTC = nowUTC.withSecond(0).withMinute(0).withHour(nowUTC.getHour() + 1);
        long initialDelay = nextHourUTC.toEpochSecond() - nowUTC.toEpochSecond();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //runs this code once every Hour on the hour mark as of UTC time
            }
        }, initialDelay, 1000 * 60 * 60);
    }

    public static void saveAndLogOff() {
        //Event runs on shutdown.
    }
}
