package answerProcessing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import core.JSastrawiStemmer;

public class Util {
	
	public static ArrayList<String> tokenWithoutTag;
	public static ArrayList<String> tokenWithTagStemmed;
	public static ArrayList<String> tokenWithoutTagStemmed;
	static JSastrawiStemmer stemmer;
	static{
		stemmer = new JSastrawiStemmer();
	}
	
	public static String stem (String token)
	{
		return stemmer.stem(token);
	}
	
	
	public static ArrayList<String> combineTagWithTag (ArrayList<String> token)
	{
		ArrayList<String> newToken = new ArrayList<String>();
		boolean enterTag = false;
		String concater = "";
		for (int i=0;i<token.size();i++)
		{
			if (token.get(i).equals("location") || token.get(i).equals("time") || token.get(i).equals("person"))
			{
				if (!enterTag)
				{
					enterTag = true;
					concater = "<"+token.get(i).toUpperCase()+">";
				}
				else
				{
					concater = concater.trim();
					concater = concater + "</"+token.get(i).toUpperCase()+">";
					newToken.add(concater);
					concater="";
					enterTag = false;
				}
				
			}
			else
			{
				if (enterTag)
				{
					concater = concater + token.get(i)+" ";
				}
				else
				{
					newToken.add(token.get(i));
				}
			}
		}
		return newToken;
	}
	
	public static ArrayList<String> combineTag(ArrayList<String> token)
	{
		ArrayList<String> newToken = new ArrayList<String>();
		boolean enterTag = false;
		String concater = "";
		for (int i=0;i<token.size();i++)
		{
			if (token.get(i).equals("location") || token.get(i).equals("time") || token.get(i).equals("person"))
			{
				if (!enterTag)
				{
					enterTag = true;
				}
				else
				{
					concater = concater.trim();
					newToken.add(concater);
					concater="";
					enterTag = false;
				}
				
			}
			else
			{
				if (enterTag)
				{
					concater = concater + token.get(i)+" ";
				}
				else
				{
					newToken.add(token.get(i));
				}
			}
		}
		return newToken;
	}
	
