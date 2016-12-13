package com.tutorialspoint.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

public class Testing {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<String>> passage = new ArrayList<ArrayList<String>>();
		final org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
	  		    .newDocumentBuilder()
	  		    .parse(new File ("C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\datadokumen\\doc7.txt"));
		final org.w3c.dom.NodeList field = doc.getElementsByTagName("field");
		String[] token = field.item(2).getTextContent().split(" ");
		for (int i=0;i<token.length;i++)
		{
			if (!token[i].equals(null))
			{
				System.out.println(token[i]);
			}
		}
	}

}
