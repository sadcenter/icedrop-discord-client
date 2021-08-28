package dev.shitzuu.client.listener.internal;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.command.CommandService;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class CommandExecutionListener implements MessageCreateListener {

    private final CommandService commandService;

    public CommandExecutionListener(CommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        MessageAuthor messageAuthor = event.getMessageAuthor();
        if (!(messageAuthor.isRegularUser())) {
            return;
        }

        String plainContent = event.getMessageContent();
        if (!(plainContent.startsWith("."))) {
            return;
        }

        String[] splitContent = plainContent.replaceFirst("(?i)" + Pattern.quote("."), "")
            .split("\\s+");

        Optional<Command> optionalCommand = commandService.getCommand(splitContent[0]);
        optionalCommand.ifPresent(command -> command.invokeCommand(event, Arrays.copyOfRange(splitContent, 1, splitContent.length)));
    }
}
