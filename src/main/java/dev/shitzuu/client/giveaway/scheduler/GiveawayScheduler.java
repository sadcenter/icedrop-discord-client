package dev.shitzuu.client.giveaway.scheduler;

import dev.shitzuu.client.config.PrimaryConfig;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.giveaway.GiveawayService;
import dev.shitzuu.client.giveaway.ModifiableGiveaway;
import dev.shitzuu.client.utility.TimeUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;

import java.util.Optional;

public class GiveawayScheduler implements Runnable {

    private final PrimaryConfig primaryConfig;
    private final GiveawayService giveawayService;
    private final DiscordApi discordApi;

    public GiveawayScheduler(PrimaryConfig primaryConfig, GiveawayService giveawayService, DiscordApi discordApi) {
        this.primaryConfig = primaryConfig;
        this.giveawayService = giveawayService;
        this.discordApi = discordApi;
    }

    @Override
    public void run() {
        for (ModifiableGiveaway giveaway : giveawayService.getGiveaways()) {
            if (giveaway.getExpireAt() < System.currentTimeMillis()) {
                giveawayService.finishGiveaway(discordApi, giveaway);
                continue;
            }

            Optional<ServerTextChannel> optionalChannel = discordApi.getServerTextChannelById(primaryConfig.getGiveawayChannelSnowflake());
            if (optionalChannel.isEmpty()) {
                break;
            }

            discordApi.getMessageById(giveaway.getMessageSnowflake(), optionalChannel.get())
                .thenAccept(sentMessage -> sentMessage.edit(EmbedFactory.produce()
                    .setThumbnail("https://media.discordapp.net/attachments/695176663276978216/888043810221867019/received_332322134635036.png")
                    .setTitle("ICEDROP.EU - Konkurs")
                    .setDescription("Pojawił się nowy konkurs, aby wziąć w nim udział i wygrać **" + giveaway.getItem() + " x" + giveaway.getCount() + "**" + " zareaguj pod tą wiadomością! \n\nZwycięzcy zostaną wylosowani za **" + TimeUtil.toString(giveaway.getExpireAt() - System.currentTimeMillis()) + "**.")
                    .setFooter(discordApi.getYourself().getDiscriminatedName(), discordApi.getYourself().getAvatar())));
        }
    }
}
