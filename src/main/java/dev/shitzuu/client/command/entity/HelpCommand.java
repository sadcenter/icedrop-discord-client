package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.command.CommandService;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class HelpCommand extends Command {

    private final CommandService commandService;

    public HelpCommand(CommandService commandService) {
        super("help", "Wyświetla listę dostępnych komend.", "<prefix>help", Collections.singletonList("pomoc"));
        this.commandService = commandService;
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        EmbedBuilder embedBuilder = EmbedFactory.produce()
            .setDescription("Poniżej znajdziesz spis dostępnych komend:")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar());
        for (Command command : commandService.getCommands()) {
            if (command.equals(this)) {
                continue;
            }
            embedBuilder.addInlineField(command.getSyntax().replace("<prefix>", "."), command.getDescription());
        }
        TextChannel textChannel = event.getChannel();
        textChannel.sendMessage(embedBuilder);
    }
}
