package akura.ontology;

public class OntologyFactoryImpl implements OntologyFactory{
	private JenaInterface jenaInterface;
	
	public JenaInterface create() {
		this.jenaInterface = new JenaImplementation();
		return this.jenaInterface;
	}

}
