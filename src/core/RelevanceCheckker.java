package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class RelevanceCheckker
{
	Map <String,ArrayList<String>> jawaban;
	
	public RelevanceCheckker(String relevanceDirectoryPath)
	{
		this.jawaban = new HashMap<String,ArrayList<String>>();
		File dir = new File (relevanceDirectoryPath);
		File[] dirList = dir.listFiles();
		for (File file : dirList)
		{
			if (file.isFile())
			{
				try
				{
					String question = file.getName();
					question = question.replace(".txt","");
					question = question.trim();
					if (!this.jawaban.containsKey(question))
					{
						this.jawaban.put(question, new ArrayList<String>());
					}
					BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF8"));
					String line = "";
					ArrayList<String> isi = new ArrayList<String>();
					while ((line = in.readLine())!= null)
					{
						line = line.trim();
						isi.add(line);
					}
					this.jawaban.replace(question,isi);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public boolean checkRelevance (String question, String documentName)
	{
		ArrayList<String> candidate = this.jawaban.get(question);
		documentName = documentName.trim();
		if (candidate != null)
		{
			if (candidate.contains(documentName))
			{
				return true;
			}
			return false;
		}
		return false;
	}
}
