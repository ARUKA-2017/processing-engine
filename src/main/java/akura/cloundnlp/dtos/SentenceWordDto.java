package akura.cloundnlp.dtos;

/**
 * Dto class for sentence words
 */
public class SentenceWordDto {
    private String text;
    private float sentiment;
    private float salience;

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
}
