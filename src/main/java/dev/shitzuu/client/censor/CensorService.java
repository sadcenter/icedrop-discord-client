package dev.shitzuu.client.censor;

import dev.shitzuu.client.censor.domain.CensorAnalysis;
import dev.shitzuu.client.censor.domain.ImmutableCensorAnalysis;
import dev.shitzuu.client.censor.domain.ImmutableCensorAnalysisStatistics;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CensorService {

    private static final int CACHE_MAXIMUM_SIZE = 5;

    private final String token;
    private final LinkedList<CensorAnalysis> analyses;

    public CensorService(String token) {
        this.token = token;
        this.analyses = new LinkedList<>();
    }

    public Optional<CensorAnalysis> getAnalyse(UUID uniqueId) {
        return this.analyses.stream()
            .filter(analyse -> analyse.getUniqueId().equals(uniqueId))
            .findAny();
    }

    public CompletableFuture<ImmutableCensorAnalysis> analyze(String entity, String sample) {
        return Unirest.post("https://ai-censor.okaeri.eu/predict")
            .header("Token", this.token)
            .header("Content-Type", "application/json")
            .charset(StandardCharsets.UTF_8)
            .body(new JSONObject()
                .put("phrase", sample))
            .asJsonAsync()
            .thenApply(HttpResponse::getBody)
            .thenApply(JsonNode::getObject)
            .thenApply(jsonObject -> {
                JSONObject generalObject = jsonObject.getJSONObject("general");
                JSONObject detailsObject = jsonObject.getJSONObject("details");
                JSONObject elapsedObject = jsonObject.getJSONObject("elapsed");
                return this.cacheAndGet(ImmutableCensorAnalysis.builder()
                    .uniqueId(UUID.randomUUID())
                    .entity(entity)
                    .sample(sample)
                    .isSwear(generalObject.getBoolean("swear"))
                    .breakdown(generalObject.getString("breakdown"))
                    .label(detailsObject.getString("ai_label"))
                    .probability(detailsObject.getFloat("ai_probability"))
                    .statistics(ImmutableCensorAnalysisStatistics.builder()
                        .elapsedAll(elapsedObject.getFloat("all"))
                        .elapsedProcessing(elapsedObject.getFloat("processing"))
                        .build())
                    .build());
            });
    }

    private ImmutableCensorAnalysis cacheAndGet(ImmutableCensorAnalysis censorAnalysis) {
        if (!(censorAnalysis.isSwear())) {
            return censorAnalysis;
        }

        if (analyses.size() >= CACHE_MAXIMUM_SIZE) {
            analyses.removeLast();
        }

        analyses.addFirst(censorAnalysis);
        return censorAnalysis;
    }

    public List<CensorAnalysis> getAnalyses() {
        return analyses;
    }
}
