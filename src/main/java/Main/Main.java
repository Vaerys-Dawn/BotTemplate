package Main;

import Handlers.FileHandler;
import Listeners.AnnotationListener;
import POGOs.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Starting Program...");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info(">>> Running Shutdown Process <<<");
                Globals.saveFiles();
            }
        });

        String token = null;
        // you need to set a token in Token/Token.txt for the bot to run
        try {
            Discord4J.disableChannelWarnings();
            Discord4J.disableAudio();
            FileHandler.createDirectory(Constants.DIRECTORY_STORAGE);
            FileHandler.createDirectory(Constants.DIRECTORY_GLOBAL_IMAGES);
            FileHandler.createDirectory(Constants.DIRECTORY_BACKUPS);
            FileHandler.createDirectory(Constants.DIRECTORY_TEMP);
            if (!Files.exists(Paths.get(Constants.FILE_CONFIG))) {
                FileHandler.writeToJson(Constants.FILE_CONFIG, new Config());
            }

            //load config phase 1
            Config config = (Config) FileHandler.readFromJson(Constants.FILE_CONFIG, Config.class);
            config.initObject();
            FileHandler.writeToJson(Constants.FILE_CONFIG, config);

            //getting bot token
            try {
                token = FileHandler.readFromFile(Constants.FILE_TOKEN).get(0);
            }catch (IndexOutOfBoundsException e) {
                logger.error(Constants.ERROR_INVALID_TOKEN);
                return;
            }

            if (token == null) {
                logger.error(Constants.ERROR_INVALID_TOKEN);
                return;
            }

            IDiscordClient client = Client.getClient(token, false);

            //load config phase 2
            Globals.initConfig(client, config);

            //login + register listener.
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new AnnotationListener());
            client.login();

            //timed events init
            new TimedEvents();

            while (!client.isReady()) ;

            //makes sure that nothing in the config file will cause an error
            Globals.validateConfig();
            Globals.setVersion();
        } catch (DiscordException ex) {
            logger.error(ex.getErrorMessage());
        } catch (RateLimitException e) {
            e.printStackTrace();
        }
    }
}
