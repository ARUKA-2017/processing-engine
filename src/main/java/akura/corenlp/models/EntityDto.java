package akura.corenlp.models;

import java.util.Map;

/**
 * Created by sameera on 7/9/17.
 */
public class EntityDto {
    private String entityName;
    private double baseScore;
    private Map<String, String> property;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public double getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(double baseScore) {
        this.baseScore = baseScore;
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }
}
