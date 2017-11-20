package akura.cloundnlp;

import akura.cloundnlp.dtos.FinalEntityTagDto;
import akura.cloundnlp.dtos.MobileDataSet;
import akura.cloundnlp.dtos.SpecRelationshipDto;
import akura.cloundnlp.dtos.SpecificationDto;
import akura.utility.Logger;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class to extract several specification details of devices from a given text
 *
 */
public class SpecificationExtractor {
    private Map<String, List<String>> specMap = new HashMap<>();
    private SpecificationDto specificationDto = new SpecificationDto();
    Map<String, String> featureMap = new LinkedHashMap<>();
    private List<String> relationshipTypeList = new LinkedList<>();

    public SpecificationExtractor(){
        specMap.put("CONSUMER_GOOD", new LinkedList<>());
        specMap.put("ORGANIZATION", new LinkedList<>());
        specMap.put("output device", new LinkedList<>());
        specMap.put("parameter", new LinkedList<>());
        specMap.put("item", new LinkedList<>());
        specMap.put("color option", new LinkedList<>());
        specMap.put("factor", new LinkedList<>());
        specMap.put("feature", new LinkedList<>());
        specMap.put("spec", new LinkedList<>());
        specMap.put("device", new LinkedList<>());
        specMap.put("phone", new LinkedList<>());
        specMap.put("sensor", new LinkedList<>());
        specMap.put("component", new LinkedList<>());
        specMap.put("function", new LinkedList<>());

        relationshipTypeList.add("has a");
        relationshipTypeList.add("comes with a");
    }

