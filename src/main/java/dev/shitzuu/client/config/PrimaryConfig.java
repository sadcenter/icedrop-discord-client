package dev.shitzuu.client.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter

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

    @Comment("This should be the list of domains, which should be mark as advertisement.")
    private List<String> advertisingDomains = Arrays.asList(
        "discord.com",
        "discord.gg"
    );

    @Comment("StorageConfig - this section contains values related with storage's configuration.")
    private StorageConfig storageConfig = new StorageConfig();

    @Getter
    @Setter
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
    }

    @Comment("LoggerConfig - this section contains values related with logging.")
    private LoggerConfig loggerConfig = new LoggerConfig();

    @Getter
    @Setter
    public static class LoggerConfig extends OkaeriConfig {

        @Comment("This should be the snowflake of discord's channel, where notifications should be sent.")
        private String notificationChannelSnowflake = "885572130543116298";
    }

    @Comment("PollConfig - this section contains values related with pool creation process.")
    private PollConfig pollConfig = new PollConfig();

    @Getter
    @Setter
    public static class PollConfig extends OkaeriConfig {

        @Comment("This should be the snowflake of yes emoji.")
        public String yesEmojiSnowflake = "885232455496396850";

        @Comment("This should be the snowflake of no emoji.")
        public String noEmojiSnowflake = "885232342472482846";
    }

    @Comment("CensorConfig - this section contains values related with okaeri's ai-censor product.")
    private CensorConfig censorConfig = new CensorConfig();

    @Getter
    @Setter
    public static class CensorConfig extends OkaeriConfig {

        @Comment("This should be the value which is representing status of censor.")
        private boolean enabled = false;

        @Comment("This should be the token to purchased product.")
        private String token = "This value should be set on your own. ^_^";
    }
}
