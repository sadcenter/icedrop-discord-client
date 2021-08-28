package dev.shitzuu.client.censor.domain;

public class CensorAnalysisStatistics {

    private final float elapsedAll;
    private final float elapsedProcessing;

    public CensorAnalysisStatistics(float elapsedAll, float elapsedProcessing) {
        this.elapsedAll = elapsedAll;
        this.elapsedProcessing = elapsedProcessing;
    }

    public float getElapsedAll() {
        return elapsedAll;
    }

    public float getElapsedProcessing() {
        return elapsedProcessing;
    }

    @Override
    public String toString() {
        return "CensorAnalysisStatistics{" +
                "elapsedAll=" + elapsedAll +
                ", elapsedProcessing=" + elapsedProcessing +
                '}';
    }
}
