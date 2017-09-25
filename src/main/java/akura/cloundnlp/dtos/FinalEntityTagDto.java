package akura.cloundnlp.dtos;

/**
 * Created by sameera on 9/24/17.
 */
public class FinalEntityTagDto {
    private String text;
    private float sentiment;
    private float salience;
    private String category;
    private String nounCombination;
    private String nounCombinationCategory;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getSentiment() {
        return sentiment;
    }

    public void setSentiment(float sentiment) {
        this.sentiment = sentiment;
    }

    public float getSalience() {
        return salience;
    }

    public void setSalience(float salience) {
        this.salience = salience;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNounCombination() {
        return nounCombination;
    }

    public void setNounCombination(String nounCombination) {
        this.nounCombination = nounCombination;
    }

    public String getNounCombinationCategory() {
        return nounCombinationCategory;
    }

    public void setNounCombinationCategory(String nounCombinationCategory) {
        this.nounCombinationCategory = nounCombinationCategory;
    }
}
