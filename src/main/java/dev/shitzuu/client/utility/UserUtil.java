package dev.shitzuu.client.utility;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class UserUtil {

    private UserUtil() {

    }

    public static Optional<User> extractUser(Message message, String[] arguments) {
        if (message == null || arguments == null) {
            return Optional.empty();
        }

        Optional<Server> optionalServer = message.getServer();
        if (optionalServer.isEmpty() || arguments.length == 0) {
            return Optional.empty();
        }

        Server server = optionalServer.get();

        List<User> mentions = message.getMentionedUsers();
        if (!(mentions.isEmpty()) && arguments[0].startsWith("<@")) {
            return Optional.of(mentions.get(0));
        }

        Collection<User> results = server.getMembersByName(arguments[0]);
        if (!(results.isEmpty())) {
            return Optional.of(results.iterator().next());
        }

        return server.getMemberById(arguments[0]);
    }
}
