package akura.cloundnlp;

import akura.utility.APIConnection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an entity extractor from nouns.
 */
public class NounEntityExtractor {

    /**
     * Get entity tags with salience and sentiment.
     * @param data - data to be processed.
     * @return
     */
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

    /**
     * Method used to get average salience or sentiment score.
     * @param listData - listed data.
     * @return
     */
    private static float avgSalienceOrSentimentScore(List<Float> listData) {
        float sum = 0.0f;

        for(float value : listData) {
            sum += value;
        }

        return (listData.size() > 0) ? sum/listData.size() : 0.0f;
    }

    /**
     * Method used to add entity details.
     * @param requestString
     * @param salience
     * @param sentimentScore
     * @return
     */
    private static List<String>  addEntityDetails(String requestString,  List<Float> salience, List<Float> sentimentScore) {

        String tag = APIConnection.understandShortWordConcept(requestString, "Not Found");

        List<String> entityDetails = new ArrayList<String>();
        entityDetails.add(tag);
        entityDetails.add(avgSalienceOrSentimentScore(salience) + "");
        entityDetails.add(avgSalienceOrSentimentScore(sentimentScore) + "");

        return entityDetails;

    }

    /**
     * Method used to merge nouns and add tag.
     * @param data
     * @return
     */
    public static Map<String, String>  mergeNouns(Map<Integer, List<String>> data) {
        Map<String, String> entityTags = new LinkedHashMap<>();
        String requestString = "";
        int continousNounCount = 0;

        for(Map.Entry<Integer, List<String>> entityRow: data.entrySet()) {
            String posTag = entityRow.getValue().get(1);

            if(posTag.equalsIgnoreCase("NOUN")) {

                requestString += (continousNounCount++ == 0) ? entityRow.getValue().get(0) : " " + entityRow.getValue().get(0);

                if(data.get(data.size()-1) == entityRow && continousNounCount > 0) {
                    String tag = APIConnection.understandShortWordConcept(requestString, "Not Found");
                    entityTags.put(requestString, tag);
                }

            } else {
                if(continousNounCount > 0) {
                        String tag = APIConnection.understandShortWordConcept(requestString, "Not Found");
                        entityTags.put(requestString, tag);
                    }
                continousNounCount = 0;
                requestString = "";
            }
        }

        System.out.println(entityTags);
        return entityTags;
    }
}
