package dev.shitzuu.client.tasks;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;

import java.util.List;

public class StatusScheduledTask implements Runnable {

    private int index = 0;

    private final DiscordApi discordApi;
    private final List<String> poll;
    private final ActivityType activityType;

    public StatusScheduledTask(DiscordApi discordApi, List<String> poll, ActivityType activityType) {
        this.discordApi = discordApi;
        this.poll = poll;
        this.activityType = activityType;

    }

    @Override
    public void run() {
        discordApi.updateActivity(activityType, poll.get(index));

        index++;
        if (index > poll.size() - 1) {
            index = 0;
        }

    }
}
