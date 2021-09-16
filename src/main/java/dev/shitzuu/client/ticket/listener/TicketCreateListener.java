package dev.shitzuu.client.ticket.listener;

import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.ticket.TicketService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.Optional;

public class TicketCreateListener implements MessageComponentCreateListener {

    private final TicketService ticketService;

    public TicketCreateListener(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();

        Optional<TextChannel> optionalChannel = interaction.getChannel();
        if (optionalChannel.isEmpty()) {
            return;
        }

        TextChannel textChannel = optionalChannel.get();

        switch (interaction.getCustomId()) {
            case "ticket-create": {
                ticketService.getTicket(interaction.getUser().getIdAsString())
                    .thenAccept(optionalTicket -> {
                        if (optionalTicket.isPresent()) {
                            interaction.createImmediateResponder()
                                .addEmbed(EmbedFactory.produce()
                                    .setTitle("ICEDROP.EU - Ticket")
                                    .setDescription("Ticket nie został utworzony, ponieważ posiadasz już stworzony ticket, dostępny tutaj: <#" + optionalTicket.get().getChannelSnowflake() + ">")
                                    .setFooter(interaction.getUser().getDiscriminatedName(), interaction.getUser().getAvatar()))
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond();
                            return;
                        }
                        ticketService.createTicket(event.getApi(), interaction.getUser());
                    });
                break;
            }
            case "ticket-close": {
                ticketService.getTicket(textChannel.getIdAsString())
                    .thenAccept(optionalTicket -> optionalTicket.ifPresent(modifiableTicket -> ticketService.closeTicket(event.getApi(), modifiableTicket)));
                break;
            }
            default:
                break;
        }
    }
}
