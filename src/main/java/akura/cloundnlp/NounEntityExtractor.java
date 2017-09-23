package akura.cloundnlp;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NounEntityExtractor {

    public static  void main(String args[]) {

        JSONParser jsonParser = new JSONParser();
        JSONArray array = null;

        try {
            array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/SampleReviews.json"));
        } catch (Exception e) {

        }
    }

    public static Map<String, List<String>> getEntityTagsAccordingToNounCombinationsFromMSApi(Map<Integer, List<String>> data) {
        Map<String, List<String>> entityTags = new LinkedHashMap<>();
        String requestString = "";
        int continousNounCount = 0;

        List<Float> salience = new ArrayList<Float>();
        List<Float> sentimentScore = new ArrayList<Float>();

        for(Map.Entry<Integer, List<String>> entityRow: data.entrySet()) {
            String posTag = entityRow.getValue().get(1);

            if(posTag.equalsIgnoreCase("NOUN")) {

                requestString += (continousNounCount++ == 0) ? entityRow.getValue().get(0) : " " + entityRow.getValue().get(0);

                if(entityRow.getValue().size() > 3) {
                    sentimentScore.add(Float.parseFloat(entityRow.getValue().get(3)));
                }

                if(entityRow.getValue().size() > 4) {
                    salience.add(Float.parseFloat(entityRow.getValue().get(4)));
                }

                if(data.get(data.size()-1) == entityRow && continousNounCount > 1) {
                    entityTags.put(requestString, addEntityDetails(requestString, salience, sentimentScore));
                }

            } else {
                if(continousNounCount > 1) {
                    entityTags.put(requestString, addEntityDetails(requestString, salience, sentimentScore));
                }
                continousNounCount = 0;
                requestString = "";
                salience = new ArrayList<Float>();
                sentimentScore = new ArrayList<Float>();
            }
        }

        return entityTags;

    }

    private static float avgSalienceOrSentimentScore(List<Float> listData) {
        float sum = 0.0f;


        for(float value : listData) {
            sum += value;
        }

        return (listData.size() > 0) ? sum/listData.size() : 0.0f;
    }

    private static List<String>  addEntityDetails(String requestString,  List<Float> salience, List<Float> sentimentScore) {

        String tag = Extractor.understandShortWordConcept(requestString, "Not Found");

        List<String> entityDetails = new ArrayList<String>();
        entityDetails.add(tag);
        entityDetails.add(avgSalienceOrSentimentScore(salience) + "");
        entityDetails.add(avgSalienceOrSentimentScore(sentimentScore) + "");

        return entityDetails;

    }
}
