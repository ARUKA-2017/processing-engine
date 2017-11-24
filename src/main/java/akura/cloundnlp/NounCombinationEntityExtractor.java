package akura.cloundnlp;

import akura.utility.APIConnection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an entity extractor from nouns.
 */
public class NounCombinationEntityExtractor implements NounCombinationEntityExtractirInterface{

    /**
     * Method used to merge nouns and add tag.
     *
     * @param data
     * @return
     */
    public Map<String, String> mergeNouns(Map<Integer, List<String>> data) {
        Map<String, String> entityTags = new LinkedHashMap<>();
        String requestString = "";
        int adjacentNounCount = 0;
        for (Map.Entry<Integer, List<String>> entityRow : data.entrySet()) {
            String posTag = entityRow.getValue().get(1);
            if (posTag.equalsIgnoreCase("NOUN")) {
                requestString += (adjacentNounCount++ == 0) ? entityRow.getValue().get(0) : " " + entityRow.getValue().get(0);
                if (data.get(data.size() - 1) == entityRow && adjacentNounCount > 0) {
                    String tag = APIConnection.understandShortWordConcept(requestString, "Not Found");
                    entityTags.put(requestString, tag);
                }
            } else {
                if (adjacentNounCount > 0) {
                    String tag = APIConnection.understandShortWordConcept(requestString, "Not Found");
                    entityTags.put(requestString, tag);
                }
                adjacentNounCount = 0;
                requestString = "";
            }
        }

        return entityTags;
    }
}
