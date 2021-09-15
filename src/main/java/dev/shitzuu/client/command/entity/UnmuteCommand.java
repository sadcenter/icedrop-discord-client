package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.config.PrimaryConfig.LoggerConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.utility.UserUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class UnmuteCommand extends Command {

    private final PrimaryConfig primaryConfig;
    private final PrimaryConfig.LoggerConfig loggerConfig;

    public UnmuteCommand(PrimaryConfig primaryConfig, LoggerConfig loggerConfig) {
        super("unmute", "Odcisza użytkownika na serwerze.", "<prefix>unmute <username>");
        this.primaryConfig = primaryConfig;
        this.loggerConfig = loggerConfig;
    }

    @Override
    public void invokeCommand(MessageCreateEvent event, String[] arguments) {
        Optional<Role> optionalRole = event.getApi().getRoleById(primaryConfig.getMuteGroupSnowflake());
        if (optionalRole.isEmpty()) {
            return;
        }

        Role role = optionalRole.get();

        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();
        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.MUTE_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Unmute")
                .setDescription("Nie posiadasz uprawnień do odciszania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<User> optionalUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (optionalUser.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Unmute")
                .setDescription("Nie wskazałeś użytkownika, który powinien zostać odciszony.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        User user = optionalUser.get();
        if (!(user.getRoles(server).contains(role))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Unmute")
                .setDescription("Nie możesz odciszyć <@" + user.getId() + ">, ponieważ nie jest on wyciszony.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        user.removeRole(role);

        textChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Unmute")
            .setDescription("Użytkownik **" + user.getDiscriminatedName() + "** został odciszony.")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));

        Optional<TextChannel> optionalChannel = event.getApi().getTextChannelById(loggerConfig.getNotificationChannelSnowflake());
        optionalChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
            .setDescription("**Typ operacji:** Odciszenie użytkownika")
            .addField("Administrator", "<@" + event.getMessageAuthor().getIdAsString() + ">")
            .addField("Podmiot", "<@" + user.getId() + ">")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar())));
    }
}
