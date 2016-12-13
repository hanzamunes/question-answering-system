package ner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class DocumentEditor {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		String fileLocation = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\dokumenSejarah";
		File dir = new File (fileLocation);
		File[] directoryListing = dir.listFiles();
		for (File file:directoryListing)
		{
			BufferedReader in = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file.getAbsolutePath()), "UTF8"));

			StringBuffer stringBuffer = new StringBuffer();
			String line="";
			while ((line=in.readLine())!=null)
			{
				String temp = line.toLowerCase();
				if (!temp.contains("demikianlah materi"))
				{
					stringBuffer.append(line);
					stringBuffer.append("\n");
				}
			}
			in.close();
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), "UTF-8"));
			out.write(stringBuffer.toString());
			out.close();
		}
		System.out.print("selesai");

	}

}
