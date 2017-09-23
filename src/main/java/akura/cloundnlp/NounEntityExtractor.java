package akura.cloundnlp;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
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

    public static Map<String, String> getEntityTagsAccordingToNounCombinationsFromMSApi(Map<Integer, List<String>> data) {
        Map<String, String> entityTags = new LinkedHashMap<>();
        String requestString = "";
        int continousNounCount = 0;

        for(Map.Entry<Integer, List<String>> entityRow: data.entrySet()) {
            String posTag = entityRow.getValue().get(1);

            if(posTag.equalsIgnoreCase("NOUN")) {

                requestString += (continousNounCount++ == 0) ? entityRow.getValue().get(0) : " " + entityRow.getValue().get(0);

                if(data.get(data.size()-1) == entityRow && continousNounCount > 1) {
                    String tag = Extractor.understandShortWordConcept(requestString, "Not Found");
                    entityTags.put(requestString, tag);
                }

            } else {
                if(continousNounCount > 1) {
                    String tag = Extractor.understandShortWordConcept(requestString, "Not Found");
                    entityTags.put(requestString, tag);
                }
                continousNounCount = 0;
                requestString = "";
            }
        }

        return entityTags;

    }
}
