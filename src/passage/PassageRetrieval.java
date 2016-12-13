package passage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

import com.tutorialspoint.lucene.LuceneConstants;

import core.Utils;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class PassageRetrieval {
	

	public PassageRetrieval ()
	{
	}
	
	public List<List<CoreLabel>> getSentenceViaCoreLabel (Document doc)
	{
		String serializedClassifier = Utils.classifierPath;
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		List<List<CoreLabel>> out = classifier.classify(doc.get(LuceneConstants.TEXT));
		return out;
	}
	
	public String getXML (Document doc)
	{
		String serializedClassifier = Utils.classifierPath;
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		return classifier.classifyWithInlineXML(doc.get(LuceneConstants.TEXT));
	}
	
	public ArrayList<PassageModel> sentenceGenerator (Document doc, int documentRank) throws IOException, ClassCastException, ClassNotFoundException
	{
		ArrayList<PassageModel> result = new ArrayList<PassageModel>();
		String serializedClassifier = Utils.classifierPath;
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifier(serializedClassifier);
		String title = doc.get(LuceneConstants.TITLE);
		String filePath = doc.get(LuceneConstants.FILE_PATH);
		List<List<CoreLabel>> out = classifier.classify(doc.get(LuceneConstants.TEXT));
		int co = 0;
		PassageModel passage = new PassageModel();
		for (List<CoreLabel> sentence:out)
		{
			String puresen = "";
			
			for (int i=0;i<sentence.size();i++)
			{
				
				puresen = puresen + sentence.get(i).originalText().toLowerCase();
				if (i != sentence.size()-1)
				{
					puresen = puresen + " ";
				}
			}
			String sen = classifier.classifyWithInlineXML(puresen);
			passage.setSentenceXML(sen);
			passage.setSentence(puresen);
			if (passage.getPassageCount()==5)
			{
				passage.setDocumentRank(documentRank);
				passage.setTitle(title);
				passage.setDocumentPath(filePath);
				co++;
				passage.setPassageNumber(co);
				result.add(passage);
				passage = new PassageModel();
				passage.setSentenceXML(sen);
				passage.setSentence(puresen);
			}
		}
		if (passage.getPassageCount()!=1)
		{
			passage.setDocumentRank(documentRank);
			passage.setDocumentPath(filePath);
			passage.setTitle(title);
			co++;
			passage.setPassageNumber(co);
			result.add(passage);
		}
		PrintWriter write = new PrintWriter ("hasilPassage.txt","UTF-8");
		int ind=0;
		for (PassageModel e:result)
		{
			ind++;
			ArrayList<String> hasil = e.getSentenceAll();
			write.println("passage ke-"+ind);
			for (String e1:hasil)
			{
				write.println(e1);
			}
			write.println();
		}
		
		write.close();
		return result;
	}
	
	public static void main (String[] args)
	{
		Writer out;
		try
		{
			File dok = new File ("dokumenSejarah/dok365.txt");
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("hasil token.txt"), "UTF-8"));
			final org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
		  		    .newDocumentBuilder()
		  		    .parse(dok);
		      	final org.w3c.dom.NodeList field = doc.getElementsByTagName("field");
		      	String text = field.item(2).getTextContent().toLowerCase();
			String serializedClassifier = Utils.classifierPath;
			AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
			out.write( classifier.classifyWithInlineXML(text)+"\n");
		} catch (UnsupportedEncodingException | FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	public ArrayList<PassageModel> sentenceGenerator(Document doc) throws IOException
	{
		System.out.println();
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(doc.get(LuceneConstants.TEXT)),new CoreLabelTokenFactory(),"normalizeParentheses=false,normalizeOtherBrackets=false,asciiQuotes=true,unicodeQuotes=true");
		ArrayList<PassageModel> result = new ArrayList<PassageModel>();
		PrintWriter write = new PrintWriter ("hasilPassage.txt","UTF-8");
		PassageModel atomic = new PassageModel();
		int sentence = 0;
		String kalimat="";
		while (ptbt.hasNext())
		{

			CoreLabel label = ptbt.next();
			if (atomic.getPassageCount() < 5)
			{
				if (label.value().endsWith(".") || label.value().endsWith("?") || label.value().endsWith("!") || label.value().endsWith("?!"))
				{
					kalimat = kalimat + label.value();
					atomic.setSentence(kalimat);
					kalimat="";
				}
				else
				{
					kalimat = kalimat + label.value() + " ";
				}

			}
			if(atomic.getPassageCount()==5)
			{
				result.add(atomic);
				String lastSentence = atomic.getSentence(4);
				atomic = new PassageModel();
				atomic.setSentence(lastSentence);
			}
		}
		if (atomic.getPassageCount()!=1)
		{
			result.add(atomic);
		}
		return result;
			
		/*int ind=0;
		for (PassageModel e:result)
		{
			ind++;
			ArrayList<String> hasil = e.getSentenceAll();
			write.println("passage ke-"+ind);
			for (String e1:hasil)
			{
				write.println(e1);
			}
			write.println();
		}
		
		write.close();*/
		/*PrintWriter writerStanford = new PrintWriter ("tokenStanford.txt","UTF-8");
		while (ptbt.hasNext())
		{
			CoreLabel label = ptbt.next();
	        writerStanford.println(label.value());
		}
		writerStanford.close();
		/*Analyzer analyze = new StandardAnalyzer(Version.LUCENE_36);
		TokenStream tokenStream = analyze.tokenStream(null, new StringReader (doc.get(LuceneConstants.TEXT)));
		tokenStream.reset();
		PrintWriter writerLucene = new PrintWriter ("tokenLucene.txt","UTF-8");
		while (tokenStream.incrementToken())
		{
			System.out.println("tes");
			writerLucene.println(tokenStream.getAttribute(CharTermAttribute.class).toString());
		}
		writerLucene.close();
	}*/

}
