package dev.shitzuu.client.command.entity;

import dev.shitzuu.client.command.Command;
import dev.shitzuu.client.factory.EmbedFactory;
import dev.shitzuu.client.giveaway.GiveawayService;
import dev.shitzuu.client.utility.TimeUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Arrays;
import java.util.Optional;

public class GiveawayCommand extends Command {

    private final GiveawayService giveawayService;

    public GiveawayCommand(GiveawayService giveawayService) {
        super("giveaway", "Tworzy konkursy czasowe na wskazany przedmiot.", "<prefix>giveaway <count> <time> <item>");
        this.giveawayService = giveawayService;
    }

    @Override
    public void invokeCommand(MessageCreateEvent event, String[] arguments) {
        TextChannel textChannel = event.getChannel();

        Optional<Server> optionalServer = event.getServer();
        if (optionalServer.isEmpty()) {
            return;
        }

        Server server = optionalServer.get();

        Optional<User> optionalAuthor = event.getMessageAuthor().asUser();
        if (optionalAuthor.isPresent() && !(server.hasAnyPermission(optionalAuthor.get(), PermissionType.ADMINISTRATOR))) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Konkurs")
                .setDescription("Nie posiadasz uprawnień do tworzenia konkursów.")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        if (arguments.length < 3) {
            textChannel.sendMessage(EmbedFactory.produce()
                .setTitle("ICEDROP.EU - Konkurs")
                .setDescription("Musisz podać nazwę nagrody, ilość oraz czas trwania konkursu!")
                .setFooter(event.getMessageAuthor().getDiscriminatedName(), event.getMessageAuthor().getAvatar()));
            return;
        }

        giveawayService.createGiveaway(event.getApi(), String.join(" ", Arrays.copyOfRange(arguments, 2, arguments.length)), Integer.parseInt(arguments[0]), TimeUtil.fromString(arguments[1]));
    }
}
