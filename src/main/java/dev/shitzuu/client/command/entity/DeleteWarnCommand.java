package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.service.WarnService;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

public class DeleteWarnCommand extends Command {

    private final WarnService warnService;

    public DeleteWarnCommand(WarnService warnService) {
        super("delwarn", "Usuwa ostatnie ostrzeżenia dla wskazanego użytkownika.", "<prefix>delwarn <username>");
        this.warnService = warnService;
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        // todo: implement that
    }
}
