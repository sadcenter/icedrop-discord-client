package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.config.PrimaryConfig.PollConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PollCommand extends Command {

    private final PollConfig pollConfig;

    public PollCommand(PollConfig pollConfig) {
        super("poll", "Tworzy ankietę dotyczącą wskazanego pytania.", "<prefix>poll <question>");
        this.pollConfig = pollConfig;
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();
        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.MANAGE_MESSAGES))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ankieta")
                .setDescription("Nie posiadasz uprawnień do tworzenia ankiet.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        if (arguments.length == 0) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ankieta")
                .setDescription("Musisz podać treść pytania, którego ma dotyczyć ankieta.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ankieta")
                .setDescription("**Ankieta**\n" + String.join(" ", arguments))
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()))
            .thenAccept(sentMessage -> {
                DiscordApi discordApi = event.getApi();

                Optional<KnownCustomEmoji> optionalYesEmoji = discordApi.getCustomEmojiById(pollConfig.getYesEmojiSnowflake());
                Optional<KnownCustomEmoji> optionalNoEmoji = discordApi.getCustomEmojiById(pollConfig.getNoEmojiSnowflake());
                if (optionalYesEmoji.isEmpty() || optionalNoEmoji.isEmpty()) {
                    return;
                }

                sentMessage.addReaction(optionalYesEmoji.get());
                sentMessage.addReaction(optionalNoEmoji.get());
            });
    }
}
