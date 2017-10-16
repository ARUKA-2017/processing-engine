package akura.cloundnlp;

import akura.cloundnlp.dtos.SentenceDto;
import akura.cloundnlp.dtos.SentenceWordDto;
import akura.utility.APIConnection;
import akura.utility.Logger;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Relationship extractor class
 *
 */
public class RelationshipExtractor {
    private final static String REGEX = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";
    private EntityExtractor entityExtractor = new EntityExtractor();
    private LanguageServiceClient languageServiceClient;

    /**
     * Constructor
     */
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
     *
     * @param paragraph
     * @return
     */
    public List<String> sentenceTokenize(String paragraph) {
        List<String> sentenceList = new ArrayList<>();
        Matcher reMatcher = Pattern.compile(REGEX, Pattern.MULTILINE | Pattern.COMMENTS).matcher(paragraph);
        while (reMatcher.find()) {
            sentenceList.add(reMatcher.group());
        }
        System.out.println("----------------Sentence wise tokenization----------------");
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(sentenceList));
        return sentenceList;
    }

    /**
     * analyse syntax of sentence
     *
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
            SentenceDto sentenceDto = new SentenceDto();
            sentenceDto.setSentence(sentence);
            sentenceDto.setSentenceWordDtos(createDto(sentenceEntityAnalysisMap));
            double totalSalience = 0f;
            int counter = 0;
            for (SentenceWordDto sentenceWordDto : analyzedSentenceWordDtoList) {
                counter++;
                totalSalience = (float) totalSalience + sentenceWordDto.getSalience();
            }
            sentenceDto.setTotalSalience((float) totalSalience);
            analyzedSentenceDtoList.add(sentenceDto);
        });
        System.out.println("----------------Analyzed sentence list----------------");
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(analyzedSentenceDtoList));
        return analyzedSentenceDtoList;
    }

    /**
     * replace entities in a sentence where the phone and this phone is located
     *
     * @param entity
     * @param entityList
     * @return
     */
    public List<String> replaceEntityInSentences(String entity, List<String> entityList) {
        List<String> replacedSentenceList = new LinkedList<>();
        entityList.forEach(sentence -> {
            replacedSentenceList.add(
                    sentence
                            .replaceAll("(?i)the phone", entity)
                            .replaceAll("(?i)this phone", entity)
                            .replaceAll("(?i)this device", entity)
            );
        });


        Logger.Log("----------------Entity replaced sentence list(the phone, this phone, this device)----------------");
        Logger.Log(new GsonBuilder().setPrettyPrinting().create().toJson(replacedSentenceList));
        return replacedSentenceList;
    }

    /**
     * Replace "and it" and starting "it" with the main entity of the previous sentence or the paragraph
     *
     * @param sentenceList
     * @return
     */
    public List<String> replaceEntityInSentenceByITContext(List<SentenceDto> sentenceList) {
        List<String> replacedSentenceList = new LinkedList<>();
        int sentenceCount = 0;
        for (SentenceDto sentence : sentenceList) {
            if (sentenceCount > 0 && (sentence.getSentence().startsWith("It") || (sentence.getSentence().startsWith("it")))) {
                String prevSentence = sentenceList.get(sentenceCount - 1).getSentence();
                Document doc = Document.newBuilder().setContent(prevSentence).setType(Document.Type.PLAIN_TEXT).build();
                Map<String, List<String>> prevSentenceEntityAnalysisMap = entityExtractor.analyseEntity(languageServiceClient, doc);
                SentenceDto prevSentenceDto = new SentenceDto();
                prevSentenceDto.setSentence(prevSentence);
                prevSentenceDto.setTotalSalience(0f);
                prevSentenceDto.setSentenceWordDtos(createDto(prevSentenceEntityAnalysisMap));
                List<Float> salience = new LinkedList<>();
                prevSentenceDto.getSentenceWordDtos().forEach(words -> {
                    if (prevSentence.contains(words.getText())) {
                        salience.add(words.getSalience());
                    }
                });
                Collections.sort(salience, Collections.reverseOrder());
                SentenceWordDto eligibleEntity = prevSentenceDto.getSentenceWordDtos()
                        .stream()
                        .filter(words -> (salience.size() > 0 && words.getSalience() == salience.get(0)))
                        .findFirst().orElse(null);
                String x = sentence.getSentence().replaceAll("It", eligibleEntity.getText());
                sentence.setSentence(x);
            }
            String newSentence = "";
            if (sentence.getSentence().contains("and it")) {
                String[] splittedArray = sentence.getSentence().split("and it");
                int counter = 0;
                for (String subSentence : splittedArray) {
                    Map<String, List<String>> sentenceEntityAnalysisMap = null;
                    Document doc = Document.newBuilder().setContent(subSentence).setType(Document.Type.PLAIN_TEXT).build();
                    sentenceEntityAnalysisMap = entityExtractor.analyseEntity(languageServiceClient, doc);
                    SentenceDto subSentenceDto = new SentenceDto();
                    subSentenceDto.setSentence(subSentence);
                    subSentenceDto.setTotalSalience(0f);
                    subSentenceDto.setSentenceWordDtos(createDto(sentenceEntityAnalysisMap));
                    List<Float> salience = new LinkedList<>();
                    subSentenceDto.getSentenceWordDtos().forEach(words -> {
                        if (subSentence.contains(words.getText())) {
                            salience.add(words.getSalience());
                        }
                    });
                    Collections.sort(salience, Collections.reverseOrder());
                    SentenceWordDto eligibleEntity = subSentenceDto.getSentenceWordDtos()
                            .stream()
                            .filter(words -> (salience.size() > 0 && words.getSalience() == salience.get(0)))
                            .findFirst().orElse(null);
                    String subSentenceString = "";
                    if ((splittedArray.length - 1) != counter) {
                        subSentenceString = subSentence.concat("and ");
                        newSentence = newSentence.concat(subSentenceString);
                        splittedArray[++counter] = eligibleEntity.getText().concat(splittedArray[counter]);
                    } else {
                        subSentenceString = subSentence;
                        newSentence = newSentence.concat(subSentenceString);
                    }
                }
                replacedSentenceList.add(newSentence);
                sentence.setSentence(newSentence);
            } else {
                replacedSentenceList.add(sentence.getSentence());
            }
            sentenceCount++;
        }

        Logger.Log("----------------Entity replacement by IT context----------------");
        Logger.Log(new GsonBuilder().setPrettyPrinting().create().toJson(replacedSentenceList));
        return replacedSentenceList;
    }

    /**
     * Generate dtos' by given map
     * @param map
     * @return
     */
    private List<SentenceWordDto> createDto(Map<String, List<String>> map) {
        List<SentenceWordDto> sentenceWordDtos = new LinkedList<>();
        map.forEach((key, value) -> {
            SentenceWordDto prevSentenceWordDto = new SentenceWordDto();
            prevSentenceWordDto.setText(value.get(0));
            prevSentenceWordDto.setSentiment(Float.parseFloat(value.get(2)));
            prevSentenceWordDto.setSalience(Float.parseFloat(value.get(3)));
            sentenceWordDtos.add(prevSentenceWordDto);
        });

        Logger.Log("----------------Sentence list with word by word details----------------");
        Logger.Log(new GsonBuilder().setPrettyPrinting().create().toJson(sentenceWordDtos));

        return sentenceWordDtos;
    }

    /**
     * modify given paragraph with the defined rules
     * @param text
     * @param entity
     * @return
     */
    public List<String> executeModifier(String text, String entity) throws IOException {
        try {
            return replaceEntityInSentenceByITContext(
                    sentenceSyntaxAnalysis(
                            replaceEntityInSentences(
                                    entity,
                                    sentenceTokenize(text))
                    )
            );
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

}