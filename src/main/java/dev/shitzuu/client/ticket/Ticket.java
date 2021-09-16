package dev.shitzuu.client.ticket;

import org.immutables.value.Value;

@Value.Modifiable
public interface Ticket {

    String getCreatorSnowflake();

    String getChannelSnowflake();

    long getCreatedAt();
}
