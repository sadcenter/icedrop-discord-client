package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.utility.UserUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AvatarCommand extends Command {

    public AvatarCommand() {
        super("avatar", "Wyświetla avatar użytkownika.", "<prefix>avatar [optional:<username>]");
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        Optional<User> optionalUser = event.getMessageAuthor().asUser();
        if (optionalUser.isEmpty()) {
            return;
        }

        User target = optionalUser.get();

        Optional<User> extractedUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (extractedUser.isPresent()) {
            target = extractedUser.get();
        }

        TextChannel textChannel = event.getChannel();
        textChannel.sendMessage(EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Avatar")
            .setThumbnail("")
            .setImage(target.getAvatar())
            .setDescription("Avatar użytkownika <@" + target.getId() + ">")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
    }
}
