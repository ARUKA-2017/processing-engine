package akura.service;

import akura.cloundnlp.EntityExtractor;
import akura.cloundnlp.dtos.OntologyMapDto;

import java.util.List;

public class EntityExtractorService {

    private EntityExtractor entityExtractor = new EntityExtractor();

    public List<OntologyMapDto> extractEntity(String text) throws Exception {

        return entityExtractor.extractEntityData(text);
    }
}
