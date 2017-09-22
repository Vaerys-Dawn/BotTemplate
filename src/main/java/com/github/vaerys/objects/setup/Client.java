package com.github.vaerys.objects.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Client {
    final static Logger logger = LoggerFactory.getLogger(Client.class);
    public static IDiscordClient client;

    public static IDiscordClient generateClient(String token, boolean login) throws DiscordException {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        clientBuilder.setMaxReconnectAttempts(4000);
        if (login) {
            logger.info("Logging in to Discord");
            client = clientBuilder.login();
        } else {
            client = clientBuilder.build();
        }
        return client;
    }
}
