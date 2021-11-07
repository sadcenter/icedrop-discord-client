package dev.shitzuu.client;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.command.CommandService;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.config.PrimaryConfig.CensorConfig;
import dev.shitzuu.client.config.factory.ConfigFactory;
import dev.shitzuu.client.database.DatabaseConnector;
import dev.shitzuu.client.giveaway.GiveawayService;
import dev.shitzuu.client.giveaway.scheduler.GiveawayScheduler;
import dev.shitzuu.client.listener.LoggingListener;
import dev.shitzuu.client.listener.MessageAdvertiseListener;
import dev.shitzuu.client.listener.MessageSwearListener;
import dev.shitzuu.client.listener.VerificationListener;
import dev.shitzuu.client.tasks.StatusScheduledTask;
import dev.shitzuu.client.ticket.listener.TicketCreateListener;
import dev.shitzuu.client.listener.internal.CommandExecutionListener;
import dev.shitzuu.client.ticket.TicketService;
import dev.shitzuu.client.warn.WarnService;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApplicationContext {

    private final PrimaryConfig primaryConfig;

    public ApplicationContext() {
        this.primaryConfig = new ConfigFactory(System.getProperty("user.dir")).produceConfig(PrimaryConfig.class, "config.hjson");
    }

    public void initialize() {
        DiscordApi discordApi = new DiscordApiBuilder()
            .setAllIntents()
            .setToken(primaryConfig.getToken())
            .login()
            .join();

        DatabaseConnector databaseConnector = new DatabaseConnector(primaryConfig.getStorageConfig());

        WarnService warnService = new WarnService(databaseConnector);
        warnService.initialize();

        CensorConfig censorConfig = primaryConfig.getCensorConfig();

        CensorService censorService = null;
        if (censorConfig.isEnabled()) {
            censorService = new CensorService(primaryConfig.getCensorConfig().getToken());
            discordApi.addMessageCreateListener(new MessageSwearListener(censorService));
        }

        GiveawayService giveawayService = new GiveawayService(primaryConfig, databaseConnector);
        giveawayService.initialize(discordApi);

        ScheduledExecutorService scheduledExecutorService = discordApi.getThreadPool().getScheduler();
        scheduledExecutorService.scheduleAtFixedRate(
            new StatusScheduledTask(
                discordApi, primaryConfig.getStatusPool(), primaryConfig.getStatusType()), 0, primaryConfig.getStatusSeconds(), TimeUnit.SECONDS);

        discordApi.addMessageCreateListener(new CommandExecutionListener(new CommandService(
            primaryConfig,
            primaryConfig.getLoggerConfig(),
            primaryConfig.getPollConfig(),
            primaryConfig.getCensorConfig(),
            censorService,
            warnService,
            giveawayService)));

        discordApi.addMessageCreateListener(new MessageAdvertiseListener(primaryConfig, warnService));
        discordApi.addMessageComponentCreateListener(new VerificationListener(primaryConfig));

        TicketService ticketService = new TicketService(primaryConfig, databaseConnector);
        ticketService.initialize();

        discordApi.addMessageComponentCreateListener(new TicketCreateListener(ticketService));

        LoggingListener loggingListener = new LoggingListener(primaryConfig);
        discordApi.addServerMemberJoinListener(loggingListener);
        discordApi.addServerMemberLeaveListener(loggingListener);
    }
}
