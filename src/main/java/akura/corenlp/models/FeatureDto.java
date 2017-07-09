package akura.corenlp.models;

/**
 * Created by sameera on 7/9/17.
 */
public class FeatureDto{
    private String featureName;
    private String secondaryEntitiy;

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getSecondaryEntitiy() {
        return secondaryEntitiy;
    }

    public void setSecondaryEntitiy(String secondaryEntitiy) {
        this.secondaryEntitiy = secondaryEntitiy;
    }

}
