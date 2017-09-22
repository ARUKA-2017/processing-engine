package akura.corenlp;

import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import gate.*;
import gate.creole.ANNIEConstants;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * Created by Nipuna H Herath on 7/9/17.
 */
public class SentimentAnalyzer {
    static StanfordCoreNLP pipeline;

    public static void init() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse, sentiment, relation");
        pipeline = new StanfordCoreNLP(props);
    }

    public static double findSentiment(String tweet) {
        double mainSentiment = 0;
        if (tweet != null && tweet.length() > 0) {
            double longest = 0;
            Annotation annotation = pipeline.process(tweet);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentAnnotatedTree.class);
                System.out.println(tree);
                double sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                SimpleMatrix sentiment_new = RNNCoreAnnotations.getPredictions(tree);
                String partText = sentence.toString();

                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        return mainSentiment;
    }

    public static void main(String[] args) throws GateException, IOException {
        Gate.setGateHome(new File("/Applications/GATE_Developer_8.4.1"));
        Gate.init();
        LanguageAnalyser controller = (LanguageAnalyser) PersistenceManager
                .loadObjectFromFile(new File(new File(Gate.getPluginsHome(),
                        ANNIEConstants.PLUGIN_DIR), ANNIEConstants.DEFAULT_FILE));

        Corpus corpus = Factory.newCorpus("corpus");
        Document document = Factory.newDocument(
                "Michael Jordan is a professor at the University of California, Berkeley.");
        corpus.add(document); controller.setCorpus(corpus);
        controller.execute();

//        document.getAnnotations().get(new HashSet<>(Arrays.asList("Person", "Organization", "Location")))
//                .forEach(a -> System.err.format("%s - \"%s\" [%d to %d]\n",
//                        a.getType(), Utils.stringFor(document, a),
//                        a.getStartNode().getOffset(), a.getEndNode().getOffset()));

        //Don't forget to release GATE resources
        Factory.deleteResource(document);
        Factory.deleteResource(corpus);
        Factory.deleteResource(controller);
    }
}
