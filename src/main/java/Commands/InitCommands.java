package Commands;

import Commands.Admin.*;
import Commands.General.*;
import Commands.Creator.*;
import Commands.Help.*;

import java.util.ArrayList;

/**
 * Created by Vaerys on 18/02/2017.
 */
public class InitCommands {

    /**
     * This Class is where you should init all of your commands.
     * if you want to add a new type of command just simply create a new variable in Commands.Command
     * and then it will automatically be added to the types list when you use it in a command.
     * to add a new channel it works in the same way. and if you dont want to use channels, the ChannelHere command
     * will automatically be removed from the command list. theoretically you shouldnt need to mess with any of the
     * other packages unless you want to make more complicated commands.
     *
     * Main.TimedEvents have methods set up to automate tasks ever 1 second, 1 min, 5 minutes, 1 hour, and 1 daily.
     *
     * pom.xml in the root folder is where you can set the bot name and Version number.
     * Change these variables:
     *
     * <groupId>com.github.vaerys</groupId>
     * <artifactId>BotTemplate</artifactId>
     * <version>0.0.4</version>
     */

    public static ArrayList<Command> init(){

        ArrayList<Command> commands = new ArrayList<>();

        //Admin Commands
        commands.add(new ChannelHere());

        //Creator Commands
        commands.add(new Sudo());
        commands.add(new Shutdown());
        commands.add(new UpdateAvatar());

        //General Commands
        commands.add(new Hello());

        //Help Commands
        commands.add(new Help());
        commands.add(new Info());

        return commands;
    }
}
