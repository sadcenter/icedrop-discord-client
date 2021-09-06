package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.censor.CensorService;
import dev.shitzuu.client.censor.domain.CensorAnalysis;
import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class AnalysisCommand extends Command {

    private final CensorService censorService;

    public AnalysisCommand(CensorService censorService) {
        super("analysis", "Wyświetla 5 ostatnich analiz, które zostały oznaczone jako wulgarne.", "<prefix>analysis [optional:<identifier>]");
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

            EmbedBuilder embedBuilder = EmbedFactory.produce()
                .setDescription("Poniżej znajduje się lista ostatnich analiz w których wykryto wulgarną wypowiedź. \n\nAby wyświetlić dokładne informacje na temat z jednej nich użyj komendy **!analysis <id>**")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar());
            for (CensorAnalysis analyse : censorService.getAnalyses()) {
                embedBuilder.addInlineField("Unikalny identyfikator", analyse.getUniqueId().toString());
                embedBuilder.addInlineField("Podmiot", "<@" + analyse.getEntity() + ">");
                embedBuilder.addInlineField("Prawdopodobieństwo", String.valueOf(analyse.getProbability()));
            }

            textChannel.sendMessage(embedBuilder);
            return;
        }

        Optional<CensorAnalysis> optionalAnalyse = censorService.getAnalyse(UUID.fromString(arguments[0]));
        if (optionalAnalyse.isEmpty()) {
            textChannel.sendMessage("Nie znaleziono analizy z pasującym unikalnym identyfikatorem, spróbuj ponownie.");
            return;
        }

        CensorAnalysis censorAnalysis = optionalAnalyse.get();

        textChannel.sendMessage(EmbedFactory.produce()
            .setDescription("Poniżej znajdują się informacje dotyczące analizy o identyfikatorze **" + censorAnalysis.getUniqueId() + "**.")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar())
            .addField("Unikalny identyfikator", censorAnalysis.getUniqueId().toString())
            .addField("Wiadomość", censorAnalysis.getSample())
            .addField("Podmiot", "<@" + censorAnalysis.getEntity() + ">")
            .addField("Prawdopodobieństwo", String.valueOf(censorAnalysis.getProbability()))
            .addField("Czas trwania analizy", censorAnalysis.getStatistics().getElapsedProcessing() + "ms"));
    }
}
