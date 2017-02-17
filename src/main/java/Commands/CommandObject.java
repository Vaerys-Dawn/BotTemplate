package Commands;

import Main.Globals;
import Main.Utility;
import Objects.GuildContentObject;
import POGOs.GuildConfig;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vaerys on 29/01/2017.
 */
public class CommandObject {

    public IMessage message;
    public String messageID;
    public IGuild guild;
    public String guildID;
    public IChannel channel;
    public String channelID;
    public IUser author;
    public String authorID;
    public String authorDisplayName;
    public String authorUserName;
    public Color authorColour;
    public List<IRole> authorRoles;
    public String notAllowed;

    public GuildConfig guildConfig;

    public ArrayList<Command> commands = new ArrayList<>();

    public IDiscordClient client;

    public CommandObject(IMessage message) {
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        init();
    }

    public CommandObject(IMessage message, IGuild guild, IChannel channel, IUser author) {
        this.message = message;
        this.guild = guild;
        this.channel = channel;
        this.author = author;
        validate();
        init();
    }

    private void init() {
        messageID = message.getID();
        guildID = guild.getID();
        channelID = channel.getID();
        authorID = author.getID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);

        GuildContentObject guildFiles = Globals.getGuildContent(guildID);
        guildConfig = guildFiles.getGuildConfig();
        client = Globals.getClient();

        commands = Globals.getCommands();

        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
    }

    private void validate() throws IllegalStateException {
        if (message == null) throw new IllegalStateException("message can't be null");
        if (guild == null) throw new IllegalStateException("guild can't be null");
        if (channel == null) throw new IllegalStateException("channel can't be null");
        if (author == null) throw new IllegalStateException("author can't be null");
    }

    public void setAuthor(IUser author) {
        this.author = author;
        authorID = author.getID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);
        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
    }

    public void setChannel(IChannel channel) {
        this.channel = channel;
        channelID = channel.getID();
    }

    public void setGuild(IGuild guild) {
        this.guild = guild;
        guildID = guild.getID();
        GuildContentObject contentObject = Globals.getGuildContent(guildID);
        guildConfig = contentObject.getGuildConfig();
        if (guild.getUserByID(authorID) != null) {
            authorColour = Utility.getUsersColour(author, guild);
            authorRoles = author.getRolesForGuild(guild);
            authorDisplayName = author.getDisplayName(guild);
            notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
        }
    }

    public void setMessage(IMessage message) {
        this.message = message;
        messageID = message.getID();
    }
}
