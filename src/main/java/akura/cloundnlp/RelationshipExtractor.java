package akura.cloundnlp;

import akura.cloundnlp.dtos.SentenceDto;
import akura.cloundnlp.dtos.SentenceWordDto;
import akura.utility.APIConnection;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.LanguageServiceClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sameera on 9/26/17.
 */
public class RelationshipExtractor {
    private final static String REGEX = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";
    private EntityExtractor entityExtractor = new EntityExtractor();
    private LanguageServiceClient languageServiceClient;
    /**
     *
     * Relationship extractor methods
     *
     */

    /**
     * Sentence tokenize method
     * @param paragraph
     * @return
     */
    public List<String> sentenceTokenize(String paragraph){
        List<String> sentenceList = new ArrayList<>();
        Matcher reMatcher = Pattern.compile(REGEX, Pattern.MULTILINE | Pattern.COMMENTS).matcher(paragraph);
        while (reMatcher.find()) {
            sentenceList.add(reMatcher.group());
        }
        return sentenceList;
    }

    /**
     * analyse syntax of sentence
     * @param sentenceList
     * @return
     */
    public List<SentenceDto> sentenceSyntaxAnalysis(List<String> sentenceList) throws IOException {
        List<SentenceDto> analyzedSentenceDtoList = new LinkedList<>();

        languageServiceClient = APIConnection.provideLanguageServiceClient();

        sentenceList.forEach(sentence -> {
            List<SentenceWordDto> analyzedSentenceWordDtoList = new LinkedList<>();
            Map<String, List<String>> sentenceEntityAnalysisMap = null;
            Document doc = Document.newBuilder().setContent(sentence).setType(Document.Type.PLAIN_TEXT).build();

            sentenceEntityAnalysisMap = entityExtractor.analyseEntity(languageServiceClient, doc);


            sentenceEntityAnalysisMap.forEach((key, value)->{
                SentenceWordDto sentenceWordDto = new SentenceWordDto();
                sentenceWordDto.setText(value.get(0));
                sentenceWordDto.setSentiment(Float.parseFloat(value.get(2)));
                sentenceWordDto.setSalience(Float.parseFloat(value.get(3)));
                analyzedSentenceWordDtoList.add(sentenceWordDto);
            });

            SentenceDto sentenceDto = new SentenceDto();
            sentenceDto.setSentence(sentence);
            sentenceDto.setSentenceWordDtos(analyzedSentenceWordDtoList);

            double totalSalience = 0f;
            int counter = 0;
            for (SentenceWordDto sentenceWordDto: analyzedSentenceWordDtoList){
                counter++;
                totalSalience = (float)totalSalience + sentenceWordDto.getSalience();
            }
            sentenceDto.setTotalSalience((float) totalSalience);
            analyzedSentenceDtoList.add(sentenceDto);

        });

        return analyzedSentenceDtoList;
    }

    public List<String> replaceEntityInSentences(String entity, List<String> entityList){
        List<String> replacedSentenceList = new LinkedList<>();
        entityList.forEach(sentence -> {
            replacedSentenceList.add(
                    sentence
                            .replaceAll("(?i)the phone", entity)
                            .replaceAll("(?i)this phone", entity)
            );
        });
        return replacedSentenceList;
    }

}