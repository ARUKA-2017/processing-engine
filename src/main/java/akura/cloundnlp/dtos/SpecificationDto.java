package akura.cloundnlp.dtos;

import java.util.List;
import java.util.Map;

public class SpecificationDto {
    private FinalEntityTagDto mainEntity;
    private List<FinalEntityTagDto> relativeEntityList;
    private Map<String, String> featureMap;
    private List<SpecRelationshipDto> specRelationshipDtoList;

    public FinalEntityTagDto getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(FinalEntityTagDto mainEntity) {
        this.mainEntity = mainEntity;
    }

    public List<FinalEntityTagDto> getRelativeEntityList() {
        return relativeEntityList;
    }

    public void setRelativeEntityList(List<FinalEntityTagDto> relativeEntityList) {
        this.relativeEntityList = relativeEntityList;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    public List<SpecRelationshipDto> getSpecRelationshipDtoList() {
        return specRelationshipDtoList;
    }

    public void setSpecRelationshipDtoList(List<SpecRelationshipDto> specRelationshipDtoList) {
        this.specRelationshipDtoList = specRelationshipDtoList;
    }
}