	public static void splitWithTagAndNoTag (String token)
	{
		Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(token));
		TokenStream result = new StandardFilter (Version.LUCENE_36,source);
		result = new LowerCaseFilter (Version.LUCENE_36,result);
		IndonesianAnalyzer analyze = new IndonesianAnalyzer (Version.LUCENE_36);
		result = new StopFilter (Version.LUCENE_36,result,analyze.getStopwordSet());
		ArrayList<String> allToken = new ArrayList<String>();
		tokenWithoutTag = new ArrayList<String>();
		tokenWithTagStemmed = new ArrayList<String>();
		tokenWithoutTagStemmed = new ArrayList<String>();
		try {
			while (result.incrementToken())
			{
				allToken.add(result.getAttribute(CharTermAttribute.class).toString());
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean enterTag = false;
		String concater = "";
		for (String tok : allToken)
		{
			if (tok.equalsIgnoreCase("location") || tok.equalsIgnoreCase("time") || tok.equalsIgnoreCase("person"))
			{
				if (!enterTag)
				{
					enterTag = true;
				}
				else
				{
					concater = concater.trim();
					tokenWithoutTag.add(concater);
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
					tokenWithoutTag.add(tok);
				}
			}
		}
		enterTag = false;
		concater = "";
		for (String tok : allToken)
		{
			if (tok.equalsIgnoreCase("location") || tok.equalsIgnoreCase("time") || tok.equalsIgnoreCase("person"))
			{
				if (!enterTag)
				{
					enterTag = true;
					concater = "<"+tok.toUpperCase()+">";
				}
				else
				{
					concater = concater.trim();
					concater = concater + "</"+tok.toUpperCase()+">";
					tokenWithTagStemmed.add(concater);
					concater="";
					enterTag = false;
				}
				
			}
			else
			{
				if (enterTag)
				{
					concater = concater + stem(tok)+" ";
				}
				else
				{
					tokenWithTagStemmed.add(stem(tok));
				}
			}
		}
		//tokenStream = analyze.reusableTokenStream(null, new StringReader (token));
		enterTag = false;
		concater = "";
		for (String tok:allToken)
		{
			if (tok.equalsIgnoreCase("location") || tok.equalsIgnoreCase("time") || tok.equalsIgnoreCase("person"))
			{
				if (!enterTag)
				{
					enterTag = true;
				}
				else
				{
					concater = concater.trim();
					tokenWithoutTagStemmed.add(concater);
					concater="";
					enterTag = false;
				}
				
			}
			else
			{
				if (enterTag)
				{
					concater = concater + stem(tok)+" ";
				}
				else
				{
					tokenWithoutTagStemmed.add(stem(tok));
				}
			}
		}
		
	}
	
	/*public static void splitWithTagAndNoTag (String token)
	{
		Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(token));
		TokenStream result = new StandardFilter (Version.LUCENE_36,source);
		result = new LowerCaseFilter (Version.LUCENE_36,result);
		IndonesianAnalyzer analyze = new IndonesianAnalyzer (Version.LUCENE_36);
		result = new StopFilter (Version.LUCENE_36,result,analyze.getStopwordSet());
		tokenWithoutTag = new ArrayList<String>();
		tokenWithTagStemmed = new ArrayList<String>();
		tokenWithoutTagStemmed = new ArrayList<String>();
		TokenStream tokenStream =
		null;
		try
		{
			tokenStream = analyze.reusableTokenStream(null,new StringReader (token));
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
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
						tokenWithoutTag.add(concater);
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
						tokenWithoutTag.add(tok);
					}
				}
			}
			enterTag = false;
			concater = "";
			while (tokenStream.incrementToken())
			{
				String tok = tokenStream.getAttribute(CharTermAttribute.class).toString();
				if (tok.equalsIgnoreCase("location") || tok.equalsIgnoreCase("time") || tok.equalsIgnoreCase("person"))
				{
					if (!enterTag)
					{
						enterTag = true;
						concater = "<"+tok.toUpperCase()+">";
					}
					else
					{
						concater = concater.trim();
						concater = concater + "</"+tok.toUpperCase()+">";
						tokenWithTagStemmed.add(concater);
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
						tokenWithTagStemmed.add(tok);
					}
				}
			}
			tokenStream.reset();
			tokenStream = analyze.reusableTokenStream(null, new StringReader (token));
			enterTag = false;
			concater = "";
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
						tokenWithoutTagStemmed.add(concater);
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
						tokenWithoutTagStemmed.add(tok);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public static ArrayList<String> split (String token)
	{
		Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(token));
		TokenStream result = new StandardFilter (Version.LUCENE_36,source);
		result = new LowerCaseFilter (Version.LUCENE_36,result);
		ArrayList<String> splittedToken = new ArrayList<String>();
		try {
			while (result.incrementToken())
			{
				splittedToken.add(result.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		splittedToken = combineTag(splittedToken);
		return splittedToken;
	}
	
	public static ArrayList<String> splitWithTag (String token)
	{
		Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(token));
		TokenStream result = new StandardFilter (Version.LUCENE_36,source);
		result = new LowerCaseFilter (Version.LUCENE_36,result);
		ArrayList<String> splittedToken = new ArrayList<String>();
		try {
			while (result.incrementToken())
			{
				splittedToken.add(result.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		splittedToken = combineTagWithTag(splittedToken);
		return splittedToken;
	}
	
	public static void main (String[] Args)
	{
		String tes = "halo semua <LOCATION>hahaha jijijiji</LOCATION> huuhuu mengajarkan diselenggarakan jijij  jfkdsjfksjd <TIME>skdfjskdf</TIME> ksjdfkjs kfsdjkfjals ksjflkad";
		Util.splitWithTagAndNoTag(tes);
		for (int i=0;i<tokenWithoutTag.size();i++)
		{
			System.out.println(tokenWithoutTag.get(i));
		}
		System.out.println("masuk2");
		for (int i=0;i<tokenWithTagStemmed.size();i++)
		{
			System.out.println(tokenWithTagStemmed.get(i));
		}
		for (int i=0;i<tokenWithoutTagStemmed.size();i++)
		{
			System.out.println(tokenWithoutTagStemmed.get(i));
		}
	}

}
