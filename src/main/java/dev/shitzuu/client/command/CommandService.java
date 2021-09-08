package dev.shitzuu.client.command;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.command.entity.*;
import dev.shitzuu.client.config.PrimaryConfig.CensorConfig;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandService {

    private final Set<Command> commands;

    public CommandService(CensorConfig censorConfig, CensorService censorService) {
        this.commands = new HashSet<>();
        this.commands.add(new HelpCommand(this));
        this.commands.add(new PingCommand());
        this.commands.add(new ClearCommand());
        this.commands.add(new AvatarCommand());
        this.commands.add(new KickCommand());
        this.commands.add(new BanCommand());
        this.commands.add(new UnbanCommand());
        if (censorConfig.isEnabled()) {
            this.commands.add(new AnalysisCommand(censorService));
        }
    }

    public Optional<Command> getCommand(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return commands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(name) || command.getAliases().contains(name))
                .findAny();
    }

    public Set<Command> getCommands() {
        return commands;
    }
}
