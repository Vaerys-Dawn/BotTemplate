package POGOs;

import Main.Constants;
import Objects.ChannelTypeObject;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig {
    String prefixCommand = Constants.PREFIX_COMMAND;
    boolean properlyInit = false;
    String guildName = "";

    ArrayList<ChannelTypeObject> channels = new ArrayList<>();

    public String getPrefixCommand() {
        return prefixCommand;
    }

    public void setPrefixCommand(String prefixCommand) {
        this.prefixCommand = prefixCommand;
    }

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public ArrayList<ChannelTypeObject> getChannels() {
        return channels;
    }

    public void setUpChannel(String channelType, String channelID) {
        if (channels.size() == 0) {
            channels.add(new ChannelTypeObject(channelType, channelID));
            return;
        }
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getType().equals(channelType)) {
                channels.set(i, new ChannelTypeObject(channelType, channelID));
                return;
            }
        }
        channels.add(new ChannelTypeObject(channelType, channelID));
    }

    public String getChannelTypeID(String channelType) {
        for (ChannelTypeObject c : channels) {
            if (c.getType().equals(channelType)) {
                return c.getID();
            }
        }
        return null;
    }

    public void updateVariables(IGuild guild) {
        //update Guild Name
        setGuildName(guild.getName());
    }
}
