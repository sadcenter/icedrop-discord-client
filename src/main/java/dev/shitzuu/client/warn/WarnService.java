package dev.shitzuu.client.warn;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import dev.shitzuu.client.database.DatabaseConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WarnService {

    private final LoadingCache<String, LinkedList<Warn>> warnCache = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(30))
        .build(this::loadWarnings);
    private final DatabaseConnector databaseConnector;

    public WarnService(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void initialize() {
        try (Statement statement = databaseConnector.getConnection().createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `iclient_warnings` (`id` INT(11) PRIMARY KEY AUTO_INCREMENT, `identifier` INT(11), `punisher-snowflake` VARCHAR(64), `victim-snowflake` VARCHAR(64), `reason` VARCHAR(256) NOT NULL, `createdAt` MEDIUMTEXT);");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public LinkedList<Warn> loadWarnings(String snowflake) {
        LinkedList<Warn> warnings = new LinkedList<>();
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("SELECT * FROM `iclient_warnings` WHERE `victim-snowflake` = ?;")) {
            preparedStatement.setString(1, snowflake);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                warnings.add(ModifiableWarn.create()
                    .setIdentifier(resultSet.getInt("identifier"))
                    .setPunisherSnowflake(resultSet.getString("punisher-snowflake"))
                    .setVictimSnowflake(resultSet.getString("victim-snowflake"))
                    .setReason(resultSet.getString("reason"))
                    .setCreatedAt(resultSet.getLong("createdAt")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return warnings;
    }

    public LinkedList<Warn> getAssociatedWarnings(String snowflake) {
        return warnCache.get(snowflake);
    }

    public void addWarning(Warn warn) {
        List<Warn> warnings = this.getAssociatedWarnings(warn.getVictimSnowflake());

        boolean duplicate = warnings.stream().anyMatch(warning -> warning.getIdentifier() == warn.getIdentifier());
        if (duplicate) {
            return;
        }

        this.saveWarning(warn);

        List<Warn> warns = warnCache.get(warn.getVictimSnowflake());
        if (warns == null) {
            return;
        }

        warns.add(warn);
    }

    public Warn removeNewestWarning(String snowflake) {
        LinkedList<Warn> warnings = new LinkedList<>(this.getAssociatedWarnings(snowflake));

        Collections.reverse(warnings);

        Warn warning = warnings.getFirst();

        this.getAssociatedWarnings(snowflake).remove(warning);
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("DELETE FROM `iclient_warnings` WHERE `identifier` = ? AND `victim-snowflake` = ?;")) {
            preparedStatement.setInt(1, warning.getIdentifier());
            preparedStatement.setString(2, warning.getVictimSnowflake());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return warning;
    }

    public void saveWarning(Warn warn) {
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("INSERT INTO `iclient_warnings` (`identifier`, `punisher-snowflake`, `victim-snowflake`, `reason`, `createdAt`) VALUES (?, ?, ?, ?, ?);")) {
            preparedStatement.setInt(1, warn.getIdentifier());
            preparedStatement.setString(2, warn.getPunisherSnowflake());
            preparedStatement.setString(3, warn.getVictimSnowflake());
            preparedStatement.setString(4, warn.getReason());
            preparedStatement.setLong(5, warn.getCreatedAt());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public int getNextWarningIdentifier(String snowflake) {
        return (this.getAssociatedWarnings(snowflake).size()) + 1;
    }
}
