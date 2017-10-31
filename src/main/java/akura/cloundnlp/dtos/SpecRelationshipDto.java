package akura.cloundnlp.dtos;

import java.util.Map;

public class SpecRelationshipDto {
    private FinalEntityTagDto finalEntityTagDto;
    private String relationship;
    private Map<String, String> featureMap;

    public FinalEntityTagDto getFinalEntityTagDto() {
        return finalEntityTagDto;
    }

    public void setFinalEntityTagDto(FinalEntityTagDto finalEntityTagDto) {
        this.finalEntityTagDto = finalEntityTagDto;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }
}
