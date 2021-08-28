package dev.shitzuu.client;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.command.CommandService;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.config.PrimaryConfig.CensorConfig;
import dev.shitzuu.client.config.factory.ConfigFactory;
import dev.shitzuu.client.listener.internal.CommandExecutionListener;
import dev.shitzuu.client.listener.MessageSwearListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class ApplicationContext {

    private final PrimaryConfig primaryConfig;

    public ApplicationContext() {
        this.primaryConfig = new ConfigFactory(System.getProperty("user.dir"))
                .produceConfig(PrimaryConfig.class, "config.hjson");
    }

    public void initialize() {
        DiscordApi discordApi = new DiscordApiBuilder()
            .setAllIntents()
            .setToken(primaryConfig.getToken())
            .login()
            .join();

        CensorConfig censorConfig = primaryConfig.getCensorConfig();

        CensorService censorService = null;
        if (censorConfig.isEnabled()) {
            censorService = new CensorService(primaryConfig.getCensorConfig().getToken());
            discordApi.addMessageCreateListener(new MessageSwearListener(censorService));
        }

        discordApi.addMessageCreateListener(new CommandExecutionListener(new CommandService(primaryConfig.getCensorConfig(), censorService)));
    }
}
