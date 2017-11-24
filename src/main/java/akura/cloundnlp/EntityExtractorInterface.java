package akura.cloundnlp;

import akura.cloundnlp.dtos.FinalEntityTagDto;
import akura.cloundnlp.dtos.OntologyMapDto;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface EntityExtractorInterface {
    /**
     * To identify the review category of a given review
     *
     * @param text
     * @param languageServiceClient
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    Map<String, Float> identifyReviewCategory(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException;


    /**
     * analyse a given document(paragraph) and identify possible entities from google nlp according to their role play inside the paragraph and output and entity map with entity, category, sentiment and sailience
     *
     * @param languageApi
     * @param doc
     * @return
     */
    Map<String, List<String>> analyseEntity(LanguageServiceClient languageApi, Document doc);

    /**
     * analyse a given document(paragraph) and output syntax tag map and final entity tag map
     *
     * @param text
     * @param languageServiceClient
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    Map<String, Map<Integer, List<String>>> analyseSyntax(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException;

    /**
     * construct json using the dto list
     *
     * @param review
     * @param categoryMap
     * @param outputMap
     * @return
     */
    OntologyMapDto constructJson(JSONObject review, Map<String, Float> categoryMap, Map<String, Map<Integer, List<String>>> outputMap);

    /**
     * prioritize the entity map according to the salience of entities
     *
     * @param finalEntityTagDtos
     */
    List<FinalEntityTagDto> prioritizeEntities(List<FinalEntityTagDto> finalEntityTagDtos);

    /**
     * calculate avg scores from the redundant data entities
     *
     * @param finalEntityTagDtos
     * @return
     */
    List<FinalEntityTagDto> constructAvgScores(List<FinalEntityTagDto> finalEntityTagDtos);

    /**
     * write output to a json document - output.json
     *
     * @param ontologyMapDtos
     * @throws IOException
     */
    void writeDocumentOutput(List<OntologyMapDto> ontologyMapDtos) throws IOException;

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
    List<OntologyMapDto> extractEntityData(String text, String entity);

    /**
     * Endpoint - extracted entity data
     *
     * @return
     */
    List<OntologyMapDto> extractEntityData(String searchKeyWord);
}
