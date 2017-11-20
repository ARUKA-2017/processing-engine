package akura.cloundnlp;

import akura.cloundnlp.dtos.FinalEntityTagDto;
import akura.cloundnlp.dtos.MobileDataSet;
import akura.cloundnlp.dtos.SpecificationDto;
import akura.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
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

    public SpecificationDto extractDomainsFromSentenceSyntax(List<FinalEntityTagDto> finalEntityTagDtoList){
        for (FinalEntityTagDto finalEntityTagDto: finalEntityTagDtoList){
            specMap.forEach((s, strings) -> {
                if (s.equals(finalEntityTagDto.getCategory())
                        || s.equals(finalEntityTagDto.getNounCombinationCategory())){
                    strings.add(finalEntityTagDto.getText());
                    specMap.replace(s, strings);
                }
            });
        }
        specificationDto.setMainEntity(this.findMainEntity(finalEntityTagDtoList));//should change it from the main method
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

    //get entities and match with the phone dataset inside the resources folder and get its mapped feature set in addition to the original feature set extracted from the nlp processes
    public String findMainEntity(List<FinalEntityTagDto> finalEntityTagDtoList){
        List<FinalEntityTagDto> tmpFinalEntityTagDtoList = new LinkedList<>();
        List<MobileDataSet> mobileDataSetList = this.getPhoneDataList();

        for (FinalEntityTagDto finalEntityTagDto: finalEntityTagDtoList){
            if (finalEntityTagDto.getCategory().equalsIgnoreCase("CONSUMER_GOOD")
                    || finalEntityTagDto.getCategory().equalsIgnoreCase("ORGANIZATION")
                    || finalEntityTagDto.getNounCombinationCategory().equalsIgnoreCase("device")){

                for (MobileDataSet mobileDataSet: mobileDataSetList){
                    if (mobileDataSet.getName().toString().contains(finalEntityTagDto.getText())){
                        System.out.println(mobileDataSet.getName().toString());
                        tmpFinalEntityTagDtoList.add(finalEntityTagDto);
                        break;
                    }
                }
            }
        }
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(tmpFinalEntityTagDtoList));
        String mainEntity = "";
        if (tmpFinalEntityTagDtoList.get(tmpFinalEntityTagDtoList.size()-1).getText().length() >= tmpFinalEntityTagDtoList.get(tmpFinalEntityTagDtoList.size()-1).getNounCombination().length()){
            mainEntity = tmpFinalEntityTagDtoList.get(tmpFinalEntityTagDtoList.size()-1).getText();
        } else {
            mainEntity = tmpFinalEntityTagDtoList.get(tmpFinalEntityTagDtoList.size()-1).getNounCombination();
        }
        return mainEntity;
    }

    private List<MobileDataSet> getPhoneDataList(){
        JSONParser jsonParser = new JSONParser();
        List<MobileDataSet> mobileDataSetList = new LinkedList<>();
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader(System.getProperty("user.dir")+"/src/main/java/akura/cloundnlp/sample_resources/phone_dataset.json"));
           
            for (Object object: jsonArray){
                JSONObject jsonObject = (JSONObject) object;
                MobileDataSet mobileDataSet = new MobileDataSet();
                jsonObject.forEach((key, value) -> {

                    switch (key.toString()){
                        case "name": mobileDataSet.setName(value.toString());
                            break;
                        case "releaseYear": mobileDataSet.setReleaseYear(value.toString());
                            break;
                        case "volume_mm_3": mobileDataSet.setVolume_mm_3(value.toString());
                            break;
                        case "weight_g": mobileDataSet.setWeight_g(value.toString());
                            break;
                        case "screen_size_in": mobileDataSet.setScreen_size_in(value.toString());
                            break;
                        case "total_resolution": mobileDataSet.setTotal_resolution(value.toString());
                            break;
                        case "OSType": mobileDataSet.setOSType(value.toString());
                            break;
                        case "number_of_cores": mobileDataSet.setNumber_of_cores(value.toString());
                            break;
                        case "core_clock_rate_gHz": mobileDataSet.setCore_clock_rate_gHz(value.toString());
                            break;
                        case "ram": mobileDataSet.setRam(value.toString());
                            break;
                        case "primary_camera_MP": mobileDataSet.setPrimary_camera_MP(value.toString());
                            break;
                        default:
                    }
                });
                mobileDataSetList.add(mobileDataSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mobileDataSetList;
    }
}
