package suggestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;

import com.tutorialspoint.lucene.LuceneConstants;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class SuggestionSearch {
	
	Document topDocument;
	
	public SuggestionSearch(Document topDocument)
	{
		this.topDocument = topDocument;
	}
	
	public String getSuggestions (String query) throws ClassCastException, ClassNotFoundException, IOException
	{
		String[] queryToken = query.split(" ");
		String documentText = topDocument.get(LuceneConstants.TEXT);
		String serializedClassifier = "classifier/sejarahIndonesia-model.ser.gz";
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifier(serializedClassifier);
		List<List<CoreLabel>> out = classifier.classify(documentText);
		String querySuggestion = "";
		for (int i=0;i<queryToken.length;i++)
		{
			String temp="";
			String check = queryToken[i].toLowerCase();
			if (check.equals("dimana") || check.equals("apa") || check.equals("kapan") || check.equals("siapa") || check.equals("siapakah") || check.equals("dimana") || check.equals("apakah"))
			{
				querySuggestion = querySuggestion + queryToken[i]+" ";
			}
			else
			{
				ArrayList<Pair<Integer,String>> suggestionList = new ArrayList<Pair<Integer,String>>(); 
				for (List<CoreLabel> sentence:out)
				{
					
					for (int j=0;j<sentence.size();j++)
					{
						int distance = StringUtils.getLevenshteinDistance(sentence.get(j).originalText(), queryToken[i]);
						if ( distance < 2)
						{
							suggestionList.add(new MutablePair(distance,sentence.get(j).originalText()));
						}
					}
				}
				if (suggestionList.isEmpty())
				{
					querySuggestion = querySuggestion+ queryToken[i]+" ";
				}
				else
				{
					Collections.sort(suggestionList);
					querySuggestion = querySuggestion+suggestionList.get(0).getRight()+" ";
				}
			}
			
			
		}
		return querySuggestion;
		
	}

}
