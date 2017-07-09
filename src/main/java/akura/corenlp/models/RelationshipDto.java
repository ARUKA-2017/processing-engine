package akura.corenlp.models;

/**
 * Created by sameera on 7/9/17.
 */
public class RelationshipDto {
    private String type;
    private String secondaryEntity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecondaryEntity() {
        return secondaryEntity;
    }

    public void setSecondaryEntity(String secondaryEntity) {
        this.secondaryEntity = secondaryEntity;
    }
}
