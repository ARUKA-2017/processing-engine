package akura.service;

import akura.cloundnlp.EntityExtractor;
import akura.cloundnlp.RelationshipExtractor;
import akura.cloundnlp.EntityExtractorInterface;
import akura.cloundnlp.RelationshipExtractorInterface;
import akura.cloundnlp.dtos.OntologyMapDto;

import java.io.IOException;
import java.util.List;

/**
 * Entity extractor service class
 */
public class EntityExtractorService {

    private EntityExtractorInterface entityExtractor = new EntityExtractor();

    private RelationshipExtractorInterface relationshipExtractor = new RelationshipExtractor();

    public List<OntologyMapDto> extractEntity(String text, String entity) throws Exception {
        return entityExtractor.extractEntityData(text, entity);
    }

    public List<String> modifiedSentenceList(String text, String entity) {
        List<String> resultList = null;
        try{
            resultList = relationshipExtractor.executeModifier(text, entity);
        } catch (IOException e) {
            System.out.println("IO EXCEPTION: "+e.getLocalizedMessage());
        }
        return resultList;
    }
}
