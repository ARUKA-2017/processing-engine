package akura.service;

import akura.cloundnlp.EntityExtractor;
import akura.cloundnlp.RelationshipExtractor;
import akura.cloundnlp.dtos.OntologyMapDto;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Entity extractor service class
 */
public class EntityExtractorService {

    private EntityExtractor entityExtractor = new EntityExtractor();
    private RelationshipExtractor relationshipExtractor = new RelationshipExtractor();

    public List<OntologyMapDto> extractEntity(String text) throws Exception {
        return entityExtractor.extractEntityData(text);
    }

    public List<String> modifiedSentenceList(String text, String entity) {
        try {
            return relationshipExtractor.replaceEntityInSentenceByITContext(
                    relationshipExtractor.sentenceSyntaxAnalysis(
                            relationshipExtractor.replaceEntityInSentences(
                                    entity,
                                    relationshipExtractor.sentenceTokenize(text))
                    )
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
