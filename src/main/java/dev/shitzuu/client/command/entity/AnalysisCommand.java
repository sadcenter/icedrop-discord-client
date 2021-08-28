package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.censor.domain.CensorAnalysis;
import dev.shitzuu.client.command.Command;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class AnalysisCommand extends Command {

    private final CensorService censorService;

    public AnalysisCommand(CensorService censorService) {
        super("analysis", "Wyświetla {x} ostatnich analiz.", "<prefix>analysis [optional:<identifier>]");
        this.censorService = censorService;
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();
        if (arguments.length == 0) {
            if (censorService.getAnalyses().isEmpty()) {
                textChannel.sendMessage("Historia analiz jest aktualnie pusta, prawdopodobnie wpis którego szukasz wygasł.");
                return;
            }

            StringBuilder stringBuilder = new StringBuilder("Poniżej znajdują się identyfikatory ostatnich analiz, wybierz jedną oraz wyświetl szczegółowe informacje używając [<prefix>analysis <uuid>]");
            stringBuilder.append("\n");
            for (int index = 0; index < censorService.getAnalyses().size(); index++) {
                CensorAnalysis censorAnalysis = censorService.getAnalyses().get(index);
                stringBuilder
                    .append("**")
                    .append("#")
                    .append(index)
                    .append("** (")
                    .append(censorAnalysis.getUniqueId())
                    .append(")")
                    .append(" - probability: ")
                    .append(censorAnalysis.getProbability())
                    .append("\n");
            }

            textChannel.sendMessage(stringBuilder.toString());
            return;
        }

        Optional<CensorAnalysis> optionalAnalyse = censorService.getAnalyse(UUID.fromString(arguments[0]));
        if (optionalAnalyse.isEmpty()) {
            textChannel.sendMessage("Nie znaleziono analizy z pasującym unikalnym identyfikatorem, spróbuj ponownie.");
            return;
        }

        textChannel.sendMessage(optionalAnalyse.get().toString());
    }
}
