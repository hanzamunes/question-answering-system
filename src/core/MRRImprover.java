package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MRRImprover
{
	private ArrayList<String>improvedQuery;
	
	public MRRImprover()
	{
		this.improvedQuery = new ArrayList<String>();
	}
	
	public MRRImprover (String path)
	{
		this.improvedQuery = new ArrayList<String>();
		BufferedReader in;
		try
		{
			in =  new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
			String read;
			while ((read=in.readLine()) != null)
			{
				System.out.println(read);
				this.improvedQuery.add(read);
			}
			in.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<String> getImprovedQuery()
	{
		return this.improvedQuery;
	}
	
	public void addQuery (String query)
	{
		this.improvedQuery.add(query);
	}
	
	public static void main (String[] args)
	{
		MRRImprover imp = new MRRImprover ("improveList.txt");
		ArrayList<String> im = imp.getImprovedQuery();
		for (int i=0;i<im.size();i++)
		{
			System.out.println(im.get(i));
		}
	}

}
