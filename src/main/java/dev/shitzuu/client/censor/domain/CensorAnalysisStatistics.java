package dev.shitzuu.client.censor.domain;

import org.immutables.value.Value;

@Value.Immutable
public interface CensorAnalysisStatistics {

    float getElapsedAll();

    float getElapsedProcessing();
}
