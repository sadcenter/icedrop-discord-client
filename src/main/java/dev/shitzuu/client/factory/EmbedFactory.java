package dev.shitzuu.client.factory;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.function.Supplier;

public class EmbedFactory {

    private static final Supplier<EmbedBuilder> EMBED_SUPPLIER = () ->
        new EmbedBuilder()
            .setThumbnail("https://media.discordapp.net/attachments/884175063774015522/884461670397804594/logo.png")
            .setColor(EmbedFactory.translateColor("#00BFFFFF"))
            .setFooter("Data ")
            .setTimestampToNow();

    private EmbedFactory() {

    }

    public static EmbedBuilder produce() {
        return EMBED_SUPPLIER.get();
    }

    @SuppressWarnings("SameParameterValue")
    private static Color translateColor(String value) {
        value = value.replace("#", "");
        switch (value.length()) {
            case 6:
                return new Color(
                    Integer.valueOf(value.substring(0, 2), 16),
                    Integer.valueOf(value.substring(2, 4), 16),
                    Integer.valueOf(value.substring(4, 6), 16));
            case 8:
                return new Color(
                    Integer.valueOf(value.substring(0, 2), 16),
                    Integer.valueOf(value.substring(2, 4), 16),
                    Integer.valueOf(value.substring(4, 6), 16),
                    Integer.valueOf(value.substring(6, 8), 16));
            default:
                break;
        }
        return null;
    }
}

