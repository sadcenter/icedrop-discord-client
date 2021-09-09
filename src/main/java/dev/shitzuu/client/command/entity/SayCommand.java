package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class SayCommand extends Command {

    public SayCommand() {
        super("say", "Wysyła wiadomość na podany kanał.", "<prefix>say <channel> <content>");
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        Optional<User> optionalUser = event.getMessageAuthor().asUser();
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        TextChannel textChannel = event.getChannel();
        if (arguments.length < 2) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ogłoszenie")
                .setDescription("Aby użyć tej komendy musisz wskazać kanał na którym ma zostać wysłana wiadomość jak i jej treść.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<ServerTextChannel> optionalChannel = event.getApi().getServerTextChannelById(arguments[0]);
        if (optionalChannel.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ogłoszenie")
                .setDescription("Kanał na który próbujesz wysłać wiadomość nie został odnaleziony, bądź jest on nieprawidłowy.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        ServerTextChannel serverTextChannel = optionalChannel.get();

        Server server = serverTextChannel.getServer();
        if (!(server.hasPermission(user, PermissionType.ADMINISTRATOR))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ogłoszenie")
                .setDescription("Nie posiadasz wystarczających uprawnień aby wysłać wiadomość na ten kanał.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        serverTextChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Ogłoszenie")
            .setDescription(String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length)))
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
    }
}
