package Main;

import Handlers.FileHandler;
import Listeners.AnnotationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Starting Program...");
        Globals.setVersion();

        String token;
        // you need to set a token in Token/Token.txt for the bot to run
        try {
            Discord4J.disableChannelWarnings();
            FileHandler.createDirectory(Constants.DIRECTORY_STORAGE);
            FileHandler.createDirectory(Constants.DIRECTORY_GLOBAL_IMAGES);
            FileHandler.createDirectory(Constants.DIRECTORY_BACKUPS);
            FileHandler.createDirectory(Constants.DIRECTORY_TEMP);
            FileHandler.createDirectory(Constants.DIRECTORY_ERROR);
            List<String> tokenFile = FileHandler.readFromFile(Constants.FILE_TOKEN);
            if (tokenFile == null || tokenFile.size() == 0){
                logger.error("!!!BOT TOKEN NOT VALID PLEASE CHECK \"Storage/Token.txt\" AND UPDATE THE TOKEN!!!");
                return;
            }
            token = tokenFile.get(0);
            IDiscordClient client = Client.getClient(token, false);
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new AnnotationListener());
            client.login();
            new TimedEvents();
            Globals.setClient(client);
            consoleInput();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    TimedEvents.saveAndLogOff();
                }
            });
        } catch (DiscordException ex) {
            logger.error(ex.getErrorMessage());
        } catch (RateLimitException e) {
            e.printStackTrace();
        }
    }

    /**Allows you to send messages to a channel that you pinged via the console*/
    private static void consoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (!Globals.isReady);
        logger.info("Console input initiated.");
        while (scanner.hasNextLine()){
            if (Globals.consoleMessageCID != null) {
                IChannel channel = Globals.getClient().getChannelByID(Globals.consoleMessageCID);
                String message = scanner.nextLine();
                if (!message.equals("")) {
                    Utility.sendMessage(message, channel);
                }
            }
        }
    }
}
