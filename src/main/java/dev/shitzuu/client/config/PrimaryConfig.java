package dev.shitzuu.client.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;

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
