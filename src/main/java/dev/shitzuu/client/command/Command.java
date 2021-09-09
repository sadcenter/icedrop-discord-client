package dev.shitzuu.client.command;

import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class Command {

    private final String name;
    private final String description;
    private final String syntax;
    private final List<String> aliases;

    public Command(String name, String description, String syntax, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = aliases;
    }

    public Command(String name, String description, String syntax) {
        this(name, description, syntax, Collections.emptyList());
    }

    public abstract void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }

    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return "Command{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", syntax='" + syntax + '\'' +
            ", aliases=" + aliases +
            '}';
    }
}
