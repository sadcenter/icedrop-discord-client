package dev.shitzuu.client.domain;

public class Warn {

    private int identifier;
    private String punisherSnowflake;
    private String victimSnowflake;
    private String reason;
    private long createdAt;

    public Warn(int identifier, String punisherSnowflake, String victimSnowflake, String reason, long createdAt) {
        this.identifier = identifier;
        this.punisherSnowflake = punisherSnowflake;
        this.victimSnowflake = victimSnowflake;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Warn(int identifier, String punisherSnowflake, String victimSnowflake, String reason) {
        this(identifier, punisherSnowflake, victimSnowflake, reason, System.currentTimeMillis());
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getPunisherSnowflake() {
        return punisherSnowflake;
    }

    public void setPunisherSnowflake(String punisherSnowflake) {
        this.punisherSnowflake = punisherSnowflake;
    }

    public String getVictimSnowflake() {
        return victimSnowflake;
    }

    public void setVictimSnowflake(String victimSnowflake) {
        this.victimSnowflake = victimSnowflake;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Warn{" +
            "identifier=" + identifier +
            ", punisherSnowflake='" + punisherSnowflake + '\'' +
            ", victimSnowflake='" + victimSnowflake + '\'' +
            ", reason='" + reason + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
