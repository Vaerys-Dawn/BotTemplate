package POGOs;

import Annotations.ToggleAnnotation;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.ChannelTypeObject;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig {
    String prefixCommand = Constants.PREFIX_COMMAND;
    boolean properlyInit = false;
    String guildName = "";
    boolean generalLogging = false;
    boolean adminLogging = false;

    ArrayList<ChannelTypeObject> channels = new ArrayList<>();

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public boolean isProperlyInit() {
        return properlyInit;
    }

    /**use this method when calling command prefixes if you want them to be "unique" per guild.*/
    public String getPrefixCommand() {
        return prefixCommand;
    }

    public void setPrefixCommand(String prefixCommand) {
        this.prefixCommand = prefixCommand;
    }

    /**Code that is put in here will be run on the first time that the guild config file is created.*/
    public void initConfig() {

    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public ArrayList<ChannelTypeObject> getChannels() {
        return channels;
    }

    /**Getters For the Toggles.*/
    public boolean doGeneralLogging() {
        return generalLogging;
    }

    public boolean doAdminLogging() {
        return adminLogging;
    }


    /**Togglers
    * To add a new toggleable item to the config file add a new method with the toggle annotation to it.*/
    @ToggleAnnotation(name = "GeneralLogging")
    public void toggleLogging() {
        generalLogging = !generalLogging;
    }

    @ToggleAnnotation(name = "AdminLogging")
    public void toggleAdminLogging() {
        adminLogging = !adminLogging;
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


    /**this method will get the channel ID of the channel of a certain type that is stored within the config file*/
    public String getChannelTypeID(String channelType) {
        for (ChannelTypeObject c : channels) {
            if (c.getType().equals(channelType)) {
                return c.getID();
            }
        }
        return null;
    }

    /**this method updates the variables of the guild in the config file any time that the guild has an update event. i.e role update or
     * channel update. it is also run at guild create*/
    public void updateVariables(IGuild guild) {
        //update Guild Name
        setGuildName(guild.getName());

        //update channels.
        for (ChannelTypeObject c : channels){
            IChannel channel = guild.getChannelByID(c.getID());
            if (channel == null){
                channels.remove(c);
            }
        }
    }

    public String getInfo(IGuild guild, IUser author) {
        String guildName = guild.getName();
        LocalDateTime creationDate = guild.getCreationDate();
        IUser guildOwner = guild.getOwner();
        IRegion region = guild.getRegion();
        StringBuilder builder = new StringBuilder();
        builder.append("***[" + guildName.toUpperCase() + "]***");
        builder.append("\n\n> Guild ID : **" + guild.getID());
        builder.append("**\n> Creation Date : **" + creationDate.getYear() + " " + creationDate.getMonth() + " " + creationDate.getDayOfMonth() + " - " + creationDate.getHour() + ":" + creationDate.getMinute());
        builder.append("**\n> Guild Owner : **@" + guildOwner.getName() + "#" + guildOwner.getDiscriminator() + "**");
        if (region != null) {
            builder.append("\n> Region : **" + region.getName() + "**");
        }
        builder.append("\n> Total Members: **" + guild.getUsers().size() + "**");
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_SERVER}, author, guild) || author.getID().equals(Globals.creatorID)) {
            builder.append("\n\n***[GUILD CONFIG OPTIONS]***");
            builder.append("\n> GeneralLogging = **" + doGeneralLogging());
            builder.append("**\n> AdminLogging = **" + doAdminLogging());
        }
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_CHANNELS}, author, guild) || author.getID().equals(Globals.creatorID)) {
            builder.append("\n\n***[CHANNELS]***");
            for (ChannelTypeObject c : getChannels()) {
                builder.append("\n> " + c.getType() + " = **#" + guild.getChannelByID(c.getID()).getName() + "**");
            }
        }
        builder.append("\n\n------{END OF INFO}------");
        Utility.sendDM(builder.toString(), author.getID());
        return "> Info sent to you via Direct Message.";
    }
}
