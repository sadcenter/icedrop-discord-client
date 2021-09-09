package dev.shitzuu.client;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.command.CommandService;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.config.PrimaryConfig.CensorConfig;
import dev.shitzuu.client.config.factory.ConfigFactory;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.listener.MessageAdvertiseListener;
import dev.shitzuu.client.listener.internal.CommandExecutionListener;
import dev.shitzuu.client.listener.MessageSwearListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Optional;

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

        discordApi.addMessageCreateListener(new CommandExecutionListener(new CommandService(
                primaryConfig.getLoggerConfig(),
                primaryConfig.getPollConfig(),
                primaryConfig.getCensorConfig(),
                censorService)));
        discordApi.addMessageCreateListener(new MessageAdvertiseListener(primaryConfig));

        discordApi.addServerMemberJoinListener(event -> {
            Optional<TextChannel> optionalNotificationChannel = discordApi.getTextChannelById(primaryConfig.getLoggerConfig().getNotificationChannelSnowflake());
            optionalNotificationChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
                    .setTitle("ICEDROP.EU - Dołączenie")
                    .setDescription("**Typ operacji:** Dołączenie na serwer")
                    .addField("Podmiot", "<@" + event.getUser().getIdAsString() + ">")
                    .setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar())));

            Optional<TextChannel> optionalMembershipChangeChannel = discordApi.getTextChannelById(primaryConfig.getWelcomeChannelSnowflake());
            optionalMembershipChangeChannel.ifPresent(membershipChangeChannel -> membershipChangeChannel.sendMessage(EmbedFactory.produce()
                    .setTitle("Witaj " + event.getUser().getName() + "!")
                    .setDescription("Jesteś **" + event.getServer().getMemberCount() + "** osobą na naszym serwerze! \n\nMamy nadzieję, że zostaniesz na dłużej!\n<@" + event.getUser().getId() + ">")
                    .setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar())));
        });

        discordApi.addServerMemberLeaveListener(event -> {
            Optional<TextChannel> optionalNotificationChannel = discordApi.getTextChannelById(primaryConfig.getLoggerConfig().getNotificationChannelSnowflake());
            optionalNotificationChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
                    .setTitle("ICEDROP.EU - Opuszczenie")
                    .setDescription("**Typ operacji:** Opuszczenie serwera")
                    .addField("Podmiot", "<@" + event.getUser().getId() + ">")
                    .setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar())));
        });
    }
}
