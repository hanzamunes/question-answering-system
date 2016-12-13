package ner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class NERCreator {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*String fileLocation = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\datadokumen";
		File dir = new File (fileLocation);
		File[] directoryListing = dir.listFiles();
		//Random rand = new Random();
		Set<String>fileList = new HashSet<String>();
		if (directoryListing != null)
		{
			int count = 0;
			while (count <5)
			{
				count++;
				File child = directoryListing[count-1];
				ArrayList<String> token = new ArrayList<String>();
				System.out.println(child.getName());
				Document dok = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(child.getAbsolutePath());
				NodeList field = dok.getElementsByTagName("field");
				PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader (field.item(2).getTextContent()),new CoreLabelTokenFactory(),"");
				while (ptbt.hasNext())
				{
					CoreLabel label = ptbt.next();
					if (!Pattern.matches("\\p{Punct}", label.value()))
					{
						token.add(label.value());
					}
				}
				String f = child.getName().replace(".txt", "");
				String namaFile = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\src\\ner\\tsvList\\"+f+".tsv";
				fileList.add(f+".tsv");
				PrintWriter writer = new PrintWriter (namaFile,"UTF-8");
				for (String s : token) {
				    writer.println(s+"\tO");
				}
				writer.close();
			}
		}*/
		/*String fileLocation = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\src\\ner\\testing2";
		File dir = new File (fileLocation);
		String prop = "trainFileList = ";
		File[] directoryListing = dir.listFiles();
		int index=0;
		for (int i=0;i<directoryListing.length;i++)
		{
			prop = prop + directoryListing[i].getAbsolutePath();
			if (i != directoryListing.length-1)
			{
				prop = prop+",";
			}
		}
		prop = prop.replace("\\","\\\\");
		/*String prop  = "";
		prop = prop + "trainFileList = ";
		Iterator<String> it=fileList.iterator();
		while (it.hasNext())
		{
			prop = prop + it.next()+",";
		}
		prop = prop.substring(0,prop.length()-2);*/
		/*String dirs = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion\\src\\ner\\testing2\\tes.prop";
		prop = prop + "\n";
		prop = prop + "serializeTo = ner-model.ser.gz\n";
		prop = prop + "map = word=0,answer=1\n";
		prop = prop + "maxLeft=1\n";
		prop = prop + "useClassFeature=true\nuseWord=true\n";
		prop = prop + "useNGrams=true\nnoMidNGrams=true\nmaxNGramLeng=6\nusePrev=true\nuseNext=true\nuseDisjunctive=true\nuseSequences=true\nusePrevSequences=true\n";
		prop = prop + "useTypeSeqs=true\nuseTypeSeqs2=true\nuseTypeySequences=true\nwordShape=chris2useLC";
		PrintWriter write = new PrintWriter (dirs,"UTF-8");
		write.println(prop);
		write.close();*/
		String[] arg = new String[]{"-props","C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion\\src\\ner\\testing2\\tes.prop"};
		CRFClassifier.main(arg);
		System.out.println("Selesai");
	}

}
