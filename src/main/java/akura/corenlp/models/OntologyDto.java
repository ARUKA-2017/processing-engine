package akura.corenlp.models;

import java.util.List;

/**
 * Created by sameera on 7/9/17.
 */
public class OntologyDto {
    private ReviewInfoDto reviewInfoDto;
    private List<EntityDto> entities;
    private List<RelationshipDto> relationshipDtos;

    public ReviewInfoDto getReviewInfoDto() {
        return reviewInfoDto;
    }

    public void setReviewInfoDto(ReviewInfoDto reviewInfoDto) {
        this.reviewInfoDto = reviewInfoDto;
    }

    public List<EntityDto> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityDto> entities) {
        this.entities = entities;
    }

    public List<RelationshipDto> getRelationshipDtos() {
        return relationshipDtos;
    }

    public void setRelationshipDtos(List<RelationshipDto> relationshipDtos) {
        this.relationshipDtos = relationshipDtos;
    }
}
