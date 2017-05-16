package akura.ontology;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Model;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.github.andrewoma.dexx.collection.ArrayList;
import com.thoughtworks.xstream.XStream;
import akura.ontology.schemaImplementation.SchemaToXml;
import akura.ontology.schemaImplementation.Schemas.GeneralSchema;

public class TestRun {
	private static OntologyFactory ontologyFactory;
	private static JenaInterface jenaInterface;
    private static SchemaToXml schemaToXml;

	public static void main(String[] args){
		ontologyFactory = new OntologyFactoryImpl();
		jenaInterface = ontologyFactory.create();
		//generation of schema
		
		GeneralSchema schema = new GeneralSchema();
		schema.setId(1);
		schema.setResource("https://www.amazon.com/Amazon-Echo-Bluetooth-Speaker-with-Alexa-Black/dp/");
		schema.setEntityName("IPhone 7");
		schema.setCustomerName("Nilesh");
		
		List<String> list = new LinkedList<>();
		list.add("Camera");
		schema.setFeatures(list);
		
		schema.setAdditional("NEGATIVE on Feature-camera");
		schema.setComparision("Better than IPhone6");

		schema.constructStaticJsonObject();

		System.out.println("\n\n\n");
		//ontology model development
		String str = "{'schema':{'id':'1','resource':'https://www.amazon.com/Amazon-Echo-Bluetooth-Speaker-with-Alexa-Black/dp/B00X4WHP5E/ref=redir_mobile_desktop?_encoding=UTF8&ref_=ods_gw_ha_d_black','customer':'null','features':'Camera,Bluetooth'}}";  
		try {
			JSONObject jsonpObject = new JSONObject(str);
			String xml = XML.toString(jsonpObject);
			System.out.println("XML");
			System.out.println(xml);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	    
	}
}
