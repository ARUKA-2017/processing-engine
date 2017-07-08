package akura.corenlp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class TokenExtraction {
	private final static String API_KEY = "5bfb19a823dfc6ff4b7f5cbf232a1086f64e3f3d429fe429e4919b29";

	private final static String DELIMETER = ".";
	private final static String[] CONJUNCTIONS = {"greater than", "better than"};
	private final static String[] DEVICE_LIST = {"samsung galaxy s8", "iphone 7", "samsung galaxy s8", "iphone 7s", "samsung galaxy s6", "iphone 6s",};

	private final static String NNP = "NNP";
	private final static String NN = "NN";
	private final static String VBD = "VBD";
	private final static String VBP = "VBP";
	private final static String VB = "VB";
	private final static String VBN = "VBN";
	private final static String CC = "CC";
	private final static String PRP = "PRP";
	private final static String IN = "IN";
	
	private final static String DT = "DT";
	private final static String VBZ = "VBZ";
	private final static String RB = "RB";
	private final static String JJ = "JJ";
	private final static String MD = "MD";
	private final static String NNS = "NNS";
	private final static String TO = "TO";
	private final static String VBG = "VBG";
	private final static String PRP$ = "PRP$";
	private final static String CD = "CD";
	
	
	private static Map<String, Map<String, List<String>>> sentenceMap = new HashMap<>();
	private static Map<String, List<String>> wordMap;
	private static List<String> nnpList, nnList, vbdList, vbpList, vbList, vbnList, ccList, prpList, inList, dtList, vbzList, rbList, jjList, mdList, nnsList , toList, vbgList, prp$List, cdList;
	
	static Properties props = new Properties();
	static Annotation document;
	public static Map<String, Map<String, List<String>>> extractTokens(String paraphrase){
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    document = new Annotation(paraphrase);
	    pipeline.annotate(document);
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence : sentences){
	    	wordMap = new HashMap<>();
	    	nnpList = new LinkedList<>();
	    	nnList = new LinkedList<>();
	    	vbdList = new LinkedList<>();
	    	vbpList = new LinkedList<>();
	    	vbList = new LinkedList<>();
	    	vbnList = new LinkedList<>();
	    	ccList = new LinkedList<>();
	    	prpList = new LinkedList<>();
	    	inList = new LinkedList<>();
	    	dtList = new LinkedList<>();
	    	vbzList = new LinkedList<>();
	    	rbList = new LinkedList<>();
	    	jjList = new LinkedList<>();
	    	mdList = new LinkedList<>();
	    	nnsList = new LinkedList<>();
	    	toList = new LinkedList<>();
	    	vbgList = new LinkedList<>();
	    	prp$List = new LinkedList<>();
	    	cdList = new LinkedList<>();
	    	for(CoreLabel token: sentence.get(TokensAnnotation.class)){
	//	    	System.out.println(sentence.toString());
		    	String word = token.get(TextAnnotation.class);
		        // this is the POS tag of the token
		        String pos = token.get(PartOfSpeechAnnotation.class);
		        // this is the NER label of the token
		        String ne = token.get(NamedEntityTagAnnotation.class);
		        
		        storeExtractedData(pos, word);
	    	}
	    	
	    	wordMap.put(NNP, nnpList);
	    	wordMap.put(NN, nnList);
	    	wordMap.put(VBD, vbdList);
	    	wordMap.put(VBP, vbpList);
	    	wordMap.put(VB, vbList);
	    	wordMap.put(VBN, vbnList);
	    	wordMap.put(CC, ccList);
	    	wordMap.put(PRP, prpList);
	    	wordMap.put(IN, inList);
	    	wordMap.put(DT, dtList);
	    	wordMap.put(VBZ, vbzList);
	    	wordMap.put(RB, rbList);
	    	wordMap.put(JJ, jjList);
	    	wordMap.put(MD, mdList);
	    	wordMap.put(NNS, nnsList);
	    	wordMap.put(TO, toList);
	    	wordMap.put(VBG, vbgList);
	    	wordMap.put(PRP$, prp$List);
	    	wordMap.put(CD, cdList);
	    	
	    	sentenceMap.put(sentence.toString(), wordMap);
	    }
//	    System.out.println(sentenceMap.toString());
//	    for(Map.Entry<String, Map<String, List<String>>> entry: sentenceMap.entrySet()){
//	    	System.out.println("Sentence : "+entry.getKey());
//
//	    	for(Map.Entry<String, List<String>> inner : entry.getValue().entrySet()){
//	    		System.out.println(inner.getKey()+" => "+inner.getValue());
//	    	}
//	    	System.out.println();
//	    }
	    return sentenceMap;
	}
	
	private static void storeExtractedData(String pos, String word){
		switch (pos){
			case "NNP":nnpList.add(word);
			break;
			case "NN":nnList.add(word);
			break;
			case "VBD":vbdList.add(word);
			break;
			case "VBP":vbpList.add(word);
			break;
			case "VB":vbList.add(word);
			break;
			case "VBN":vbnList.add(word);
			break;
			case "CC":ccList.add(word);
			break;
			case "PRP":prpList.add(word);
			break;
			case "IN":inList.add(word);
			break;
			case "DT":dtList.add(word);
			break;
			case "VBZ":vbzList.add(word);
			break;
			case "RB":rbList.add(word);
			break;
			case "JJ":jjList.add(word);
			break;
			case "MD":mdList.add(word);
			break;
			case "NNS":nnsList.add(word);
			break;
			case "TO":toList.add(word);
			break;
			case "VBG":vbgList.add(word);
			break;
			case "PRP$":prp$List.add(word);
			break;
			case "CD":cdList.add(word);
			break;
			default:System.out.println();
		}
	}
	
	private static double generateSingleSentenceScore(String sentence){
		String[] sentenceArray = sentence.split("\\.");
		String[] conjuncArray;
		for(int i = 0; i < sentenceArray.length; i++){
			for (int x = 0; x < CONJUNCTIONS.length; x++){

				if(sentenceArray[i].contains(CONJUNCTIONS[x])){
					conjuncArray = sentenceArray[i].split(CONJUNCTIONS[x]);
					for (int y = 0; y < conjuncArray.length; y++){
						String conjunction = CONJUNCTIONS[x];
						//have to set a score to the conjunction word----> -10 - 0 - +10
						String entityModel = getMatchedEntity(extractTokens(conjuncArray[y]));
					}
				}

			}
			break;
		}

		return 0;
	}

	private static String getMatchedEntity(Map<String, Map<String, List<String>>> extractedTokenMap){
		for(Map.Entry<String, Map<String, List<String>>> entry: sentenceMap.entrySet()){
			System.out.println("Sentence : "+entry.getKey());

			for(Map.Entry<String, List<String>> inner : entry.getValue().entrySet()){
				System.out.println(inner.getKey()+" => "+inner.getValue());
			}
			System.out.println();
		}
		return "";
	}
	
	public static void main(String[] args){
//		extractTokens("Samsung Galaxy S8 is a good phone and it is better than IPhone7. The camera of Samsung Galaxy S8 is perfect than the IPhone7.");
		generateSingleSentenceScore("Samsung Galaxy S8 is better than IPhone7. The camera of Samsung Galaxy S8 is perfect than the IPhone7.");

	}
	
	
//	public static void textRazorOperation(String text){
//	TextRazor textRazor = new TextRazor(API_KEY);
//	textRazor.addExtractor("words");
//	textRazor.addExtractor("entities");
////	String temp = "The new iPhone 7 is unlocked and will work with all major telecom networks worldwide. Buy from Acebeach. The phones are in stock right now for immediate delivery.";
//	String temp = text;
//	try {
//		AnalyzedText response = textRazor.analyze(temp);
//		System.out.println(response.getResponse().getNounPhrases());
//		for(Entity entity : response.getResponse().getEntities()){
//			System.out.println(entity.getEntityId());
//		}
//	} catch (NetworkException e) {
//		// TODO Auto-generated catch block
//		System.out.println(e.getLocalizedMessage());
//	} catch (AnalysisException e) {
//		// TODO Auto-generated catch block
//		System.out.println(e.getLocalizedMessage());
//	}
//}
}
