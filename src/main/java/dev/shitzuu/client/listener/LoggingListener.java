package dev.shitzuu.client.listener;

import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;
import org.javacord.api.listener.server.member.ServerMemberLeaveListener;

import java.util.Optional;

public class LoggingListener implements ServerMemberJoinListener, ServerMemberLeaveListener {

    private final PrimaryConfig primaryConfig;

    public LoggingListener(PrimaryConfig primaryConfig) {
        this.primaryConfig = primaryConfig;
    }

    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        Optional<TextChannel> optionalNotificationChannel = event.getApi().getTextChannelById(primaryConfig.getLoggerConfig().getNotificationChannelSnowflake());
        optionalNotificationChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Dołączenie")
            .setDescription("**Typ operacji:** Dołączenie na serwer")
            .addField("Podmiot", "<@" + event.getUser().getIdAsString() + ">")
            .setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar())));

        Optional<TextChannel> optionalMembershipChangeChannel = event.getApi().getTextChannelById(primaryConfig.getWelcomeChannelSnowflake());
        optionalMembershipChangeChannel.ifPresent(membershipChangeChannel -> membershipChangeChannel.sendMessage(EmbedFactory.produce()
            .setTitle("Witaj " + event.getUser().getName() + "!")
            .setDescription("Jesteś **" + event.getServer().getMemberCount() + "** osobą na naszym serwerze! \n\nMamy nadzieję, że zostaniesz na dłużej!\n<@" + event.getUser().getId() + ">")
            .setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar())));
    }

    @Override
    public void onServerMemberLeave(ServerMemberLeaveEvent event) {
        Optional<TextChannel> optionalNotificationChannel = event.getApi().getTextChannelById(primaryConfig.getLoggerConfig().getNotificationChannelSnowflake());
        optionalNotificationChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Opuszczenie")
            .setDescription("**Typ operacji:** Opuszczenie serwera")
            .addField("Podmiot", "<@" + event.getUser().getId() + ">")
            .setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar())));
    }
}
