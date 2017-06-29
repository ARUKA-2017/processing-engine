package akura.corenlp;

import java.util.List;
import java.util.Properties;

import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.AnalyzedText;
import com.textrazor.annotations.Entity;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.util.CoreMap;

public class TokenExtraction {
	private final static String API_KEY = "5bfb19a823dfc6ff4b7f5cbf232a1086f64e3f3d429fe429e4919b29";
	static Properties props = new Properties();
	static Annotation document;
	public static void extractTokens(String paraphrase){
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    document = new Annotation(paraphrase);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    System.out.println(sentences.toString());
	    for(CoreMap cm : sentences){
	    	textRazorOperation(cm.toString());
//	    	System.out.println(cm.toString());
	    }
	}
	
	
	public static void textRazorOperation(String text){
		TextRazor textRazor = new TextRazor(API_KEY);
		textRazor.addExtractor("words");
		textRazor.addExtractor("entities");
//		String temp = "The new iPhone 7 is unlocked and will work with all major telecom networks worldwide. Buy from Acebeach. The phones are in stock right now for immediate delivery.";
		String temp = text;
		try {
			AnalyzedText response = textRazor.analyze(temp);
			System.out.println(response.getResponse().getNounPhrases());
			for(Entity entity : response.getResponse().getEntities()){
				System.out.println(entity.getEntityId());
			}
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		} catch (AnalysisException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	
	public static void main(String[] args){
		extractTokens("Item Received today and I dont understand if I have purchased an iPhone or an Android. It brings Android software and it looks very very cheap. Will add photos for you to all can see it. Just looking them its enough to understand its not an iPhone7 .It works very slow, the language is randomly changing between apps, it doesnt even have a good battery. The serial number in the phone its not the same that the the one on the box. Its not fair, I will find a way to return it because I dont live at USA. ABSOLUTELY NOT RECCOMENDED, DO NOT BUY THIS ITEM.");
		
	}
}
