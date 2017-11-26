package akura.cloundnlp;

import akura.cloundnlp.dtos.*;
import akura.utility.APIConnection;

import akura.utility.Logger;
import com.google.cloud.language.v1beta2.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.io.FileReader;
import java.io.IOException;

import java.security.GeneralSecurityException;
import java.util.*;

/**
 * A snippet for Google Cloud Speech API showing how to analyze text message sentiment.
 */
public class EntityExtractor implements EntityExtractorInterface {
    private static OntologyMapDto ontologyMapDto;
    private static LanguageServiceClient languageServiceClient;

    /**
     *
     * Entity extraction methods sequence
     *
     */

    /**
     * To identify the review category of a given review
     *
     * @param text
     * @param languageServiceClient
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Map<String, Float> identifyReviewCategory(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException {
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
                .setDocument(doc)
                .build();
        ClassifyTextResponse response = languageServiceClient.classifyText(request);
        Map<String, Float> categoryMap = new LinkedHashMap<>();
        for (ClassificationCategory category : response.getCategoriesList()) {
            categoryMap.put(category.getName().split("/")[1], category.getConfidence());
            break;
        }
        return categoryMap;
    }

    /**
     * analyse a given document(paragraph) and identify possible entities from google nlp according to their role play inside the paragraph and output and entity map with entity, category, sentiment and sailience
     *
     * @param languageApi
     * @param doc
     * @return
     */
    public Map<String, List<String>> analyseEntity(LanguageServiceClient languageApi, Document doc) {
        Map<String, List<String>> entityList = new HashMap<>();
        AnalyzeEntitySentimentRequest request = AnalyzeEntitySentimentRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();
        AnalyzeEntitySentimentResponse response = languageApi.analyzeEntitySentiment(request);
        for (Entity entity : response.getEntitiesList()) {
            List<String> detailList = new LinkedList<>();
            detailList.add(entity.getName());
            detailList.add(entity.getType().name());
            detailList.add(String.valueOf(entity.getSentiment().getScore()));
            detailList.add(String.valueOf(entity.getSalience()));
            entityList.put(UUID.randomUUID().toString(), detailList);
        }
        return entityList;
    }

