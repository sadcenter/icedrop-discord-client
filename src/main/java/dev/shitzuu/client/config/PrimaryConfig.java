package dev.shitzuu.client.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import org.javacord.api.entity.activity.ActivityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Header("############################################")
@Header("#                                          #")
@Header("#      icedrop-client - PrimaryConfig      #")
@Header("#                                          #")
@Header("############################################")

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PrimaryConfig extends OkaeriConfig {

    @Comment("This should be the token to your Discord's application.")
    private String token = "This value should be set on your own. ^-^";

    @Comment("This should be the snowflake of channel, where welcome messages will be sent.")
    private String welcomeChannelSnowflake = "885233344462327888";

    @Comment("This should be the snowflake of group, which will be added to user while he will be muted.")
    private String muteGroupSnowflake = "887358441495072828";

    @Comment("This should be the snowflake of your server.")
    private String serverSnowflake = "884175063774015519";

    @Comment("This should be the snowflake of ticket's category.")
    private String ticketCategorySnowflake = "888068014325501993";

    @Comment("This should be the snowflake of group, which will be added to user while he verified his self.")
    private List<String> verifiedGroupSnowflakes = Collections.singletonList("888098120871862362");

    @Comment("This should be the snowflake of giveaway's channel.")
    private String giveawayChannelSnowflake = "887779592423735316";

    @Comment("This should be the list of domains, which should be mark as advertisement.")
    private List<String> advertisingDomains = Arrays.asList(
        "discord.com",
        "discord.gg"
    );

    @Comment("Custom discord status pool")
    private List<String> statusPool = Arrays.asList(
        "ICEDROP.eu",
        "icedrop.eu"
    );

    @Comment("Discord status mode")
    private ActivityType statusType = ActivityType.PLAYING;

    @Comment("Discord status interval seconds")
    private int statusSeconds = 20;

    @Comment("StorageConfig - this section contains values related with storage's configuration.")
    private StorageConfig storageConfig = new StorageConfig();

    public static class StorageConfig extends OkaeriConfig {

        @Comment("This should be the name of your database.")
        private String databaseName = "icedrop-client";

        @Comment("This should be the hostname of your database server.")
        private String hostname = "127.0.0.1";

        @Comment("This should be the port to your database server.")
        private int port = 3306;

        @Comment("This should be the username of your account.")
        private String username = "root";

        @Comment("This should be the password to your account.")
        private String password = "my-secret-password-123";

        public String getDatabaseName() {
            return databaseName;
        }

        public void setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Comment("LoggerConfig - this section contains values related with logging.")
    private LoggerConfig loggerConfig = new LoggerConfig();

    public static class LoggerConfig extends OkaeriConfig {

        @Comment("This should be the snowflake of discord's channel, where notifications should be sent.")
        private String notificationChannelSnowflake = "885572130543116298";

        public String getNotificationChannelSnowflake() {
            return notificationChannelSnowflake;
        }

        public void setNotificationChannelSnowflake(String notificationChannelSnowflake) {
            this.notificationChannelSnowflake = notificationChannelSnowflake;
        }
    }

    @Comment("PollConfig - this section contains values related with pool creation process.")
    private PollConfig pollConfig = new PollConfig();

    public static class PollConfig extends OkaeriConfig {

        @Comment("This should be the snowflake of yes emoji.")
        public String yesEmojiSnowflake = "885232455496396850";

        @Comment("This should be the snowflake of no emoji.")
        public String noEmojiSnowflake = "885232342472482846";

        public String getYesEmojiSnowflake() {
            return yesEmojiSnowflake;
        }

        public void setYesEmojiSnowflake(String yesEmojiSnowflake) {
            this.yesEmojiSnowflake = yesEmojiSnowflake;
        }

        public String getNoEmojiSnowflake() {
            return noEmojiSnowflake;
        }

        public void setNoEmojiSnowflake(String noEmojiSnowflake) {
            this.noEmojiSnowflake = noEmojiSnowflake;
        }
    }

    @Comment("CensorConfig - this section contains values related with okaeri's ai-censor product.")
    private CensorConfig censorConfig = new CensorConfig();

    public static class CensorConfig extends OkaeriConfig {

        @Comment("This should be the value which is representing status of censor.")
        private boolean enabled = false;

        @Comment("This should be the token to purchased product.")
        private String token = "This value should be set on your own. ^_^";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @Comment("[!] You shouldn't change this value manually, because it is changing automatically. ^_^")
    private int ticketCount = 0;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWelcomeChannelSnowflake() {
        return welcomeChannelSnowflake;
    }

    public void setWelcomeChannelSnowflake(String welcomeChannelSnowflake) {
        this.welcomeChannelSnowflake = welcomeChannelSnowflake;
    }

    public String getMuteGroupSnowflake() {
        return muteGroupSnowflake;
    }

    public void setMuteGroupSnowflake(String muteGroupSnowflake) {
        this.muteGroupSnowflake = muteGroupSnowflake;
    }

    public String getServerSnowflake() {
        return serverSnowflake;
    }

    public void setServerSnowflake(String serverSnowflake) {
        this.serverSnowflake = serverSnowflake;
    }

    public String getTicketCategorySnowflake() {
        return ticketCategorySnowflake;
    }

    public void setTicketCategorySnowflake(String ticketCategorySnowflake) {
        this.ticketCategorySnowflake = ticketCategorySnowflake;
    }

    public List<String> getVerifiedGroupSnowflakes() {
        return verifiedGroupSnowflakes;
    }

    public void setVerifiedGroupSnowflakes(List<String> verifiedGroupSnowflakes) {
        this.verifiedGroupSnowflakes = verifiedGroupSnowflakes;
    }

    public String getGiveawayChannelSnowflake() {
        return giveawayChannelSnowflake;
    }

    public void setGiveawayChannelSnowflake(String giveawayChannelSnowflake) {
        this.giveawayChannelSnowflake = giveawayChannelSnowflake;
    }

    public List<String> getAdvertisingDomains() {
        return advertisingDomains;
    }

    public List<String> getStatusPool() {
        return statusPool;
    }

    public ActivityType getStatusType() {
        return statusType;
    }

    public int getStatusSeconds() {
        return statusSeconds;
    }

    public void setStatusPool(List<String> statusPool) {
        this.statusPool = statusPool;
    }

    public void setStatusType(ActivityType statusType) {
        this.statusType = statusType;
    }

    public void setStatusSeconds(int statusSeconds) {
        this.statusSeconds = statusSeconds;
    }

    public void setAdvertisingDomains(List<String> advertisingDomains) {
        this.advertisingDomains = advertisingDomains;
    }

    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    public LoggerConfig getLoggerConfig() {
        return loggerConfig;
    }

    public void setLoggerConfig(LoggerConfig loggerConfig) {
        this.loggerConfig = loggerConfig;
    }

    public PollConfig getPollConfig() {
        return pollConfig;
    }

    public void setPollConfig(PollConfig pollConfig) {
        this.pollConfig = pollConfig;
    }

    public CensorConfig getCensorConfig() {
        return censorConfig;
    }

    public void setCensorConfig(CensorConfig censorConfig) {
        this.censorConfig = censorConfig;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }
}
