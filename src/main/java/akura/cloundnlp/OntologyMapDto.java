package akura.cloundnlp;

import java.util.List;
import java.util.Map;

/**
 * Created by sameera on 9/22/17.
 */
public class OntologyMapDto {
    private String reviewId;
    private String review;
    private float reviewRating;
    private List<EntityDto> entityDtos;
    private Map<String, Float> categoryMap;
    private Map<Integer, List<String>> data;

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Map<String, Float> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, Float> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
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

    public Map<Integer, List<String>> getData() {
        return data;
    }

    public void setData(Map<Integer, List<String>> data) {
        this.data = data;
    }
}