    /**
     * analyse a given document(paragraph) and output syntax tag map and final entity tag map
     *
     * @param text
     * @param languageServiceClient
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Map<String, Map<Integer, List<String>>> analyseSyntax(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException {
        Map<String, Map<Integer, List<String>>> outputMap = new LinkedHashMap<>();
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        Map<String, List<String>> entitiesFound = analyseEntity(languageServiceClient, doc);
        AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();
        AnalyzeSyntaxResponse response = languageServiceClient.analyzeSyntax(request);
        Map<Integer, List<String>> syntaxTagMap = new LinkedHashMap<>();
        int counter = 0;
        for (Token token : response.getTokensList()) {
            List<String> tokenTags = new LinkedList<>();
            tokenTags.add(token.getText().getContent());
            tokenTags.add(token.getPartOfSpeech().getTag().toString());
            tokenTags.add(token.getLemma());
            syntaxTagMap.put(++counter, tokenTags);
        }
        Map<String, String> mergedNouns = new NounCombinationEntityExtractor().mergeNouns(syntaxTagMap);

        Logger.Log("#TITLE-STEP 3: Identify noun combination categories through ProBase");
        Logger.Log("#JSON-".concat(new Gson().toJson(mergedNouns)));

        Map<Integer, List<String>> finalEntityTaggedMap = new LinkedHashMap<>();
        counter = 0;
        for (Map.Entry<String, List<String>> entityRow : entitiesFound.entrySet()) {
            List<String> temporaryEntityDetailList = new LinkedList<>();
            String entity = entityRow.getValue().get(0);
            String organization = entityRow.getValue().get(1);
            String sentiment = ((entityRow.getValue().size() > 2) ? (entityRow.getValue().get(2)) : "");
            String sailience = ((entityRow.getValue().size() > 3) ? (entityRow.getValue().get(3)) : "");
            temporaryEntityDetailList.add(entity);
            temporaryEntityDetailList.add(sentiment);
            temporaryEntityDetailList.add(sailience);
            temporaryEntityDetailList.add(organization);
            for (Map.Entry<String, String> nounEntry : mergedNouns.entrySet()) {
                if (nounEntry.getKey().toString().contains(entity)) {
                    temporaryEntityDetailList.add(nounEntry.getKey());
                    temporaryEntityDetailList.add(nounEntry.getValue());
                    break;
                }
            }
            finalEntityTaggedMap.put(++counter, temporaryEntityDetailList);
        }
        Logger.Log("#TITLE-STEP 4: Construct Syntax Tags");
        Logger.Log("#SUB- Tags included->test,pos tag,lemmatization tag");
        Logger.Log("#JSON-".concat(new Gson().toJson(syntaxTagMap)));
        Logger.Log("#TITLE-STEP 5: Construct Entity Tags");
        Logger.Log("#SUB- Tags included->text,sentiment,salience,category,noun combination,noun combination category");
        Logger.Log("#JSON-".concat(new Gson().toJson(finalEntityTaggedMap)));

        outputMap.put("syntaxTagMap", syntaxTagMap);
        outputMap.put("finalEntityTaggedMap", finalEntityTaggedMap);
        return outputMap;
    }

    /**
     * construct json using the dto list
     *
     * @param review
     * @param categoryMap
     * @param outputMap
     * @return
     */
    public OntologyMapDto constructJson(JSONObject review, Map<String, Float> categoryMap, Map<String, Map<Integer, List<String>>> outputMap) {
        ontologyMapDto = new OntologyMapDto();
        ontologyMapDto.setReviewId(review.get("review_id").toString());
        ontologyMapDto.setReview(review.get("reviewContent").toString());
        ontologyMapDto.setReviewRating((review.get("rating").equals("N/A")) ? 0 : Float.parseFloat(review.get("rating").toString()));
        ontologyMapDto.setCategoryMap(categoryMap);

        Logger.Log("#TITLE-STEP 6: Identify Sentence/Paragraph domain");
        Logger.Log("#JSON-".concat(new Gson().toJson(categoryMap)));

        List<SyntaxDto> syntaxDtos = new LinkedList<>();
        List<FinalEntityTagDto> finalEntityTagDtos = new LinkedList<>();
        for (Map.Entry<String, Map<Integer, List<String>>> entry : outputMap.entrySet()) {
            if (entry.getKey().equals("syntaxTagMap")) {
                for (Map.Entry<Integer, List<String>> subEntry : entry.getValue().entrySet()) {
                    SyntaxDto syntaxDto = new SyntaxDto();
                    syntaxDto.setText(subEntry.getValue().get(0));
                    syntaxDto.setPos(subEntry.getValue().get(1));
                    syntaxDto.setLemma(subEntry.getValue().get(2));
                    syntaxDtos.add(syntaxDto);
                }
            } else if (entry.getKey().equals("finalEntityTaggedMap")) {
                for (Map.Entry<Integer, List<String>> subEntry : entry.getValue().entrySet()) {
                    FinalEntityTagDto finalEntityTagDto = new FinalEntityTagDto();
                    finalEntityTagDto.setText(subEntry.getValue().get(0));
                    finalEntityTagDto.setSentiment(Float.parseFloat(subEntry.getValue().get(1)));
                    finalEntityTagDto.setSalience(Float.parseFloat(subEntry.getValue().get(2)));
                    finalEntityTagDto.setCategory((subEntry.getValue().size() > 3) ? subEntry.getValue().get(3) : "");
                    finalEntityTagDto.setNounCombination((subEntry.getValue().size() > 4) ? subEntry.getValue().get(4) : "");
                    String text = subEntry.getValue().get(0);
                    String nounCombination = (subEntry.getValue().size() > 4) ? subEntry.getValue().get(4) : "";
                    String nounCombinationCategory = (subEntry.getValue().size() > 5) ? subEntry.getValue().get(5) : "";
                    if (nounCombinationCategory.equalsIgnoreCase("not found") && !text.equalsIgnoreCase(nounCombination)) {
                        finalEntityTagDto.setNounCombination(text);
                        finalEntityTagDto.setNounCombinationCategory(APIConnection.understandShortWordConcept(text, "Not Found"));
                    } else {
                        finalEntityTagDto.setNounCombinationCategory((subEntry.getValue().size() > 5) ? subEntry.getValue().get(5) : "");
                    }
                    finalEntityTagDtos.add(finalEntityTagDto);
                }
            }
        }
        ontologyMapDto.setSyntaxTagList(syntaxDtos);
        ontologyMapDto.setFinalEntityTaggedList(constructAvgScores(prioritizeEntities(finalEntityTagDtos)));

        SpecificationExtractorInterface specificationExtractor = new SpecificationExtractor();
        SpecificationDto specificationDto = specificationExtractor.extractDomainsFromSentenceSyntax(ontologyMapDto.getFinalEntityTaggedList(), ontologyMapDto.getReview());
        ontologyMapDto.setSpecificationDto(specificationDto);

        return ontologyMapDto;
    }

