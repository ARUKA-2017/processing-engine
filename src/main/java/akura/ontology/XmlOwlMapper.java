package akura.ontology;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Model;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.andrewoma.dexx.collection.ArrayList;

public class XmlOwlMapper {
	private DocumentBuilderFactory dbf = null;
    private DocumentBuilder db = null;
    private Document document = null;
    
	private Model model = null;
	private int no = 0;
	private Map<String, Integer> count = null;
	private String opprefic = Constants.DEFAULT_OBP_PREFIX;
	private String dtpprefix = Constants.DEFAULT_DTP_PREFIX;

	private String baseURI = Constants.ONTMALIZER_INSTANCE_BASE_URI;
	private String baseNS = Constants.ONTMALIZER_INSTANCE_BASE_NS;
    
	private ArrayList<OntClass> mixedClasses = null;
	private String ns = null;
    private String nsPrefix = null;
    
    public XmlOwlMapper(File xmlFile, XsdOwlMapper mapper){
    	initDocumentBuilder();
    	try {
			document = db.parse(xmlFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	initializeEnvironment(mapper);
    }
    
    public XmlOwlMapper(InputStream xmlInputStream, XsdOwlMapper mapping) {
        try {
            initDocumentBuilder();
            document = db.parse(xmlInputStream);
        } catch (SAXException e) {
            
        } catch (IOException e) {
			e.printStackTrace();
		}
        initializeEnvironment(mapping);
    }

	private void initializeEnvironment(XsdOwlMapper mapper) {
		// TODO Auto-generated method stub
		
	}

	private void initDocumentBuilder() {
		// TODO Auto-generated method stub
		
	}
}
