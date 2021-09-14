package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.domain.Warn;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.service.WarnService;
import dev.shitzuu.client.utility.UserUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;

public class DelWarnCommand extends Command {

    private final WarnService warnService;

    public DelWarnCommand(WarnService warnService) {
        super("delwarn", "Usuwa ostatnie ostrzeżenia dla wskazanego użytkownika.", "<prefix>delwarn <username>");
        this.warnService = warnService;
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.MANAGE_MESSAGES))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Delwarn")
                .setDescription("Nie posiadasz uprawnień do usuwania ostrzeżeń użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<User> optionalUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (optionalUser.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Delwarn")
                .setDescription("Nie wskazałeś użytkownika, którego ostrzeżenie powinno zostać usunięte.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        User user = optionalUser.get();

        LinkedList<Warn> warnings = warnService.getAssociatedWarnings(user.getIdAsString());
        if (warnings.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Delwarn")
                .setDescription("Użytkownik <@" + user.getId() + "> nie posiada żadnych ostrzeżeń.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Collections.reverse(warnings);

        Warn warn = warnings.removeFirst();

        textChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Delwarn")
            .setDescription("Ostatnie ostrzeżenie użytkownika <@" + warn.getVictimSnowflake() + "> nadane przez <@" + warn.getPunisherSnowflake() + "> z powodem " + warn.getReason() + " zostało usunięte.")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
    }
}