    /**
     * prioritize the entity map according to the salience of entities
     *
     * @param finalEntityTagDtos
     */
    public List<FinalEntityTagDto> prioritizeEntities(List<FinalEntityTagDto> finalEntityTagDtos) {
        Collections.sort(finalEntityTagDtos, (object1, object2) -> (int) (object1.getSalience() * 10000 - object2.getSalience() * 10000));
        return finalEntityTagDtos;
    }

    /**
     * calculate avg scores from the redundant data entities
     *
     * @param finalEntityTagDtos
     * @return
     */
    public List<FinalEntityTagDto> constructAvgScores(List<FinalEntityTagDto> finalEntityTagDtos) {
        List<FinalEntityTagDto> outputDtoList = new LinkedList<>();
        Iterator<FinalEntityTagDto> iterator = finalEntityTagDtos.iterator();
        while (iterator.hasNext()) {
            FinalEntityTagDto finalEntityTagDto = iterator.next();
            FinalEntityTagDto temporaryDto = new FinalEntityTagDto();
            String entityName = finalEntityTagDto.getText();
            String entityCategory = finalEntityTagDto.getCategory();
            String nounCombination = finalEntityTagDto.getNounCombination();
            String nounCombinationCategory = finalEntityTagDto.getNounCombinationCategory();
            float sentiment = finalEntityTagDto.getSentiment();
            float salience = finalEntityTagDto.getSalience();
            iterator.remove();
            int counter = 1;
            while (iterator.hasNext()) {
                FinalEntityTagDto finalEntityTagDto1 = iterator.next();
                if (entityName.equalsIgnoreCase(finalEntityTagDto1.getText()) || (nounCombination.equalsIgnoreCase(finalEntityTagDto1.getNounCombination()) && !nounCombination.equals("") && !finalEntityTagDto1.getNounCombination().equals(""))) {
                    counter++;
                    sentiment = (sentiment + finalEntityTagDto1.getSentiment());
                    salience = (salience + finalEntityTagDto1.getSalience());
                    iterator.remove();
                }
            }

            temporaryDto.setText(entityName);
            temporaryDto.setCategory(entityCategory);
            temporaryDto.setSentiment(sentiment / counter);
            temporaryDto.setSalience(salience / counter);
            temporaryDto.setNounCombination(nounCombination);
            temporaryDto.setNounCombinationCategory(nounCombinationCategory);
            outputDtoList.add(temporaryDto);
            iterator = finalEntityTagDtos.iterator();
        }
        Logger.Log("#TITLE-STEP 7: Prioritize entity list by salience");
        Logger.Log("#JSON-".concat(new Gson().toJson(outputDtoList)));
        return this.prioritizeEntities(outputDtoList);
    }

