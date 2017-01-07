package Objects;


import java.util.ArrayList;

/**
 * Created by Vaerys on 30/09/2016.
 */
public class TimedObject {
    public int doAdminMention = 0;
    String guildID;

    public TimedObject(String guildID) {
        this.guildID = guildID;
    }

    public String getGuildID() {
        return guildID;
    }

}
