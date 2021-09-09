package dev.shitzuu.client.listener;

import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MessageAdvertiseListener implements MessageCreateListener {

    private final PrimaryConfig primaryConfig;

    public MessageAdvertiseListener(PrimaryConfig primaryConfig) {
        this.primaryConfig = primaryConfig;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (this.isAdvertisement(event.getMessageContent())) {
            TextChannel textChannel = event.getChannel();
            textChannel.sendMessage(EmbedFactory.produce()
                .setDescription("Wiadomość wysłana przez <@" + event.getMessageAuthor().getId() + "> została usunięta, ponieważ posiadała reklame.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));

            Message message = event.getMessage();
            message.delete();
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