    public SpecificationDto extractDomainsFromSentenceSyntax(List<FinalEntityTagDto> finalEntityTagDtoList, String review){
        for (FinalEntityTagDto finalEntityTagDto: finalEntityTagDtoList){
            specMap.forEach((s, strings) -> {
                if (s.equals(finalEntityTagDto.getCategory())
                        || s.equals(finalEntityTagDto.getNounCombinationCategory())){
                    strings.add(finalEntityTagDto.getText());
                    specMap.replace(s, strings);
                }
            });
        }
        List<FinalEntityTagDto> finalEntityTagDtos = this.findMainEntityAndRelativeEntities(finalEntityTagDtoList);

        FinalEntityTagDto mainEntity = ((finalEntityTagDtos != null && !finalEntityTagDtos.isEmpty())?finalEntityTagDtos.get(finalEntityTagDtos.size()-1):null);

        specificationDto.setMainEntity(mainEntity);
//        finalEntityTagDtos.remove(finalEntityTagDtos.get(finalEntityTagDtos.size()-1));

//        specificationDto.setRelativeEntityList(finalEntityTagDtos);
        //relative entity list specifies main entity is better than the other entities
        if (finalEntityTagDtos != null && !finalEntityTagDtos.isEmpty()){
            List<FinalEntityTagDto> tmpRelativeEntityList = new LinkedList<>();
            List<FinalEntityTagDto> relativeEntityList = finalEntityTagDtos;
//            relativeEntityList.remove(relativeEntityList.size()-1);
            List<MobileDataSet> mobileDataSets = this.getPhoneDataList();
            if (relativeEntityList != null && !relativeEntityList.isEmpty())
                for(FinalEntityTagDto finalEntityTagDto : relativeEntityList){
                    for (MobileDataSet mobileDataSet : mobileDataSets){
                        if ((finalEntityTagDto.getText().toLowerCase().equals(mobileDataSet.getName().toLowerCase())
                                && finalEntityTagDto.getText().toLowerCase().contains(mobileDataSet.getName().toLowerCase()))
                                || (finalEntityTagDto.getNounCombination().toLowerCase().equals(mobileDataSet.getName().toLowerCase())
                                && finalEntityTagDto.getNounCombination().toLowerCase().contains(mobileDataSet.getName().toLowerCase()))){
                            System.out.println("matched " + mobileDataSet.getName());
                            tmpRelativeEntityList.add(finalEntityTagDto);
                        }
                    }
                }

            specificationDto.setRelativeEntityList(tmpRelativeEntityList);
        }




        specMap.forEach((key, value) -> {
            if (key.equals("feature") || key.equals("factor") || key.equals("spec") || key.equals("item") || key.equals("sensor") || key.equals("component") || key.equals("function") || key.equals("output device")){
                value.forEach(s -> {
                    featureMap.put(s, key);
                });
            }
        });
        specificationDto.setFeatureMap(featureMap);
        //relationship list between specifications and entities
        List<SpecRelationshipDto> specRelationshipDtoList = getSpecificationRelationshipList(
//                "iPhone 6S and iPhone 6S Plus (stylized and marketed as iPhone 6s and iPhone 6s Plus) are smartphones designed, developed and marketed by Apple Inc. They were announced on September 9, 2015, at the Bill Graham Civic Auditorium in San Francisco by Apple CEO Tim Cook, with pre-orders beginning September 12 and official release on September 25, 2015. The iPhone 6S and 6S Plus were succeeded by the iPhone 7 and iPhone 7 Plus in September 2016. The iPhone 6S has a similar design to the 6 but updated hardware, including a strengthened chassis and upgraded system-on-chip, a 12-megapixel camera, improved fingerprint recognition sensor, and LTE Advanced support. The iPhone 6S also introduces a new hardware feature known as \\\"3D Touch\\\", which enables pressure-sensitive touch inputs. iPhone 6S had a mostly positive reception. While performance and camera quality were praised by most reviewers, the addition of 3D Touch was liked by one critic for the potential of entirely new interface interactions, but disliked by another critic for not providing users with an expected intuitive response before actually using the feature. The battery life was criticized, and one reviewer asserted that the phone's camera wasn't significantly better than the rest of the industry. The iPhone 6S set a new first-weekend sales record, selling 13 million models, up from 10 million for the iPhone 6 in the previous year. However, Apple saw its first-ever quarterly year-over-year decline in iPhone sales in the months after the launch, credited to a saturated smartphone market in Apple's biggest countries and a lack of iPhone purchases in developing countries.",
                review,
                finalEntityTagDtos,
                featureMap
        );
//        List<SpecRelationshipDto> specRelationshipDtoList = getSpecificationRelationshipList(review, finalEntityTagDtos, featureMap);

        specificationDto.setSpecRelationshipDtoList(specRelationshipDtoList);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(specRelationshipDtoList));
        Logger.Log("----------------Feature understanding----------------");
        Logger.Log(new GsonBuilder().setPrettyPrinting().create().toJson(specificationDto));
        return specificationDto;
    }

    public List<SpecRelationshipDto> getSpecificationRelationshipList(String reviewText, List<FinalEntityTagDto> finalEntityTagDtoList, Map<String, String> featureMap){
        List<SpecRelationshipDto> specRelationshipDtoList = new LinkedList<>();
        List<String> tokenizedSentenceList = RelationshipExtractor.sentenceTokenize(reviewText);

        for(String sentence: tokenizedSentenceList){
            for (Map.Entry<String, String> featureMapEntry: featureMap.entrySet()){
                if (sentence.contains(featureMapEntry.getKey())){
                    String[] featureSplit = sentence.split(featureMapEntry.getKey());
                    if (featureSplit[0].contains(" has a ")){
                        processSpecRelationship(
                                "has a",
                                finalEntityTagDtoList,
                                featureSplit,
                                specRelationshipDtoList,
                                featureMapEntry
                        );
                    } else if (featureSplit[0].contains(" comes with a ")){
                        processSpecRelationship(
                                "comes with a",
                                finalEntityTagDtoList,
                                featureSplit,
                                specRelationshipDtoList,
                                featureMapEntry
                        );
                    }
                }
            }
        }

        return specRelationshipDtoList;
    }

    private void processSpecRelationship(String relationship, List<FinalEntityTagDto> finalEntityTagDtoList, String[] featureSplit, List<SpecRelationshipDto> specRelationshipDtoList, Map.Entry<String, String> featureMapEntry){
        String[] relationshipSplit = featureSplit[0].split(relationship);
        finalEntityTagDtoList.forEach(finalEntityTagDto -> {

            if (Pattern.compile("\\b"+finalEntityTagDto.getText().toLowerCase()+"\\b").matcher(relationshipSplit[0].toLowerCase()).find()
                    || (!finalEntityTagDto.getNounCombination().toLowerCase().equals("")
                    && Pattern.compile("\\b"+finalEntityTagDto.getNounCombination().toLowerCase()+"\\b").matcher(relationshipSplit[0].toLowerCase()).find())){

                SpecRelationshipDto specRelationshipDto = new SpecRelationshipDto();
                Map<String, String> tmpFeatureMap = new HashMap<>();
                specRelationshipDto.setFinalEntityTagDto(finalEntityTagDto);
                specRelationshipDto.setRelationship(relationship);

                tmpFeatureMap.put(featureMapEntry.getKey(), featureMapEntry.getValue());
                specRelationshipDto.setFeatureMap(tmpFeatureMap);
                specRelationshipDtoList.add(specRelationshipDto);
            }
        });
    }

    //get entities and match with the phone dataset inside the resources folder and get its mapped feature set in addition to the original feature set extracted from the nlp processes
    public List<FinalEntityTagDto> findMainEntityAndRelativeEntities(List<FinalEntityTagDto> finalEntityTagDtoList){
        List<FinalEntityTagDto> tmpFinalEntityTagDtoList = new LinkedList<>();
        List<MobileDataSet> mobileDataSetList = this.getPhoneDataList();

        for (FinalEntityTagDto finalEntityTagDto: finalEntityTagDtoList){
            if (finalEntityTagDto.getCategory().equalsIgnoreCase("CONSUMER_GOOD")
                    || finalEntityTagDto.getCategory().equalsIgnoreCase("ORGANIZATION")
                    || finalEntityTagDto.getCategory().equalsIgnoreCase("OTHER")
                    || finalEntityTagDto.getNounCombinationCategory().equalsIgnoreCase("device")
                    || finalEntityTagDto.getNounCombinationCategory().equalsIgnoreCase("phone")){

                for (MobileDataSet mobileDataSet: mobileDataSetList){
                    if (mobileDataSet.getName().toLowerCase().toString().contains(finalEntityTagDto.getText().toLowerCase())){
                        System.out.println(mobileDataSet.getName().toString());
                        tmpFinalEntityTagDtoList.add(finalEntityTagDto);
                        break;
                    }
                }
            }
        }
        System.out.println("\ntemporary final entity tag dto list");
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(tmpFinalEntityTagDtoList));
        System.out.println();

        return tmpFinalEntityTagDtoList;
    }

    public static List<MobileDataSet> getPhoneDataList(){
        JSONParser jsonParser = new JSONParser();
        List<MobileDataSet> mobileDataSetList = new LinkedList<>();
        try {

            Path path = FileSystems.getDefault().getPath("src/main/java/akura/cloundnlp/sample_resources/phone_dataset.json");
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader(String.valueOf(path.toAbsolutePath())));

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
