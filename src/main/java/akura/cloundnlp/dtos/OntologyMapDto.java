package akura.cloundnlp.dtos;

import java.util.List;
import java.util.Map;

/**
 * Dto class for ontology json map
 */
public class OntologyMapDto {
    private String reviewId;
    private String review;
    private float reviewRating;
    private List<EntityDto> entityDtos;
    private Map<String, Float> categoryMap;
    private List<SyntaxDto> syntaxTagList;
    private List<FinalEntityTagDto> finalEntityTaggedList;
    private SpecificationDto specificationDto;

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(float reviewRating) {
        this.reviewRating = reviewRating;
    }

    public List<EntityDto> getEntityDtos() {
        return entityDtos;
    }

    public void setEntityDtos(List<EntityDto> entityDtos) {
        this.entityDtos = entityDtos;
    }

    public Map<String, Float> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, Float> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public List<SyntaxDto> getSyntaxTagList() {
        return syntaxTagList;
    }

    public void setSyntaxTagList(List<SyntaxDto> syntaxTagList) {
        this.syntaxTagList = syntaxTagList;
    }

    public List<FinalEntityTagDto> getFinalEntityTaggedList() {
        return finalEntityTaggedList;
    }

    public void setFinalEntityTaggedList(List<FinalEntityTagDto> finalEntityTaggedList) {
        this.finalEntityTaggedList = finalEntityTaggedList;
    }

    public SpecificationDto getSpecificationDto() {
        return specificationDto;
    }

    public void setSpecificationDto(SpecificationDto specificationDto) {
        this.specificationDto = specificationDto;
    }
}
