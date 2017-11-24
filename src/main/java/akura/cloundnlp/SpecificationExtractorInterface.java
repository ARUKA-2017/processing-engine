package akura.cloundnlp;

import akura.cloundnlp.dtos.FinalEntityTagDto;
import akura.cloundnlp.dtos.MobileDataSet;
import akura.cloundnlp.dtos.SpecRelationshipDto;
import akura.cloundnlp.dtos.SpecificationDto;

import java.util.List;
import java.util.Map;

public interface SpecificationExtractorInterface {
    /**
     * Extraction of domain according to the sentence
     *
     * @param finalEntityTagDtoList
     * @param review
     * @return
     */
    SpecificationDto extractDomainsFromSentenceSyntax(List<FinalEntityTagDto> finalEntityTagDtoList, String review);

    /**
     * Retrieve spec result list
     *
     * @param reviewText
     * @param finalEntityTagDtoList
     * @param featureMap
     * @return
     */
    List<SpecRelationshipDto> getSpecificationRelationshipList(String reviewText, List<FinalEntityTagDto> finalEntityTagDtoList, Map<String, String> featureMap);

    /**
     * identify main entity and the relative entities
     *
     * @param finalEntityTagDtoList
     * @return
     */
    List<FinalEntityTagDto> findMainEntityAndRelativeEntities(List<FinalEntityTagDto> finalEntityTagDtoList);

    /**
     * Mobile Device Name Corpus Provider
     *
     * @return
     */
    public List<MobileDataSet> getPhoneDataList();
}
