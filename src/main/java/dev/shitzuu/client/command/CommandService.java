package dev.shitzuu.client.command;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.command.entity.*;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.config.PrimaryConfig.CensorConfig;
import dev.shitzuu.client.config.PrimaryConfig.LoggerConfig;
import dev.shitzuu.client.config.PrimaryConfig.PollConfig;
import dev.shitzuu.client.giveaway.GiveawayService;
import dev.shitzuu.client.warn.WarnService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandService {

    private final Set<Command> commands;

    public CommandService(PrimaryConfig primaryConfig, LoggerConfig loggerConfig, PollConfig pollConfig, CensorConfig censorConfig, CensorService censorService, WarnService warnService, GiveawayService giveawayService) {
        this.commands = new HashSet<>();
        this.commands.add(new HelpCommand(this));
        this.commands.add(new PingCommand());
        this.commands.add(new ClearCommand());
        this.commands.add(new AvatarCommand());
        this.commands.add(new KickCommand(loggerConfig));
        this.commands.add(new BanCommand(loggerConfig));
        this.commands.add(new UnbanCommand(loggerConfig));
        this.commands.add(new PollCommand(pollConfig));
        this.commands.add(new SayCommand());
        this.commands.add(new WarnCommand(warnService));
        this.commands.add(new WarnsCommand(warnService));
        this.commands.add(new DelWarnCommand(warnService));
        this.commands.add(new MuteCommand(primaryConfig, loggerConfig));
        this.commands.add(new UnmuteCommand(primaryConfig, loggerConfig));
        this.commands.add(new GiveawayCommand(giveawayService));
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
