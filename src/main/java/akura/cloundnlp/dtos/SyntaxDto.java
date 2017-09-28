package akura.cloundnlp.dtos;

/**
 * Dto class for syntax extraction
 */
public class SyntaxDto {
    private String text;
    private String pos;
    private String lemma;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }
}
