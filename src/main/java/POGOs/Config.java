package POGOs;

import Objects.DailyObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 14/01/2017.
 */
public class Config {
    public boolean resetToDefault;
    public String botName;
    public String creatorID;
    public String defaultPrefixCommand;
    public String defaultAvatarFile;
    public boolean doDailyAvatars;
    public String dailyAvatarName;
    public String playing;
    public ArrayList<DailyObject> dailyObjects = new ArrayList<>();

    public boolean initObject() {
        if (resetToDefault) {
            resetToDefault = false;
            botName = "S.A.I.L";
            creatorID = "153159020528533505";
            defaultPrefixCommand = "$";
            defaultAvatarFile = "Avatar.png";
            doDailyAvatars = false;
            dailyAvatarName = "Avatar_For_#day#.png";
            playing = "Starbound";
            return true;
        }
        for (DailyObject d: dailyObjects){
            d.updateFilePath(dailyAvatarName);
        }
        return false;
    }

}
