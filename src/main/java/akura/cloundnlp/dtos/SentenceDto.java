package akura.cloundnlp.dtos;

import java.util.List;

/**
 * Created by sameera on 9/27/17.
 */
public class SentenceDto {
    private String sentence;
    private Float totalSalience;
    private List<SentenceWordDto> sentenceWordDtos;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Float getTotalSalience() {
        return totalSalience;
    }

    public void setTotalSalience(Float totalSalience) {
        this.totalSalience = totalSalience;
    }

    public List<SentenceWordDto> getSentenceWordDtos() {
        return sentenceWordDtos;
    }

    public void setSentenceWordDtos(List<SentenceWordDto> sentenceWordDtos) {
        this.sentenceWordDtos = sentenceWordDtos;
    }
}
