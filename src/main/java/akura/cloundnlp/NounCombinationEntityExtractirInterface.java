package akura.cloundnlp;

import java.util.List;
import java.util.Map;

public interface NounCombinationEntityExtractirInterface {
    /**
     * Method used to merge nouns and add tag.
     *
     * @param data
     * @return
     */
    Map<String, String> mergeNouns(Map<Integer, List<String>> data);
}
