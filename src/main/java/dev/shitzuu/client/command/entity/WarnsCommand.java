package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.warn.Warn;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.warn.WarnService;
import dev.shitzuu.client.utility.UserUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;

public class WarnsCommand extends Command {

    private static final int MAXIMUM_WARNING_DISPLAY_LENGTH = 5;

    private final WarnService warnService;

    public WarnsCommand(WarnService warnService) {
        super("warns", "Wyświetla ostrzeżenia użytkownika.", "<prefix>warns [optional:<username>]");
        this.warnService = warnService;
    }

    @Override
    public void invokeCommand(MessageCreateEvent event, String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Optional<User> optionalAuthor = event.getMessageAuthor().asUser();
        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.MANAGE_MESSAGES))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Warns")
                .setDescription("Nie posiadasz uprawnień do wyświetlania informacji na temat ostrzegania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        Optional<User> optionalUser = UserUtil.extractUser(event.getMessage(), arguments);
        if (optionalUser.isEmpty() && optionalAuthor.isEmpty()) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Warns")
                .setDescription("Nie wskazałeś użytkownika, którego ostrzeżenia powinny zostać pokazane.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        User user = optionalUser.orElseGet(optionalAuthor::get);

        EmbedBuilder embedBuilder = EmbedFactory.produce()
            .setTitle("ICEDROP.EU - Warns")
            .setDescription("Poniżej znajduje się lista ostrzeżeń użytkownika <@" + user.getId() + ">.")
            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar());

        int count = 0;

        LinkedList<Warn> warnings = new LinkedList<>(warnService.getAssociatedWarnings(user.getIdAsString()));

        Collections.reverse(warnings);
        for (Warn warn : warnings) {
            count++;
            if (count > MAXIMUM_WARNING_DISPLAY_LENGTH) {
                continue;
            }

            embedBuilder
                .addInlineField("Administrator", "<@" + warn.getPunisherSnowflake() + ">")
                .addInlineField("Podmiot", "<@" + warn.getVictimSnowflake() + ">")
                .addInlineField("Powód", warn.getReason());
        }

        textChannel.sendMessage(embedBuilder);
    }
}
