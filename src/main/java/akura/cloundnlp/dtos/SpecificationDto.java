package akura.cloundnlp.dtos;

import java.util.Map;

public class SpecificationDto {
    private String mainEntity;
    private Map<String, String> featureMap;

    public String getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(String mainEntity) {
        this.mainEntity = mainEntity;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }
}
