package dev.shitzuu.client.command;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Command {

    private final String name;
    private final String description;
    private final String syntax;
    private final List<String> aliases;

    protected Command(String name, String description, String syntax, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = aliases;
    }

    protected Command(String name, String description, String syntax) {
        this(name, description, syntax, Collections.emptyList());
    }

    public abstract void invokeCommand(MessageCreateEvent event, String[] arguments);

    public boolean hasPermission(MessageCreateEvent event, PermissionType... permissions) {
        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return false;
        }

        Server server = optionalServer.get();

        Optional<User> optionalAuthor = event.getMessageAuthor().asUser();
        return optionalAuthor.isPresent() && server.hasAnyPermission(optionalAuthor.get(), permissions);
    }

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
