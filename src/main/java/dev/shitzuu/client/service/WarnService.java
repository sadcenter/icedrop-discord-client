package dev.shitzuu.client.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import dev.shitzuu.client.database.DatabaseConnector;
import dev.shitzuu.client.domain.Warn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `ic_warnings` (`id` INT(11) PRIMARY KEY AUTO_INCREMENT, `identifier` INT(11), `punisherSnowflake` VARCHAR(64), `victimSnowflake` VARCHAR(64), `reason` VARCHAR(256) NOT NULL, `createdAt` MEDIUMTEXT);");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public LinkedList<Warn> loadWarnings(String snowflake) {
        LinkedList<Warn> warnings = new LinkedList<>();
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("SELECT * FROM `ic_warnings` WHERE `victimSnowflake` = ?;")) {
            preparedStatement.setString(1, snowflake);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                warnings.add(new Warn(
                    resultSet.getInt("identifier"),
                    resultSet.getString("punisherSnowflake"),
                    resultSet.getString("victimSnowflake"),
                    resultSet.getString("reason"),
                    resultSet.getLong("createdAt")
                ));
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

    public void saveWarning(Warn warn) {
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement("INSERT INTO `ic_warnings` (`identifier`, `punisherSnowflake`, `victimSnowflake`, `reason`, `createdAt`) VALUES (?, ?, ?, ?, ?);")) {
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
