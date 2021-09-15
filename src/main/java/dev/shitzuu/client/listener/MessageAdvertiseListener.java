package dev.shitzuu.client.listener;

import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.warn.ModifiableWarn;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.warn.WarnService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MessageAdvertiseListener implements MessageCreateListener {

    private final PrimaryConfig primaryConfig;
    private final WarnService warnService;

    public MessageAdvertiseListener(PrimaryConfig primaryConfig, WarnService warnService) {
        this.warnService = warnService;
        this.primaryConfig = primaryConfig;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (this.isAdvertisement(event.getMessageContent())) {
            TextChannel textChannel = event.getChannel();
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Warn")
                .setDescription("Wiadomość wysłana przez <@" + event.getMessageAuthor().getId() + "> została usunięta, ponieważ posiadała reklame.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));

            Message message = event.getMessage();
            message.delete();

            warnService.addWarning(ModifiableWarn.create()
                .setIdentifier(warnService.getNextWarningIdentifier(event.getMessageAuthor().getIdAsString()))
                .setPunisherSnowflake(event.getApi().getYourself().getIdAsString())
                .setVictimSnowflake(event.getMessageAuthor().getIdAsString())
                .setReason("Wysyłanie wiadomości, która zawierała reklame.")
                .setCreatedAt(System.currentTimeMillis()));
        }
    }

    private boolean isAdvertisement(String rawContent) {
        boolean found = false;
        for (String domain : primaryConfig.getAdvertisingDomains()) {
            if (rawContent.contains(domain)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
