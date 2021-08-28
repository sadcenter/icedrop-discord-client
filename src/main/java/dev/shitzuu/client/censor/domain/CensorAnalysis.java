package dev.shitzuu.client.censor.domain;

import java.util.UUID;

public class CensorAnalysis {

    private final String entity;

    private final UUID uniqueId;
    private final String sample;
    private final boolean swear;
    private final String breakdown;
    private final String label;
    private final float probability;
    private final CensorAnalysisStatistics statistics;

    public CensorAnalysis(String entity, String sample, boolean swear, String breakdown, String label, float probability, CensorAnalysisStatistics statistics) {
        this.entity = entity;
        this.uniqueId = UUID.randomUUID();
        this.sample = sample;
        this.swear = swear;
        this.breakdown = breakdown;
        this.label = label;
        this.probability = probability;
        this.statistics = statistics;
    }

    public String getEntity() {
        return entity;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getSample() {
        return sample;
    }

    public boolean isSwear() {
        return swear;
    }

    public String getBreakdown() {
        return breakdown;
    }

    public String getLabel() {
        return label;
    }

    public float getProbability() {
        return probability;
    }

    public CensorAnalysisStatistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "CensorAnalysis{" +
                "sample='" + sample + '\'' +
                ", swear=" + swear +
                ", breakdown='" + breakdown + '\'' +
                ", label='" + label + '\'' +
                ", probability=" + probability +
                ", statistics=" + statistics +
                '}';
    }
}
