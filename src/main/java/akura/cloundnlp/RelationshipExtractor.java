package akura.cloundnlp;

import akura.cloundnlp.dtos.SentenceDto;
import akura.cloundnlp.dtos.SentenceWordDto;
import akura.utility.APIConnection;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by sameera on 9/26/17.
 */
public class RelationshipExtractor {
    private final static String REGEX = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";
    private EntityExtractor entityExtractor = new EntityExtractor();
    private LanguageServiceClient languageServiceClient;

    public RelationshipExtractor() {
        try {
            languageServiceClient = APIConnection.provideLanguageServiceClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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

    public List<String> replaceEntityInSentenceByITContext(String entity, List<SentenceDto> sentenceList){
        List<String> replacedSentenceList = new LinkedList<>();
        int sentenceCount = 0;

        for(SentenceDto sentence: sentenceList) {//sentence list
            if (sentenceCount > 0 && (sentence.getSentence().startsWith("It") || (sentence.getSentence().startsWith("it")))){
                String prevSentence = sentenceList.get(sentenceCount-1).getSentence();
                Document doc = Document.newBuilder().setContent(prevSentence).setType(Document.Type.PLAIN_TEXT).build();
                Map<String, List<String>> prevSentenceEntityAnalysisMap = entityExtractor.analyseEntity(languageServiceClient, doc);
                List<SentenceWordDto> prevSentenceWordDtos = new LinkedList<>();
                SentenceDto prevSentenceDto = new SentenceDto();
                prevSentenceDto.setSentence(prevSentence);
                prevSentenceDto.setTotalSalience(0f);
                prevSentenceEntityAnalysisMap.forEach((key, value) -> {
                    SentenceWordDto prevSentenceWordDto = new SentenceWordDto();
                    prevSentenceWordDto.setText(value.get(0));
                    prevSentenceWordDto.setSentiment(Float.parseFloat(value.get(2)));
                    prevSentenceWordDto.setSalience(Float.parseFloat(value.get(3)));
                    prevSentenceWordDtos.add(prevSentenceWordDto);
                });
                prevSentenceDto.setSentenceWordDtos(prevSentenceWordDtos);

                List<Float> salience = new LinkedList<>();

                prevSentenceDto.getSentenceWordDtos().forEach(words -> {
                    if (prevSentence.contains(words.getText())){
                        salience.add(words.getSalience());
                    }
                });
                Collections.sort(salience, Collections.reverseOrder());
                SentenceWordDto eligibleEntity = prevSentenceDto.getSentenceWordDtos()
                        .stream()
                        .filter(words -> (salience.size()>0 && words.getSalience() == salience.get(0)))
                        .findFirst().orElse(null);


                String x = sentence.getSentence().replaceAll("It", eligibleEntity.getText());
                sentence.setSentence(x);
            }
            System.out.println(sentence.getSentence());


            String newSentence = "";
            if (sentence.getSentence().contains("and it")){
                String[] splittedArray = sentence.getSentence().split("and it");
                int counter = 0;
                for (String subSentence: splittedArray){//sub sentences analysis
                    //send the sentence again to gnlp and get the scores again
                    Map<String, List<String>> sentenceEntityAnalysisMap = null;
                    Document doc = Document.newBuilder().setContent(subSentence).setType(Document.Type.PLAIN_TEXT).build();
                    sentenceEntityAnalysisMap = entityExtractor.analyseEntity(languageServiceClient, doc);

                    List<SentenceWordDto> sentenceWordDtos = new LinkedList<>();
                    SentenceDto subSentenceDto = new SentenceDto();
                    subSentenceDto.setSentence(subSentence);
                    subSentenceDto.setTotalSalience(0f);
                    sentenceEntityAnalysisMap.forEach((key, value) -> {
                        SentenceWordDto sentenceWordDto = new SentenceWordDto();
                        sentenceWordDto.setText(value.get(0));
                        sentenceWordDto.setSentiment(Float.parseFloat(value.get(2)));
                        sentenceWordDto.setSalience(Float.parseFloat(value.get(3)));
                        sentenceWordDtos.add(sentenceWordDto);
                    });
                    subSentenceDto.setSentenceWordDtos(sentenceWordDtos);

                    List<Float> salience = new LinkedList<>();

                    subSentenceDto.getSentenceWordDtos().forEach(words -> {
                        if (subSentence.contains(words.getText())){
                            salience.add(words.getSalience());
                        }
                    });
                    Collections.sort(salience, Collections.reverseOrder());
                    SentenceWordDto eligibleEntity = subSentenceDto.getSentenceWordDtos()
                            .stream()
                            .filter(words -> (salience.size()>0 && words.getSalience() == salience.get(0)))
                            .findFirst().orElse(null);

                    String subSentenceString = "";
                    if ((splittedArray.length-1) != counter){
                        subSentenceString = subSentence.concat("and ");
                        newSentence = newSentence.concat(subSentenceString);
                        splittedArray[++counter] = eligibleEntity.getText().concat(splittedArray[counter]);
                    } else {
                        subSentenceString = subSentence;
                        newSentence = newSentence.concat(subSentenceString);
                    }
                }
                System.out.println(newSentence);
                replacedSentenceList.add(newSentence);
                sentence.setSentence(newSentence);
            } else {
                replacedSentenceList.add(sentence.getSentence());
            }
            sentenceCount++;
        }

        return replacedSentenceList;
    }

}