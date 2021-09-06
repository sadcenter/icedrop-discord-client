package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.utility.UserUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class KickCommand extends Command {

    public KickCommand() {
        super("kick", "Wyrzuca użytkownika z serwera.", "<prefix>kick <username> [optional:<reason>]");
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();

        Optional<User> optionalAuthor = event.getMessageAuthor().asUser();
        if (optionalAuthor.isPresent() && !(server.hasAnyPermission(optionalAuthor.get(), PermissionType.ADMINISTRATOR, PermissionType.KICK_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setDescription("Nie posiadasz uprawnień do wyrzucania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<User> optionalUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (optionalUser.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setDescription("Nie wskazałeś użytkownika, który powinien zostać wyrzucony.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        User user = optionalUser.get();

        if (server.hasPermission(user, PermissionType.ADMINISTRATOR)) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setDescription("Nie możesz wyrzucić <@" + user.getId() + ">, ponieważ posiada on uprawnienia Administratora.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        server.kickUser(user, reason.isEmpty()
            ? null
            : reason);

        textChannel.sendMessage(EmbedFactory.produce()
            .setDescription("Użytkownik **" + user.getDiscriminatedName() + "** został wyrzucony z serwera" + (reason.isEmpty()
                ? "."
                : " z powodem " + reason + "."))
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
    }
}
