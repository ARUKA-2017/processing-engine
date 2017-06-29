package akura.ontology;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.propertytable.lang.CSV2RDF;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import akura.ontology.schemaImplementation.SchemaToXml;
import akura.ontology.schemaImplementation.Schemas.GeneralSchema;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.apache.jena.vocabulary.RDFSyntax.doc;

public class TestRun {
	private static OntologyFactory ontologyFactory;
	private static JenaInterface jenaInterface;
    private static SchemaToXml schemaToXml;

	private static XPathFactory xPathFactory = null;
	private static DocumentBuilderFactory domFactory = null;

	public static void main(String args[]) {
		CSV2RDF.init();

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
		list.add("Bluetooth");
		schema.setFeatures(list);

		schema.setAdditional("NEGATIVE on Feature-camera");
		schema.setComparision("Better than IPhone6");

		schema.constructStaticJsonObject();

		//ontology model development
		/*
		String str = "{'schema':{'id':'1','resource':'https://www.amazon.com/Amazon-Echo-Bluetooth-Speaker-with-Alexa-Black/dp/B00X4WHP5E/ref=redir_mobile_desktop?_encoding=UTF8&ref_=ods_gw_ha_d_black','customer':'null','features':'Camera,Bluetooth'}}";
		try {
			JSONObject jsonpObject = new JSONObject(str);
			String xml = XML.toString(jsonpObject);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		xPathFactory = XPathFactory.newInstance();
		TransformerFactory.newInstance();

		ReadXML();

		Model model = ModelFactory.createModelForGraph(new GraphCSV("test.csv"));
		*/

	}

	public static void ReadXML() {
		System.out.println("In ReadXML method");
		File xmlFile = new File("/Users/sameerap/Documents/SLIIT/4th year/1st semester/CDAP/processing-engine/src/main/java/akura/ontology/test.xml");
		try {
			InputStream fis = new FileInputStream(xmlFile);
			if (fis != null) {
				Document xmlDoc = getDocFromXMLString(fis);
				HashMap<String, String> propertiesKeypair = readPropertyFile();
				FileWriter writer = new FileWriter("/Users/sameerap/Documents/SLIIT/4th year/1st semester/CDAP/processing-engine/src/main/java/akura/ontology/test.csv");
				writer.append("Key");
				writer.append(',');
				writer.append("Value");
				writer.append('\n');

				for (Map.Entry<String, String> entry : propertiesKeypair
						.entrySet()) {
					System.out.println("Key : " + entry.getKey()
							+ "Xpath value is::"
							+ getElementValue(entry.getValue(), xmlDoc));

					writer.append(entry.getKey());
					writer.append(',');
					writer.append(getElementValue(entry.getValue(), xmlDoc));
					writer.append('\n');
				}
				writer.flush();
				writer.close();
				System.out
						.println("ResultMap Updated. CSV File is being generated...");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Document getDocFromXMLString(InputStream xml)
			throws Exception {
		DocumentBuilder builder;
		Document doc;
		try {
			builder = domFactory.newDocumentBuilder();
			doc = builder.parse(xml);
		} catch (Exception exception) {
			throw exception;
		} finally {
		}
		return doc;
	}

	public static String getElementValue(final String xpathExpression,
										 final Document doc) {

		String textValue = null;
		try {
			XPath xpath = xPathFactory.newXPath();
			textValue = xpath.evaluate(xpathExpression, doc);
		} catch (final XPathExpressionException xpathException) {
			xpathException.printStackTrace();
		}

		return textValue;
	}

	public static HashMap<String, String> readPropertyFile() {
		System.out.println("In readPropertyFile method");
		Properties prop = new Properties();
		InputStream input;
		HashMap<String, String> Propvals = new HashMap<String, String>();
		try {

			input = new FileInputStream("/Users/sameerap/Documents/SLIIT/4th year/1st semester/CDAP/processing-engine/src/main/java/akura/ontology/JustProperties.properties");
			System.out.println("before load");
			prop.load(input);
			System.out.println("Property File Loaded Succesfully");
			Set<String> propertyNames = prop.stringPropertyNames();
			for (String Property : propertyNames) {
				Propvals.put(Property, prop.getProperty(Property));
			}
			System.out.println("HashMap generated::" + Propvals);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Propvals;
	}
}
