package akura.cloundnlp;

import akura.cloundnlp.dtos.SentenceDto;

import java.io.IOException;
import java.util.List;

public interface RelationshipExtractorInterface {
    /**
     * Sentence tokenize method
     *
     * @param paragraph
     * @return
     */
    List<String> sentenceTokenize(String paragraph);
    /**
     * analyse syntax of sentence
     *
     * @param sentenceList
     * @return
     */
    List<SentenceDto> sentenceSyntaxAnalysis(List<String> sentenceList) throws IOException;
    /**
     * replace entities in a sentence where the phone and this phone is located
     *
     * @param entity
     * @param entityList
     * @return
     */
    List<String> replaceEntityInSentences(String entity, List<String> entityList);
    /**
     * Replace "and it" and starting "it" with the main entity of the previous sentence or the paragraph
     *
     * @param sentenceList
     * @return
     */
    List<String> replaceEntityInSentenceByITContext(List<SentenceDto> sentenceList);
    /**
     * modify given paragraph with the defined rules
     * @param text
     * @param entity
     * @return
     */
    public List<String> executeModifier(String text, String entity) throws IOException;
}
