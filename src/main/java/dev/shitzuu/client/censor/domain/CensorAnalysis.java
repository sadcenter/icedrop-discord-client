package dev.shitzuu.client.censor.domain;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface CensorAnalysis {

    UUID getUniqueId();

    String getEntity();

    String getSample();

    boolean isSwear();

    String getBreakdown();

    String getLabel();

    float getProbability();

    CensorAnalysisStatistics getStatistics();
}
