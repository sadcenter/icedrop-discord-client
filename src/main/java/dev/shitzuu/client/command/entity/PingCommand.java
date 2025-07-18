package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", "Wyświetla aktualne opóźnienie aplikacji.", "<prefix>ping");
    }

    @Override
    public void invokeCommand(MessageCreateEvent event, String[] arguments) {
        long measurementStart = System.currentTimeMillis();

        TextChannel textChannel = event.getChannel();
        textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ping")
                .setDescription("Trwa odbijanie piłeczki... :ping_pong:")
                .setAuthor(event.getMessageAuthor()))
            .thenAccept(sentMessage -> sentMessage.edit(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ping")
                .setDescription("Pong! :ping_pong: Aktualnie opóźnienie wynosi " + (System.currentTimeMillis() - measurementStart) + " milisekund.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar())));
    }
}