    /**
     * write output to a json document - output.json
     *
     * @param ontologyMapDtos
     * @throws IOException
     */
    public void writeDocumentOutput(List<OntologyMapDto> ontologyMapDtos) throws IOException {
        try (Writer writer = new FileWriter("Output.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(ontologyMapDtos, writer);
        }
    }

    /**
     * Get the main entity through the paragraph/sentence
     *
     * @param text
     * @return
     */
    public static String getMainSalienceEntity(String text) {
        try {
            languageServiceClient = APIConnection.provideLanguageServiceClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        AnalyzeEntitySentimentRequest request = AnalyzeEntitySentimentRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();
        AnalyzeEntitySentimentResponse response = languageServiceClient.analyzeEntitySentiment(request);

        List<MobileDataSet> mobileDataSetList = new SpecificationExtractor().getPhoneDataList();
        String mainEntity = "";
        for (Entity entity : response.getEntitiesList()) {
            System.out.println(entity.getName());
            for (MobileDataSet mobileDataSet : mobileDataSetList) {
                if (mobileDataSet.getName().toLowerCase().equals(entity.getName().toLowerCase())
                        && mobileDataSet.getName().toLowerCase().contains(entity.getName().toLowerCase())) {
                    Logger.Log("#TITLE-STEP 1: Main Entity Extraction");
                    Logger.Log("#CONT-".concat(entity.getName()));
                    return entity.getName();
                }
            }
        }
        Logger.Log("#TITLE-STEP 1: Main Entity Extraction");
        Logger.Log("#CONT-".concat(mainEntity));
        return mainEntity;
    }
    /**
     *
     * api endpoint method - test
     *
     */

    /**
     * Endpoint - extracted entity data
     *
     * @param text
     * @return
     */
    public List<OntologyMapDto> extractEntityData(String text, String entity) {
        try {
            languageServiceClient = APIConnection.provideLanguageServiceClient();
            List<String> replacedText = new RelationshipExtractor().executeModifier(text, entity);

            Logger.Log("#TITLE-STEP 2: Sentence modification");
            Logger.Log("#SUB- Replace and modify IT context of sentences by salience");
            Logger.Log("#JSON-".concat(new Gson().toJson(replacedText)));

            text = "";
            for (String newStr : replacedText) {
                text += " " + newStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("review_id", "N/A");
        jsonObject.put("reviewContent", text);
        jsonObject.put("mainEntity", entity);
        jsonObject.put("rating", "N/A");
        try {
            ontologyMapDtos.add(constructJson(jsonObject, identifyReviewCategory(text, languageServiceClient), analyseSyntax(text, languageServiceClient)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        Logger.Log("#TITLE-STEP 10: Construct Final JSON output structure");
        Logger.Log("#JSON-".concat(new Gson().toJson(ontologyMapDtos)));

        return ontologyMapDtos;
    }

    /**
     * Endpoint - extracted entity data
     *
     * @return
     */
    public List<OntologyMapDto> extractEntityData(String searchKeyWord) {
        try {
            languageServiceClient = APIConnection.provideLanguageServiceClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        JSONArray array = null;
        try {
            array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/SampleReviews.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
        for (Object object : array) {

            try {
                JSONObject jsonObject = (JSONObject) object;
                String text = jsonObject.get("reviewContent").toString();
                if (text.split(" ").length <= 20) continue;
                try {
                    List<String> replacedText = new RelationshipExtractor().executeModifier(text, searchKeyWord);//change
                    text = "";
                    for (String newStr : replacedText) {
                        text += " " + newStr;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                jsonObject.put("reviewContent", text);
                jsonObject.put("mainEntity", searchKeyWord);//change
                String sampleText = jsonObject.get("reviewContent").toString();
                try {
                    ontologyMapDtos.add(constructJson(jsonObject, identifyReviewCategory(sampleText, languageServiceClient), analyseSyntax(sampleText, languageServiceClient)));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ontologyMapDtos));
        return ontologyMapDtos;
    }
}