package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.config.PrimaryConfig.LoggerConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class UnbanCommand extends Command {

    private final LoggerConfig loggerConfig;

    public UnbanCommand(LoggerConfig loggerConfig) {
        super("unban", "Odblokowywuje użytkownika na danym serwerze.", "<prefix>unban <snowflake>");
        this.loggerConfig = loggerConfig;
    }

    @Override
    public void invokeCommand(MessageCreateEvent event, String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();
        if (!(this.hasPermission(event, PermissionType.ADMINISTRATOR, PermissionType.BAN_MEMBERS))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Unban")
                .setDescription("Nie posiadasz uprawnień do odblokowywania użytkowników.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        if (arguments.length == 0) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Unban")
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
                .setTitle("ICEDROP.EU - Unban")
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
                        .setTitle("ICEDROP.EU - Unban")
                        .setDescription("Użytkownik z podanym identyfikatorem **" + snowflake + "** nie jest zablokowany, bądź nie został odnaleziony.")
                        .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
                    return;
                }

                server.unbanUser(snowflake)
                    .thenAccept(invocation -> {
                        textChannel.sendMessage(EmbedFactory.produce()
                            .setTitle("ICEDROP.EU - Unban")
                            .setDescription("Użytkownik <@" + snowflake + "> został odblokowany.")
                            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));

                        Optional<TextChannel> optionalChannel = event.getApi().getTextChannelById(loggerConfig.getNotificationChannelSnowflake());
                        optionalChannel.ifPresent(notificationChannel -> notificationChannel.sendMessage(EmbedFactory.produce()
                            .setTitle("ICEDROP.EU - Unban")
                            .setDescription("**Typ operacji:** Odblokowanie użytkownika")
                            .addField("Administrator", "<@" + event.getMessageAuthor().getIdAsString() + ">")
                            .addField("Podmiot", "<@" + snowflake + ">")
                            .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar())));
                    });
            });
    }
}
