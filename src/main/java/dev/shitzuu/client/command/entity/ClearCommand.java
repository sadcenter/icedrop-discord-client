package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends Command {

    public ClearCommand() {
        super("clear", "Usuwa określoną liczbę wiadomości z kanału na którym została użyta.", "<prefix>clear <amount>", Collections.singletonList("purge"));
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();
        if (!(event.isServerMessage())) {
            textChannel.sendMessage(EmbedFactory.produce()
                    .setDescription("Nie możesz usunąć wiadomości, ponieważ jest to możliwe tylko do użycia na serwerze.")
                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        if (arguments.length == 0) {
            textChannel.sendMessage(EmbedFactory.produce()
                    .setDescription("Musisz podać liczbę wiadomości do usunięcia.")
                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        int messageCount;
        try {
            messageCount = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException exception) {
            textChannel.sendMessage(EmbedFactory.produce()
                    .setDescription("Podana przez ciebie wartość nie jest poprawna.")
                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        textChannel.sendMessage(EmbedFactory.produce()
                        .setDescription("Trwa usuwanie " + messageCount + " wiadomości z kanału <#" + textChannel.getId() + ">.")
                        .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()))
                .thenApply(sentMessage -> textChannel.getMessagesBefore(messageCount, sentMessage)
                        .thenAccept(messages -> {
                            messages.forEach(Message::delete);
                            sentMessage.edit(EmbedFactory.produce()
                                    .setDescription("Usunięto " + messages.size() + " wiadomości z kanału <#" + textChannel.getId() + ">.")
                                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));

                            ScheduledExecutorService scheduledExecutorService = event.getApi().getThreadPool().getScheduler();
                            scheduledExecutorService.schedule((Callable<CompletableFuture<Void>>) sentMessage::delete, 3, TimeUnit.SECONDS);
                        }));
    }
}
