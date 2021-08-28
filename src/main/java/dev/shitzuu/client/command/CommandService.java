package dev.shitzuu.client.command;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.command.entity.AnalysisCommand;
import dev.shitzuu.client.command.entity.PingCommand;
import dev.shitzuu.client.config.PrimaryConfig.CensorConfig;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandService {

    private final Set<Command> commands;

    public CommandService(CensorConfig censorConfig, CensorService censorService) {
        this.commands = new HashSet<>();
        this.commands.add(new PingCommand());
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
}
