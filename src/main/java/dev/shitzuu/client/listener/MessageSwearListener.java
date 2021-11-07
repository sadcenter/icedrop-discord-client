package dev.shitzuu.client.listener;

import dev.shitzuu.client.censor.CensorService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Optional;

public class MessageSwearListener implements MessageCreateListener {

    private final CensorService censorService;

    public MessageSwearListener(CensorService censorService) {
        this.censorService = censorService;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        MessageAuthor messageAuthor = event.getMessageAuthor();
        if (messageAuthor.isBotUser() || messageAuthor.isServerAdmin()) {
            return;
        }

        censorService.analyze(messageAuthor.getIdAsString(), event.getMessageContent())
            .thenAccept(analise -> {
                if (analise.isSwear() && analise.getProbability() > 0.82) {
                    Optional<Server> optionalServer = event.getServer();
                    if (optionalServer.isPresent() && optionalServer.get().hasAnyPermission(event.getApi().getYourself(), PermissionType.ADMINISTRATOR, PermissionType.MANAGE_MESSAGES)) {
                        Message message = event.getMessage();
                        message.delete();
                    }

                    TextChannel textChannel = event.getChannel();
                    textChannel.sendMessage("Wykryto wulgarną wiadomość napisaną przez "
                        + messageAuthor.getDiscriminatedName()
                        + " z prawdopodobieństwem "
                        + (this.round(analise.getProbability()) * 100)
                        + " procent.");
                }
            });
    }

    private double round(float value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
