package Objects;

import Main.Constants;
import Main.Utility;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/01/2017.
 */
public class GuildContentObject {
    private String guildID;
    private GuildConfig guildConfig;

    public GuildContentObject(String guildID, GuildConfig guildConfig) {
        this.guildID = guildID;
        this.guildConfig = guildConfig;
    }

    public String getGuildID() {
        return guildID;
    }

    public GuildConfig getGuildConfig() {
        return guildConfig;
    }

    public void setGuildConfig(GuildConfig guildConfig) {
        this.guildConfig = guildConfig;
    }

    public void saveFiles() {
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
    }
}
