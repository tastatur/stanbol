package de.unidue.stanbol.entities.filter.data;

public class EntitiesFilterEngineConfiguration {
    public static final String MINIMAL_ENTITY_HUB_RANK_PROPERTY = "de.unidue.stanbol.entities.filter.minEntityHubRank";
    public static final String MINIMAL_CONFIDENCE_PROPERTY = "de.unidue.stanbol.entities.filter.minConfidence";

    private float minEntityHubRank;
    private double minConfidence;

    public double getMinEntityHubRank() {
        return minEntityHubRank;
    }

    public void setMinEntityHubRank(float minEntityHubRank) {
        this.minEntityHubRank = minEntityHubRank;
    }

    public double getMinConfidence() {
        return minConfidence;
    }

    public void setMinConfidence(float minConfidence) {
        this.minConfidence = minConfidence;
    }
}
