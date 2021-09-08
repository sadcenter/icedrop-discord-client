package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        super("unban", "Odblokowywuje użytkownika na danym serwerze.", "<prefix>unban <snowflake>");
    }

    @Override
    public void invokeCommand(@NotNull MessageCreateEvent event, @NotNull String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();

        Optional<User> optionalAuthor = event.getMessageAuthor().asUser();
        if (optionalAuthor.isPresent() && !(server.hasAnyPermission(optionalAuthor.get(), PermissionType.ADMINISTRATOR, PermissionType.BAN_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                    .setDescription("Nie posiadasz uprawnień do odblokowywania użytkowników.")
                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        if (arguments.length == 0) {
            textChannel.sendMessage(EmbedFactory.produce()
                    .setDescription("Musisz wskazać użytkownika (snowflake), którego chcesz odblokować.")
                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        String snowflake = arguments[0];

        boolean isSnowflake = true;
        try {
            Long.parseLong(snowflake);
        } catch (NumberFormatException exception) {
            isSnowflake = false;
        }

        if (!(isSnowflake)) {
            textChannel.sendMessage(EmbedFactory.produce()
                    .setDescription("Podana przez ciebie wartość nie pasuje do typu snowflake.")
                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        server.getBans()
                .thenApply(bans -> bans.stream()
                        .filter(ban -> ban.getUser().getIdAsString().equals(snowflake))
                        .findAny())
                .thenAccept(punishment -> {
                    if (punishment.isEmpty()) {
                        textChannel.sendMessage(EmbedFactory.produce()
                                .setDescription("Użytkownik z podanym identyfikatorem **" + snowflake + "** nie jest zablokowany, bądź nie został odnaleziony.")
                                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
                        return;
                    }

                    server.unbanUser(snowflake)
                            .thenAccept(invocation -> textChannel.sendMessage(EmbedFactory.produce()
                                    .setDescription("Użytkownik <@" + snowflake + "> został odblokowany.")
                                    .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar())));
                });
    }
}
