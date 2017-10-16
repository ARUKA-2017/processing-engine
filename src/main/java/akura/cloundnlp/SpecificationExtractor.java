package akura.cloundnlp;

import akura.cloundnlp.dtos.FinalEntityTagDto;
import akura.cloundnlp.dtos.SpecificationDto;
import akura.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * Class to extract several specification details of devices from a given text
 *
 */
public class SpecificationExtractor {
    private Map<String, List<String>> specMap = new HashMap<>();
    private SpecificationDto specificationDto = new SpecificationDto();
    Map<String, String> featureMap = new LinkedHashMap<>();

    public SpecificationExtractor(){
        specMap.put("CONSUMER_GOOD", new LinkedList<>());
        specMap.put("output device", new LinkedList<>());
        specMap.put("parameter", new LinkedList<>());
        specMap.put("item", new LinkedList<>());
        specMap.put("color option", new LinkedList<>());
        specMap.put("factor", new LinkedList<>());
        specMap.put("feature", new LinkedList<>());
        specMap.put("spec", new LinkedList<>());
        specMap.put("device", new LinkedList<>());
    }

    public SpecificationDto extractDomainsFromSentenceSyntax(List<FinalEntityTagDto> finalEntityTagDtoList, String mainEntity){
        for (FinalEntityTagDto finalEntityTagDto: finalEntityTagDtoList){
            specMap.forEach((s, strings) -> {
                if (s.equals(finalEntityTagDto.getCategory())
                        || s.equals(finalEntityTagDto.getNounCombinationCategory())){
                    strings.add(finalEntityTagDto.getText());
                    specMap.replace(s, strings);
                }
            });
        }
        specificationDto.setMainEntity(mainEntity);//should change it from the main method
        specMap.forEach((key, value) -> {
            if (key.equals("feature") || key.equals("factor") || key.equals("spec")){
                value.forEach(s -> {
                    featureMap.put(s, key);
                });
            }
        });
        specificationDto.setFeatureMap(featureMap);


        Logger.Log("----------------Feature understanding----------------");
        Logger.Log(new GsonBuilder().setPrettyPrinting().create().toJson(specificationDto));
        return specificationDto;
    }
}
