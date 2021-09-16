package dev.shitzuu.client.ticket;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.database.DatabaseConnector;
import dev.shitzuu.client.factory.EmbedFactory;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TicketService {

    private final Cache<String, ModifiableTicket> ticketCache = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofHours(2))
        .build();

    private final PrimaryConfig primaryConfig;
    private final DatabaseConnector databaseConnector;

    public TicketService(PrimaryConfig primaryConfig, DatabaseConnector databaseConnector) {
        this.primaryConfig = primaryConfig;
        this.databaseConnector = databaseConnector;
    }

    public void initialize() {
        try (Statement statement = databaseConnector.getConnection().createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `iclient_tickets` (`id` INT(11) PRIMARY KEY AUTO_INCREMENT, `creator-snowflake` VARCHAR(64), `channel-snowflake` VARCHAR(64), `createdAt` MEDIUMTEXT);");
        } catch (SQLException exception) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("There was an unexpected incident while trying to initialize ticket's table.", exception);
        }
    }

    public void createTicket(DiscordApi discordApi, User user) {
        Optional<Server> optionalServer = discordApi.getServerById(primaryConfig.getServerSnowflake());
        if (optionalServer.isEmpty()) {
            CompletableFuture.supplyAsync(Optional::empty);
            return;
        }

        Server server = optionalServer.get();

        Optional<ChannelCategory> optionalChannelCategory = discordApi.getChannelCategoryById(primaryConfig.getTicketCategorySnowflake());
        if (optionalChannelCategory.isEmpty()) {
            CompletableFuture.supplyAsync(Optional::empty);
            return;
        }

        new ServerTextChannelBuilder(server)
            .setName("ticket-" + user.getName())
            .addPermissionOverwrite(server.getEveryoneRole(), new PermissionsBuilder()
                .setAllDenied()
                .build())
            .addPermissionOverwrite(user, new PermissionsBuilder()
                .setAllowed(PermissionType.SEND_MESSAGES, PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY)
                .build())
            .setCategory(optionalChannelCategory.get())
            .create()
            .thenApply(textChannel -> {
                new MessageBuilder()
                    .setContent("<@" + user.getId() + ">")
                    .setEmbed(EmbedFactory.produce()
                        .setTitle("ICEDROP.EU - Ticket")
                        .setDescription("Twój ticket został stworzony, możesz zadać swoje pytanie / opisać problem.")
                        .setFooter(user.getDiscriminatedName(), user.getAvatar()))
                    .addComponents(ActionRow.of(
                        Button.danger("ticket-close", "Zamknij ticket")
                    ))
                    .send(textChannel);
                return ModifiableTicket.create()
                    .setCreatorSnowflake(user.getIdAsString())
                    .setChannelSnowflake(textChannel.getIdAsString())
                    .setCreatedAt(System.currentTimeMillis());
            })
            .thenApply(ticket -> {
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("INSERT INTO `iclient_tickets` (`creator-snowflake`, `channel-snowflake`, `createdAt`) VALUES (?, ?, ?);")) {
                    preparedStatement.setString(1, ticket.getCreatorSnowflake());
                    preparedStatement.setString(2, ticket.getChannelSnowflake());
                    preparedStatement.setLong(3, ticket.getCreatedAt());
                    preparedStatement.executeUpdate();
                } catch (SQLException exception) {
                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.error("There was an unexpected incident while trying to insert ticket.", exception);
                }
                return Optional.of(ticket);
            });
    }

    public void closeTicket(DiscordApi discordApi, Ticket ticket) {
        Optional<ServerTextChannel> optionalChannel = discordApi.getServerTextChannelById(ticket.getChannelSnowflake());
        if (optionalChannel.isPresent()) {
            ServerTextChannel serverTextChannel = optionalChannel.get();
            serverTextChannel.delete();
        }

        discordApi.getUserById(ticket.getCreatorSnowflake())
            .thenAccept(user -> user.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Ticket")
                .setDescription("Twój ticket został zamknięty, mamy nadzieję iż uzyskałeś odpowiedź na swoje pytanie lub została udzielona ci pomoc. Jeśli masz dodatkowe pytania, stwórz nowy ticket!")
                .setFooter(user.getDiscriminatedName(), user.getAvatar())));

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("DELETE FROM `iclient_tickets` WHERE `creator-snowflake` = ? AND `channel-snowflake` = ?;")) {
            preparedStatement.setString(1, ticket.getCreatorSnowflake());
            preparedStatement.setString(2, ticket.getChannelSnowflake());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("There was an unexpected incident while trying to delete ticket.", exception);
        }

        ticketCache.invalidate(ticket.getChannelSnowflake());
    }

    public CompletableFuture<Optional<ModifiableTicket>> getTicket(String snowflake) {
        Optional<ModifiableTicket> optionalTicket = this.getTicketLocally(snowflake);
        if (optionalTicket.isPresent()) {
            return CompletableFuture.supplyAsync(() -> optionalTicket);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("SELECT * FROM `iclient_tickets` WHERE `creator-snowflake` = ? OR `channel-snowflake` = ?;")) {
                preparedStatement.setString(1, snowflake);
                preparedStatement.setString(2, snowflake);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    return Optional.of(this.cacheAndGet(ModifiableTicket.create()
                        .setCreatorSnowflake(resultSet.getString("creator-snowflake"))
                        .setChannelSnowflake(resultSet.getString("channel-snowflake"))
                        .setCreatedAt(resultSet.getLong("createdAt"))));
                }
                return Optional.empty();
            } catch (SQLException exception) {
                Logger logger = LoggerFactory.getLogger(this.getClass());
                logger.error("There was an unexpected incident while trying to find ticket.", exception);
            }
            return Optional.empty();
        });
    }

    public Optional<ModifiableTicket> getTicketLocally(String snowflake) {
        ModifiableTicket ticket = ticketCache.getIfPresent(snowflake);
        if (ticket != null) {
            return Optional.of(ticket);
        }
        return ticketCache.asMap().values()
            .stream()
            .filter(iterator -> iterator.getCreatorSnowflake().equals(snowflake))
            .findAny();
    }

    private ModifiableTicket cacheAndGet(ModifiableTicket ticket) {
        ticketCache.put(ticket.getChannelSnowflake(), ticket);
        return ticket;
    }
}
