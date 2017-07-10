package akura.corenlp.models;

import java.util.List;

/**
 * Created by sameera on 7/9/17.
 */
public class OntologyDto {
    private ReviewInfoDto reviewInfo;
    private List<EntityDto> entities;
    private List<RelationshipDto> relationship;

    public ReviewInfoDto getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ReviewInfoDto reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public List<EntityDto> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityDto> entities) {
        this.entities = entities;
    }

    public List<RelationshipDto> getRelationship() {
        return relationship;
    }

    public void setRelationship(List<RelationshipDto> relationship) {
        this.relationship = relationship;
    }
}
