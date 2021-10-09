package dev.shitzuu.client.listener;

import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

        List<Role> verifiedRoles = primaryConfig.getVerifiedGroupSnowflakes()
            .stream()
            .map(server::getRoleById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toUnmodifiableList());
        for (Role role : verifiedRoles) {
            server.addRoleToUser(interaction.getUser(), role);
        }

        interaction.createImmediateResponder()
            .addEmbed(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Weryfikacja")
                .setDescription("Udało ci się zweryfikować, posiadasz teraz dostęp do wszystkich kanałów.")
                .setFooter(interaction.getUser().getDiscriminatedName(), interaction.getUser().getAvatar()))
            .setFlags(MessageFlag.EPHEMERAL)
            .respond();
    }
}
