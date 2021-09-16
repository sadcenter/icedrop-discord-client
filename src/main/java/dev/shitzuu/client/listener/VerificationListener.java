package dev.shitzuu.client.listener;

import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.Optional;

public class VerificationListener implements MessageComponentCreateListener {

    private final PrimaryConfig primaryConfig;

    public VerificationListener(PrimaryConfig primaryConfig) {
        this.primaryConfig = primaryConfig;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();
        if (!(interaction.getCustomId().equals("verify-confirm"))) {
            return;
        }

        Optional<Server> optionalServer = interaction.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();

        Optional<Role> optionalRole = server.getRoleById(primaryConfig.getVerifiedGroupSnowflake());
        optionalRole.ifPresent(role -> server.addRoleToUser(interaction.getUser(), role)
            .thenAccept(state -> interaction.createImmediateResponder()
                .addEmbed(EmbedFactory.produce()
                    .setTitle("ICEDROP.EU - Verify")
                    .setDescription("Udało ci się zweryfikować, posiadasz teraz dostęp do wszystkich kanałów.")
                    .setFooter(interaction.getUser().getDiscriminatedName(), interaction.getUser().getAvatar()))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()));
    }
}
