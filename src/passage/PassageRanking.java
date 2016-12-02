package passage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.id.IndonesianStemmer;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class PassageRanking {
	
	private ArrayList<PassageModel>ranked;
	private ArrayList<PassageModel>root;
	private List<List<CoreLabel>>root1;
	String inlineXML;
	
	public PassageRanking (String inlineXML)
	{
		this.inlineXML = inlineXML;
	}
	
	public PassageRanking(ArrayList<PassageModel> root)
	{
		this.root = root;
	}
	
	public PassageRanking (List<List<CoreLabel>> root)
	{
		this.root1 = root;
	}
	
	
	public ArrayList<Pair<PassageModel,Double>> getTopRankedPassage (String query, int N) throws IOException
	{
		QueryFunction qf = new QueryFunction (query);
		String entity = qf.getEntity();
		ArrayList<String> token = qf.getQueryToken();
		int index = 0;
		ArrayList <Pair<Integer,Double>>score = new ArrayList<Pair<Integer,Double>>();
		
		for (PassageModel e:root)
		{
			int dokPosition = e.getDocumentRank();
			System.out.println(dokPosition);
			double rule1=0,rule2=0,rule3=0,rule4=0,rule5=0,total=0;
			index++;
			String tag = "<"+entity+">";
			if (e.XMLcontains(tag))
			{
				rule1 = 5;
			}
			//rule2 = e.wordCountInPassageXML(tag) * 0.25;
			if (e.wordCountInPassageXML(tag) > 1)
			{
				rule2 = 0.5;
			}
			if (dokPosition <=5)
			{
				rule3 = 1;
			}
			rule4 = ((double)e.queryWordCountInPassage(token)/(double)token.size());
			/*if (e.queryWordCountInPassage(token) > (token.size()/2))
			{
				rule4 = 1;
			}*/
			if (e.checkQueryWordSequenceInPassage(token))
			{
				rule5 = 1;
			}
			total = rule1+rule2+rule3+rule4+rule5;
			score.add(new MutablePair <Integer,Double>(index,total));
		}
		Collections.sort(score,Comparator.comparing(p -> -p.getRight()));
		ArrayList<Pair<PassageModel,Double>> result = new ArrayList<Pair<PassageModel,Double>>();
		for (int i=0;i<score.size();i++)
		{
			System.out.println ("Passage"+score.get(i).getLeft()+" "+score.get(i).getRight());
		}
		for (int i=0;i<N;i++)
		{
			if (i < score.size())
			{
				result.add(new MutablePair <PassageModel,Double>(root.get(score.get(i).getLeft()-1),score.get(i).getRight()));
			}
			
		}
		return result;
	}
	
	/*public void getTopRankedPassage(int N) throws FileNotFoundException, UnsupportedEncodingException
	{
		String serializedClassifier = "classifier/ner-model.ser.gz";
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		PrintWriter write = new PrintWriter ("tes.txt","UTF-8");
		for (PassageModel e : root)
		{
			String allPassage = e.toString();
			System.out.println(e.getSentence(1));
			List<List<CoreLabel>> out = classifier.classify(allPassage);
			for (List<CoreLabel> sentence:out)
			{
				for (CoreLabel word:sentence)
				{
					write.print(word.word()+" ");
				}
				write.println();
			}
		}
		write.close();
		
		
	}*/

}
