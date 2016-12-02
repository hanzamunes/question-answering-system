package passage;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.id.IndonesianStemmer;

import com.tutorialspoint.lucene.LuceneTester;

import answerProcessing.Util;
import longestCommonSubstring.LongestCommonSubstring;

public class PassageModel {
	
	ArrayList<String>Sentence;
	ArrayList<String>SentenceXML;
	IndonesianStemmer tes;
	int documentRank;
	String title;
	String documentPath;
	int passageNumber;
	
	
	
	/**
	 * @return the passageNumber
	 */
	public int getPassageNumber()
	{
		return passageNumber;
	}

	/**
	 * @param passageNumber the passageNumber to set
	 */
	public void setPassageNumber(int passageNumber)
	{
		this.passageNumber = passageNumber;
	}

	public static boolean contains(ArrayList<String> data,String search)
	{
		for (int i=0;i<data.size();i++)
		{
			if (search.equals(data.get(i)) || (StringUtils.getLevenshteinDistance(search, data.get(i)) < 2) )
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean contains (String data, String search)
	{
		String[] token = data.split(" ");
		for (int i=0;i<data.length();i++)
		{
			if (search.equals(token[i]) || StringUtils.getLevenshteinDistance(search, token[i])<2)
			{
				return true;
			}
		}
		return false;
	}
	
	public PassageModel()
	{
		Sentence = new ArrayList<String>();
		SentenceXML = new ArrayList<String>();
		documentRank = 0;
		title = "";
		documentPath = "";
	}
	
	
	
	


	/**
	 * @return the documentPath
	 */
	public String getDocumentPath() {
		return documentPath;
	}






	/**
	 * @param documentPath the documentPath to set
	 */
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}






	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}




	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}




	/**
	 * @return the documentRank
	 */
	public int getDocumentRank() {
		return documentRank;
	}




	/**
	 * @param documentRank the documentRank to set
	 */
	public void setDocumentRank(int documentRank) {
		this.documentRank = documentRank;
	}




	/**
	 * @return number of passage
	 */
	public int getPassageCount()
	{
		return Sentence.size();
	}
	
	/**
	 * @return number of passageXML
	 */
	public int getPassageXMLCount()
	{
		return SentenceXML.size();
	}

	/**
	 * @return the sentence
	 */
	public ArrayList<String> getSentenceAll() {
		return Sentence;
	}
	
	/**
	 * @return the sentence with XML
	 */
	public ArrayList<String> getSentenceXMLAll() {
		return SentenceXML;
	}
	
	/**
	 * @return the sentence with index
	 */
	public String getSentence(int index)
	{
		return Sentence.get(index);
	}
	
	/**
	 * @return the sentence XML with index
	 */
	public String getSentenceXML(int index)
	{
		return SentenceXML.get(index);
	}
	
	/**
	 * @param sentence take string as input and push it to model
	 */
	public void setSentence (String sentence)
	{
		/*tes = new IndonesianStemmer();
		String[] token = sentence.split(" ");
		String result = "";
		for (int i=0;i<token.length;i++)
		{
			char[] qu = token[i].toCharArray();
		    int le = tes.stem(qu, qu.length, true);
		    String out = new String (qu,0,le);
		    result = result + out;
		    if (i != token.length-1)
		    {
		    	result = result + " ";
		    }
		}*/
		
		Sentence.add(sentence);
	}
	
	/**
	 * @param sentence take string with XML as input and push it to model
	 */
	public void setSentenceXML (String sentence)
	{
		/*tes = new IndonesianStemmer();
		String[] token = sentence.split(" ");
		String result = "";
		for (int i=0;i<token.length;i++)
		{
			if (token[i].contains("<PERSON>") && !token[i].contains("</PERSON>"))
			{
				String temp = token[i].substring(8, token[i].length());
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = "<PERSON>"+out;
				
			}
			else if (token[i].contains("<LOCATION>") && !token[i].contains("</LOCATION>"))
			{
				String temp = token[i].substring(10, token[i].length());
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = "<LOCATION>"+out;
			}
			else if (token[i].contains("<TIME>") && !token[i].contains("</TIME>"))
			{
				String temp = token[i].substring(6, token[i].length());
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = "<TIME>"+out;
			}
			else if (token[i].contains("</PERSON>") && !token[i].contains("<PERSON>"))
			{
				String temp = token[i].substring(0, token[i].indexOf("</PERSON>"));
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = out+"</PERSON>";
			}
			else if (token[i].contains("</LOCATION>") && !token[i].contains("<LOCATION>"))
			{
				String temp = token[i].substring(0, token[i].indexOf("</LOCATION>"));
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = out+"</LOCATION>";
			}
			else if (token[i].contains("</TIME>") && !token[i].contains("<TIME>"))
			{
				String temp = token[i].substring(0, token[i].indexOf("</TIME>"));
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = out+"</TIME>";
			}
			else if (token[i].contains("<PERSON>") && token[i].contains("</PERSON>"))
			{
				String temp = token[i].substring(8, token[i].indexOf("</PERSON>"));
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = "<PERSON>"+out+"</PERSON>";
			}
			else if (token[i].contains("<LOCATION>") && token[i].contains("</LOCATION>"))
			{
				String temp = token[i].substring(10, token[i].indexOf("</LOCATION>"));
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = "<LOCATION>"+out+"</LOCATION>";
			}
			else if (token[i].contains("<TIME>") && token[i].contains("</TIME>"))
			{
				String temp = token[i].substring(6, token[i].indexOf("</TIME>"));
				char[] qu = temp.toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = "<TIME>"+out+"</TIME>";
			}
			else
			{
				char[] qu = token[i].toCharArray();
				int le = tes.stem(qu, qu.length, true);
				String out = new String (qu,0,le);
				token[i] = out;
			}
			System.out.println(token[i]);
			result = result + token[i];
			if (i != token.length-1)
			{
				result = result + " ";
			}
		}*/
		SentenceXML.add(sentence);
	}

	/**
	 * @param sentence the sentence to set
	 */
	public void setSentenceAll(ArrayList<String> sentence) {
		Sentence = sentence;
	}
	
	/**
	 * @param sentence the sentence with XML to set
	 */
	public void setSentenceXMLAll(ArrayList<String> sentence) {
		SentenceXML = sentence;
	}
	
	/**
	 * @param Serialize Sentence to one String
	 */
	public String toString()
	{
		String gabung="";
		for (int i=0;i<Sentence.size();i++)
		{
			gabung = gabung + Sentence.get(i);
			if (i != Sentence.size()-1)
			{
				gabung = gabung + " ";
			}
		}
		return gabung;		
	}
	
	public String toStringStemmed()
	{
		String gabung="";
		for (int i=0;i<Sentence.size();i++)
		{
			String temp = "";
			String[] token = Sentence.get(i).trim().split(" ");
			for (int j=0;j<token.length;j++)
			{
				temp = temp + Util.stem(token[j])+" ";
			}
			temp = temp.trim();
			gabung = gabung + temp;
			if (i != Sentence.size()-1)
			{
				gabung = gabung + " ";
			}
		}
		gabung = gabung.trim();
		return gabung;	
	}
	
	/**
	 * @param Serialize Sentence XML to one String
	 */
	public String toStringWithXML()
	{
		String gabung="";
		for (int i=0;i<SentenceXML.size();i++)
		{
			gabung = gabung + SentenceXML.get(i);
			if (i != SentenceXML.size()-1)
			{
				gabung = gabung + " ";
			}
		}
		return gabung;		
	}
	
	/**
	 * @return whether passage contain string or not
	 */
	public boolean contains (String compare)
	{
		for (String sentence:Sentence)
		{
			if (sentence.contains(compare))
			{
				return true;
			}
			
		}
		
		return false;
	}
	
	/**
	 * @return whether passage with XML contain string or not
	 */
	public boolean XMLcontains (String compare)
	{
		for (String sentence:SentenceXML)
		{
			if (sentence.contains(compare))
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * @return total count of string in passage
	 */
	public int wordCountInPassage (String search)
	{
		int count=0;
		for (String token:Sentence)
		{
			count = count + StringUtils.countMatches(token, search);
		}
		return count;
	}
	
	/**
	 * @return total count of string in passage
	 */
	public int wordCountInPassageXML (String search)
	{
		int count=0;
		for (String token:SentenceXML)
		{
			count = count + StringUtils.countMatches(token,search);
			
		}
		return count;
	}
	
	/**
	 * @return how many query word in passage
	 */
	public int queryWordCountInPassage (ArrayList<String> query)
	{
		int count=0;
		String passage = toStringStemmed ();
		for (String token:query)
		{
			if (passage.contains(token))
			{
				count++;
			}
			
		}
		return count;
	}
	
	/**
	 * @return how many query word in passage with XML
	 */
	public int queryWordCountInPassageXML (ArrayList<String> query)
	{
		int count=0;
		String passage = toStringWithXML ();
		for (String token:query)
		{
			if (passage.contains(token))
			{
				count++;
			}
			
		}
		return count;
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public boolean checkQueryWordSequenceInPassage (ArrayList<String> query) throws IOException
	{
		int maxLength = -1;
		for (String sentence:Sentence)
		{
			sentence.trim();
			ArrayList<String> token = new ArrayList<String>( Arrays.asList(sentence.split(" ")));
			for (int i=0;i<token.size();i++)
			{
				token.set(i, Util.stem(token.get(i)));
			}
			maxLength = Math.max(maxLength, LongestCommonSubstring.LCSubStr(token, query, token.size(), query.size()));
		}
		if (maxLength >= (query.size()/2))
		{
			return true;
		}
		return false;
		
	}
	
}
