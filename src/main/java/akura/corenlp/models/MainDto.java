package akura.corenlp.models;

import java.util.List;

/**
 * Created by sameera on 7/9/17.
 */
public class MainDto{
    private String mainEntity;
    private List<RelationshipDto> relationship;
    private String secondaryEntity;
    private List<FeatureDto> features;

    public String getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(String mainEntity) {
        this.mainEntity = mainEntity;
    }

    public List<FeatureDto> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureDto> features) {
        this.features = features;
    }

    public List<RelationshipDto> getRelationship() {
        return relationship;
    }

    public void setRelationship(List<RelationshipDto> relationship) {
        this.relationship = relationship;
    }

    public String getSecondaryEntity() {
        return secondaryEntity;
    }

    public void setSecondaryEntity(String secondaryEntity) {
        this.secondaryEntity = secondaryEntity;
    }
}
