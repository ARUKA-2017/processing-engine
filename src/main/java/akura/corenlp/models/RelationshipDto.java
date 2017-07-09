package akura.corenlp.models;

/**
 * Created by sameera on 7/9/17.
 */
public class RelationshipDto {
    private String type;
    private String entity_1;
    private String entity_2;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntity_1() {
        return entity_1;
    }

    public void setEntity_1(String entity_1) {
        this.entity_1 = entity_1;
    }

    public String getEntity_2() {
        return entity_2;
    }

    public void setEntity_2(String entity_2) {
        this.entity_2 = entity_2;
    }
}
