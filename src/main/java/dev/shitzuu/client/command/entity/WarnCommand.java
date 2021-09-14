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

import java.util.Arrays;
import java.util.Optional;

public class WarnCommand extends Command {

    private final WarnService warnService;

    public WarnCommand(WarnService warnService) {
        super("warn", "Nadaje ostrzeżenie dla wskazanego użytkownika.", "<prefix>warn <username> [optional:<reason>]");
        this.warnService = warnService;
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.BAN_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Warn")
                .setDescription("Nie posiadasz uprawnień do ostrzegania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<User> optionalUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (optionalUser.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Warn")
                .setDescription("Nie wskazałeś użytkownika, który powinien zostać ostrzeżony.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        User user = optionalUser.get();

        String reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        warnService.addWarning(new Warn(warnService.getNextWarningIdentifier(user.getIdAsString()),
            event.getMessageAuthor().getIdAsString(),
            user.getIdAsString(),
            reason.isEmpty()
                ? "Powód nie został podany."
                : reason));

        textChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Warn")
            .setDescription("Ostrzeżenie zostało nadane pomyślnie.")
            .addField("Administrator", "<@" + event.getMessageAuthor().getId() + ">")
            .addField("Podmiot", "<@" + user.getId() + ">")
            .addField("Powód", reason.isEmpty()
                ? "Powód nie został podany."
                : reason)
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
    }
}
