package dev.shitzuu.client.warn;

import org.immutables.value.Value;

@Value.Modifiable
public interface Warn {

    int getIdentifier();

    String getPunisherSnowflake();

    String getVictimSnowflake();

    String getReason();

    long getCreatedAt();
}
