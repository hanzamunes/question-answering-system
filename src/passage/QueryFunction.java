package passage;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import com.tutorialspoint.lucene.StopWord;

import answerProcessing.Util;
import core.Utils;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class QueryFunction {

	String querys;
	
	public QueryFunction (String query)
	{
		this.querys = query;//.replaceAll("\\p{Punct}+", "");
	}
	
	public String getEntity ()
	{
		String query = querys;
		query = query.toLowerCase();
		String entity="";
		ArrayList<String> token = new ArrayList<String>(Arrays.asList(query.split(" ")));
		if (token.contains("siapa") || token.contains("siapakah") || token.contains("apa"))
		{
			entity = "PERSON";
		}
		else if (token.contains("dimana") || token.contains("darimana")|| token.contains("kemana"))
		{
			entity = "LOCATION";
		}
		else if (token.contains("kapan") || token.contains("berapa"))
		{
			entity = "TIME";
		}
		return entity;
	}
	
	public ArrayList<String> getQueryToken ()
	{
		String query = querys;
		String serializedClassifier = Utils.classifierPath;
		AbstractSequenceClassifier classifier = null;
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String queryXML = classifier.classifyWithInlineXML(query);
		ArrayList<String> token = tokenizeQueryWithXML (queryXML);
		
		/*PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(query),new CoreLabelTokenFactory(),"normalizeParentheses=false,normalizeOtherBrackets=false,asciiQuotes=true,unicodeQuotes=true");
		
		while (ptbt.hasNext())
		{
			char[] qu = ptbt.next().originalText().toLowerCase().toCharArray();
		       int le = tes.stem(qu, qu.length, true);
		       String out = new String (qu,0,le);
			token.add(out);
		}*/
		return token;
	}
	
	private ArrayList<String> tokenizeQueryWithXML (String query)
	{
		Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(query));
		TokenStream result = new StandardFilter (Version.LUCENE_36,source);
		result = new LowerCaseFilter (Version.LUCENE_36,result);
		IndonesianAnalyzer analyze = new IndonesianAnalyzer (Version.LUCENE_36);
		result = new StopFilter (Version.LUCENE_36,result,analyze.getStopwordSet());
		ArrayList<String> token = new ArrayList<String>();
		try
		{
			boolean enterTag = false;
			String concater = "";
			while (result.incrementToken())
			{
				String tok = result.getAttribute(CharTermAttribute.class).toString();
				if (tok.equalsIgnoreCase("location") || tok.equalsIgnoreCase("time") || tok.equalsIgnoreCase("person"))
				{
					if (!enterTag)
					{
						enterTag = true;
					}
					else
					{
						concater = concater.trim();
						token.add(concater);
						concater="";
						enterTag = false;
					}
					
				}
				else
				{
					if (enterTag)
					{
						concater = concater + Util.stem(tok)+" ";
					}
					else
					{
						token.add(Util.stem(tok));
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return token;
	}
	
	/*private ArrayList<String> tokenizeQueryWithXML (String query)
	{
		IndonesianAnalyzer analyze = new IndonesianAnalyzer (Version.LUCENE_36);
		TokenStream tokenStream = analyze.tokenStream(null, new StringReader (query));
		ArrayList<String> token = new ArrayList<String>();
		try {
			boolean enterTag = false;
			String concater = "";
			while (tokenStream.incrementToken())
			{
				String tok = tokenStream.getAttribute(CharTermAttribute.class).toString();
				if (tok.equalsIgnoreCase("location") || tok.equalsIgnoreCase("time") || tok.equalsIgnoreCase("person"))
				{
					if (!enterTag)
					{
						enterTag = true;
					}
					else
					{
						concater = concater.trim();
						token.add(concater);
						concater="";
						enterTag = false;
					}
					
				}
				else
				{
					if (enterTag)
					{
						concater = concater + tok+" ";
					}
					else
					{
						token.add(tok);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}*/
	
	private ArrayList<String> RemoveStopword(String query)
	{
		ArrayList<String> token = new ArrayList<String>();
		try {
			Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(query));
			TokenStream result = new StandardFilter (Version.LUCENE_36,source);
			result = new LowerCaseFilter (Version.LUCENE_36,result);
			result = new StopFilter (Version.LUCENE_36,result,new IndonesianAnalyzer(Version.LUCENE_36).getStopwordSet());
			while (result.incrementToken())
			{
				token.add(result.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}
	
	
	
	/*public static void main (String[] Args)
	{
		System.out.println(StringUtils.getLevenshteinDistance("rengas", "dengan"));
		String  tes = "dimana rengasdengklok terjadi";
		/*TokenStream tokenStream = new StandardTokenizer (Version.LUCENE_36,new StringReader (tes));
		ArrayList<String> token = new ArrayList<String>();
		try {
			while (tokenStream.incrementToken())
			{
				token.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		QueryFunction qf = new QueryFunction (tes);
		ArrayList<String> token = qf.getQueryToken();
		/*ArrayList<String> token = new ArrayList<String>();
		try {
			Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(tes));
			TokenStream result = new StandardFilter (Version.LUCENE_36,source);
			result = new LowerCaseFilter (Version.LUCENE_36,result);
			result = new StopFilter (Version.LUCENE_36,result,new IndonesianAnalyzer(Version.LUCENE_36).getStopwordSet());
			while (result.incrementToken())
			{
				token.add(result.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=0;i<token.size();i++)
		{
			System.out.println(token.get(i));
		}
		/*String queryXML = "halo <tes>semuanya</tes> apa kabar <AHA>balik</AHA> baik pak <ba>baguslah</ba>";
		String regex = "<(.+?)>(.+?)</(.+?)>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(queryXML);
		while (matcher.find())
		{
			String content = matcher.group(1);
			System.out.println(content);
		}
		
	}*/
}
