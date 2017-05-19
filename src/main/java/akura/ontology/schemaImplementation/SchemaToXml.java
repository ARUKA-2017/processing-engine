package akura.ontology.schemaImplementation;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import akura.ontology.schemaImplementation.Schemas.GeneralSchema;

public class SchemaToXml {
	File file = null;
	Marshaller marshaller = null;
	JAXBContext context;
	
	public SchemaToXml(GeneralSchema schema) {
		file = new File("akura/ontology/test.xml");

		try {
			context = JAXBContext.newInstance(GeneralSchema.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(schema, file);
			marshaller.marshal(schema, System.out);
		} catch (PropertyException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
