package dev.shitzuu.client.giveaway;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.database.DatabaseConnector;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.giveaway.scheduler.GiveawayScheduler;
import dev.shitzuu.client.utility.TimeUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GiveawayService {

    private final Cache<Integer, ModifiableGiveaway> giveawayCache = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(30))
        .build();

    private final PrimaryConfig primaryConfig;
    private final DatabaseConnector databaseConnector;

    public GiveawayService(PrimaryConfig primaryConfig, DatabaseConnector databaseConnector) {
        this.primaryConfig = primaryConfig;
        this.databaseConnector = databaseConnector;
    }

    public void initialize(DiscordApi discordApi) {
        try (Statement statement = databaseConnector.getConnection().createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `iclient_giveaways` (`id` INT(11) PRIMARY KEY AUTO_INCREMENT, `message-snowflake` VARCHAR(64), `item` VARCHAR(128), `count` INT(11), `createdAt` MEDIUMTEXT, `expireAt` MEDIUMTEXT);");
        } catch (SQLException exception) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("There was an unexpected incident while trying to initialize giveaways table.", exception);
        }

        try (Statement statement = databaseConnector.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `iclient_giveaways`");
            while (resultSet.next()) {
                giveawayCache.put(giveawayCache.asMap().size(), ModifiableGiveaway.create()
                    .setMessageSnowflake(resultSet.getString("message-snowflake"))
                    .setItem(resultSet.getString("item"))
                    .setCount(resultSet.getInt("count"))
                    .setCreatedAt(resultSet.getLong("createdAt"))
                    .setExpireAt(resultSet.getLong("expireAt")));
            }
        } catch (SQLException exception) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("There was an unexpected incident while trying to load giveaways.", exception);
        }

        ScheduledExecutorService scheduledExecutorService = discordApi.getThreadPool().getScheduler();
        scheduledExecutorService.scheduleAtFixedRate(new GiveawayScheduler(primaryConfig, this, discordApi), 0, 1, TimeUnit.MINUTES);
    }

    public void createGiveaway(DiscordApi discordApi, String item, int count, long expireAfter) {
        CompletableFuture.supplyAsync(() -> this.cacheAndGet(ModifiableGiveaway.create()
            .setItem(item)
            .setCount(count)
            .setCreatedAt(System.currentTimeMillis())
            .setExpireAt(System.currentTimeMillis() + expireAfter)))
            .thenApply(giveaway -> {
                Optional<TextChannel> optionalChannel = discordApi.getTextChannelById(primaryConfig.getGiveawayChannelSnowflake());
                if (optionalChannel.isEmpty()) {
                    return giveaway;
                }

                TextChannel textChannel = optionalChannel.get();
                textChannel.sendMessage(EmbedFactory.produce()
                        .setThumbnail("https://media.discordapp.net/attachments/695176663276978216/888043810221867019/received_332322134635036.png")
                        .setTitle("ICEDROP.EU - Konkurs")
                        .setDescription("Pojawił się nowy konkurs, aby wziąć w nim udział i wygrać **" + giveaway.getItem() + " x" + giveaway.getCount() + "**" + " zareaguj pod tą wiadomością! \n\nZwycięzcy zostaną wylosowani za **" + TimeUtil.toString(giveaway.getExpireAt() - giveaway.getCreatedAt()) + "**.")
                        .setFooter(discordApi.getYourself().getDiscriminatedName(), discordApi.getYourself().getAvatar()))
                    .thenAccept(sentMessage -> {
                        sentMessage.addReaction("\uD83C\uDF89");
                        giveaway.setMessageSnowflake(sentMessage.getIdAsString());
                    })
                    .join();
                return giveaway;
            })
            .thenAccept(giveaway -> {
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("INSERT INTO `iclient_giveaways` (`message-snowflake`, `item`, `count`, `createdAt`, `expireAt`) VALUES (?, ?, ?, ?, ?);")) {
                    preparedStatement.setString(1, giveaway.getMessageSnowflake());
                    preparedStatement.setString(2, giveaway.getItem());
                    preparedStatement.setInt(3, giveaway.getCount());
                    preparedStatement.setLong(4, giveaway.getCreatedAt());
                    preparedStatement.setLong(5, giveaway.getExpireAt());
                    preparedStatement.executeUpdate();
                } catch (SQLException exception) {
                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.error("There was an unexpected incident while trying to save giveaway.", exception);
                }
            });
    }


    public void finishGiveaway(DiscordApi discordApi, ModifiableGiveaway giveaway) {
        CompletableFuture.runAsync(() -> {
            Optional<ServerTextChannel> optionalChannel = discordApi.getServerTextChannelById(primaryConfig.getGiveawayChannelSnowflake());
            if (optionalChannel.isEmpty()) {
                return;
            }

            discordApi.getMessageById(giveaway.getMessageSnowflake(), optionalChannel.get())
                .thenAccept(message -> {
                    Optional<Reaction> optionalReaction = message.getReactionByEmoji("\uD83C\uDF89");
                    if (optionalReaction.isEmpty()) {
                        message.delete();
                        return;
                    }

                    Reaction reaction = optionalReaction.get();
                    reaction.getUsers()
                        .thenApply(originalUsers -> {
                            List<User> winners = new ArrayList<>();

                            List<User> users = new ArrayList<>(originalUsers);
                            users.remove(discordApi.getYourself());
                            if (users.isEmpty()) {
                                return winners;
                            }

                            for (int index = 0; index < giveaway.getCount(); index++) {
                                if (users.isEmpty()) {
                                    continue;
                                }

                                User user = users.get(ThreadLocalRandom.current().nextInt(users.size()));
                                users.remove(user);

                                winners.add(user);
                            }

                            return winners;
                        })
                        .thenApply(users -> {
                            message.edit("@everyone", EmbedFactory.produce()
                                    .setThumbnail("https://media.discordapp.net/attachments/695176663276978216/888043810221867019/received_332322134635036.png")
                                    .setTitle("ICEDROP.EU - Konkurs")
                                    .setDescription("Konkurs na **" + giveaway.getItem() + " x" + giveaway.getCount() + "** został zakończony." + (users.isEmpty()
                                        ? " Niestety nikt nie wygrał, ponieważ nikt nie wziął w nim udziału."
                                        : "\n\nZwycięzcy:\n" + users.stream()
                                        .map(User::getMentionTag)
                                        .collect(Collectors.joining("\n"))))
                                    .setFooter(discordApi.getYourself().getDiscriminatedName(), discordApi.getYourself().getAvatar()));
                            return users;
                        })
                        .thenAccept(ignored -> {
                            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("DELETE FROM `iclient_giveaways` WHERE `message-snowflake` = ?;")) {
                                preparedStatement.setString(1, giveaway.getMessageSnowflake());
                                preparedStatement.executeUpdate();
                            } catch (SQLException exception) {
                                Logger logger = LoggerFactory.getLogger(this.getClass());
                                logger.error("There was an unexpected incident while trying to remove giveaway.", exception);
                            }

                            for (Map.Entry<Integer, ModifiableGiveaway> entry : giveawayCache.asMap().entrySet()) {
                                if (entry.getValue().equals(giveaway)) {
                                    giveawayCache.invalidate(entry.getKey());
                                }
                            }
                        });
                });
        });
    }

    private ModifiableGiveaway cacheAndGet(ModifiableGiveaway giveaway) {
        giveawayCache.put(giveawayCache.asMap().size(), giveaway);
        return giveaway;
    }

    public List<ModifiableGiveaway> getGiveaways() {
        return new ArrayList<>(this.giveawayCache.asMap().values());
    }
}
