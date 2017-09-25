package akura.Service;

import akura.cloundnlp.Extractor;
import akura.cloundnlp.OntologyMapDto;
import spark.Response;

import java.util.List;

public class EntityExtractorService {

    Extractor extractor = new Extractor();
    public List<OntologyMapDto> extractEntity(String text) throws Exception {
        System.out.println();
        System.out.println("EXTRACT ENTITY : "+text);
        System.out.println();
        return extractor.testEndpoint(text);
    }
}
