package dev.shitzuu.client.config.factory;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.hjson.HjsonConfigurer;
import eu.okaeri.configs.serdes.ObjectSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfigFactory {

    private final File directory;

    public ConfigFactory(File directory) {
        this.directory = directory;
    }

    public ConfigFactory(String path) {
        this(new File(path));
    }

    public <T extends OkaeriConfig> T produceConfig(@NotNull Class<T> clazz, @NotNull String fileName, @NotNull ObjectSerializer<?>... serializers) {
        return this.produceConfig(clazz, new File(this.directory, fileName), serializers);
    }

    public <T extends OkaeriConfig> T produceConfig(@NotNull Class<T> clazz, @NotNull File file, @NotNull ObjectSerializer<?>... serializers) {
        return ConfigManager.create(clazz, initializer -> initializer
                .withConfigurer(new HjsonConfigurer(), registry -> {
                    for (ObjectSerializer<?> serializer : serializers) {
                        registry.register(serializer);
                    }
                })
                .withBindFile(file)
                .saveDefaults()
                .load(true));
    }
}
