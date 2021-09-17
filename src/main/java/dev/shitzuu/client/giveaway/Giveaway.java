package dev.shitzuu.client.giveaway;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@Value.Modifiable
public interface Giveaway {

    @Nullable
    String getMessageSnowflake();

    String getItem();

    int getCount();

    long getCreatedAt();

    long getExpireAt();
}
