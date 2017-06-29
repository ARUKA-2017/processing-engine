package akura.ontology.schemaImplementation.Schemas;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class GeneralSchema {
	
	private int id;
	private String resource;
	private String entityName;
	private String customerName;
	private List<String> features;
	private String additional;
	private String comparision;
	
	private String staticJsonString;
	
	public void constructStaticJsonObject(){
		String[] featuresString = null;
		if(this.features!=null){
			featuresString = features.stream().toArray(String[]::new);
		}
		staticJsonString = "{'schema':{"
				+ "'id':'"+this.id+"',"
				+ "'resource':'"+this.resource+"',"
				+ "'customer':'"+this.customerName+"',"
				+ "'sentiment':'"+this.customerName+"',"
				+ "'additional':{'features':"+Arrays.toString(featuresString)+",'sentiment':'"+this.additional+"'},"
				+ "}}";
		try {
			JSONObject jsonpObject = new JSONObject(staticJsonString);
			String xml = XML.toString(jsonpObject);
			System.out.println(xml);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public String getAdditional() {
		return additional;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}

	public String getComparision() {
		return comparision;
	}

	public void setComparision(String comparision) {
		this.comparision = comparision;
	}

	public String getStaticJsonString() {
		return staticJsonString;
	}

	public void setStaticJsonString(String staticJsonString) {
		this.staticJsonString = staticJsonString;
	}
	
	
}
