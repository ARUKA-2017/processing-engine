package akura.service;

import akura.cloundnlp.Extractor;
import akura.cloundnlp.dtos.OntologyMapDto;

import java.util.List;

public class EntityExtractorService {

    private Extractor extractor = new Extractor();

    public List<OntologyMapDto> extractEntity(String text) throws Exception {

        return extractor.extractEntityData(text);
    }
}
