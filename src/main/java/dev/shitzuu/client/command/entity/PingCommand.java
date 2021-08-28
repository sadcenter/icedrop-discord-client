package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", "Wyświetla aktualne opóźnienie aplikacji.", "<prefix>ping");
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();
        textChannel.sendMessage(":ping_pong: Pong!");
    }
}
