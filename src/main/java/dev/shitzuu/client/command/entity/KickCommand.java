package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.config.PrimaryConfig.LoggerConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.utility.UserUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Arrays;
import java.util.Optional;

public class KickCommand extends Command {

    private final LoggerConfig loggerConfig;

    public KickCommand(LoggerConfig loggerConfig) {
        super("kick", "Wyrzuca użytkownika z serwera.", "<prefix>kick <username> [optional:<reason>]");
        this.loggerConfig = loggerConfig;
    }

    @Override
    public void invokeCommand(MessageCreateEvent event, String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();

        Optional<User> optionalAuthor = event.getMessageAuthor().asUser();
        if (optionalAuthor.isPresent() && !(server.hasAnyPermission(optionalAuthor.get(), PermissionType.ADMINISTRATOR, PermissionType.KICK_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Kick")
                .setDescription("Nie posiadasz uprawnień do wyrzucania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<User> optionalUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (optionalUser.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Kick")
                .setDescription("Nie wskazałeś użytkownika, który powinien zostać wyrzucony.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        User user = optionalUser.get();
        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.KICK_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Kick")
                .setDescription("Nie posiadasz uprawnień do wyrzucania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        server.kickUser(user, reason.isEmpty()
            ? null
            : reason);

        textChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Kick")
            .setDescription("Użytkownik **" + user.getDiscriminatedName() + "** został wyrzucony z serwera" + (reason.isEmpty()
                ? "."
                : " z powodem " + reason + "."))
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));

        Optional<TextChannel> optionalChannel = event.getApi().getTextChannelById(loggerConfig.getNotificationChannelSnowflake());
        optionalChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
            .setDescription("**Typ operacji:** Wyrzucenie użytkownika")
            .addField("Administrator", "<@" + event.getMessageAuthor().getIdAsString() + ">")
            .addField("Podmiot", "<@" + user.getId() + ">")
            .addField("Powód", reason.isEmpty()
                ? "Powód nie został podany."
                : reason)
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar())));
    }
}
