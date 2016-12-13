package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.opencsv.CSVReader;

import core.MRRImprover;
import core.Utils;

public class MeanReciprocalRank
{
	
	ArrayList<Pair<String,Double>> dataSet;
	private double correctAnswer;
	private double questionCount;
	
	
	public MeanReciprocalRank(String path)
	{
		this.questionCount = 0;
		List<String[]> dataset = null;
		try
		{
			CSVReader reader = new CSVReader (new FileReader (path));
			dataset = reader.readAll();
			reader.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dataSet = new ArrayList<Pair<String,Double>>();
		this.correctAnswer = 0;
		String prevQuestion = dataset.get(1)[1];
		boolean get = false;
		for (int i=1;i<dataset.size();i++)
		{
			String question = dataset.get(i)[1];
			String verdict = dataset.get(i)[3];
			if (!prevQuestion.equals(question))
			{
				if (get)
				{
					this.dataSet.add(new MutablePair(prevQuestion,this.correctAnswer));
				}
				this.questionCount++;
				prevQuestion = question;
				this.correctAnswer=0;
				get = false;
			}
			if (verdict.equalsIgnoreCase("AC") && !get)
			{
				this.correctAnswer++;
				get = true;
			}
			else if (verdict.equalsIgnoreCase("WA") && !get)
			{
				this.correctAnswer++;
			}
			
		}
		if (get)
		{
			this.dataSet.add(new MutablePair (prevQuestion,this.correctAnswer));
		}
		this.questionCount++;
	}
	
	public MeanReciprocalRank(String path,ArrayList<String> improverQuery)
	{
		this.questionCount = 0;
		List<String[]> dataset = null;
		try
		{
			CSVReader reader = new CSVReader (new FileReader (path));
			dataset = reader.readAll();
			reader.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dataSet = new ArrayList<Pair<String,Double>>();
		this.correctAnswer = 0;
		String prevQuestion = dataset.get(1)[1];
		boolean get = false;
		for (int i=1;i<dataset.size();i++)
		{
			String question = dataset.get(i)[1];
			String verdict = dataset.get(i)[3];
			if (!prevQuestion.equals(question))
			{
				if (get)
				{
					this.dataSet.add(new MutablePair(prevQuestion,this.correctAnswer));
				}
				this.questionCount++;
				prevQuestion = question;
				this.correctAnswer=0;
				get = false;
			}
			if (verdict.equalsIgnoreCase("AC") && !get)
			{
				this.correctAnswer++;
				get = true;
			}
			else if (verdict.equalsIgnoreCase("WA") && !get)
			{
				this.correctAnswer++;
			}
			
		}
		if (get)
		{
			this.dataSet.add(new MutablePair (prevQuestion,this.correctAnswer));
		}
		this.questionCount++;
		this.questionCount = this.questionCount - improverQuery.size();
		int i=0;
		//System.out.println("dataSet size = "+this.dataSet.size());
		while (i<this.dataSet.size())
		{
			if (improverQuery.contains(this.dataSet.get(i).getLeft()))
			{
				this.dataSet.remove(i);
			}
			else
			{
				i++;
			}
		}
	}
	
	public double calculateMRR()
	{
		double pembilang = 0;
		for (int i=0;i<dataSet.size();i++)
		{
			//System.out.println(dataSet.get(i).getLeft());
			pembilang = pembilang + ((double)1/(double)dataSet.get(i).getRight());
		}
		double result = pembilang / this.questionCount;
		//System.out.println("\n");
		return result;
	}
	
	public static String[] getProblemQuery (String path,String questionPath)
	{
		File dir = new File (path);
		File[] dirList = dir.listFiles();
		Set<String> allFalse = new HashSet<String>();
		BufferedReader in;
		try
		{
			in = new BufferedReader(new FileReader(questionPath));
			String read;
			while ((read=in.readLine()) != null)
			{
				allFalse.add(read);
			}
			in.close();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (File file:dirList)
		{
			if (file.getName().equals("allMRR.csv"))
			{
				continue;
			}
			List<String[]> dataset = null;
			try
			{
				CSVReader reader = new CSVReader (new FileReader (file.getAbsolutePath()));
				dataset = reader.readAll();
				reader.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<Pair<String,Double>> dataSet = new ArrayList<Pair<String,Double>>();
			double correctAnswer = 0;
			String prevQuestion = dataset.get(1)[1];
			boolean get = false;
			int questionCount=0;
			for (int i=1;i<dataset.size();i++)
			{
				String question = dataset.get(i)[1];
				String verdict = dataset.get(i)[3];
				if (!prevQuestion.equals(question))
				{
					if (get)
					{
						dataSet.add(new MutablePair(prevQuestion,correctAnswer));
					}
					questionCount++;
					prevQuestion = question;
					correctAnswer=0;
					get = false;
				}
				if (verdict.equalsIgnoreCase("AC") && !get)
				{
					correctAnswer++;
					get = true;
				}
				else if (verdict.equalsIgnoreCase("WA") && !get)
				{
					correctAnswer++;
				}
				
			}
			if (get)
			{
				dataSet.add(new MutablePair (prevQuestion,correctAnswer));
			}
			for (int i=0;i<dataSet.size();i++)
			{
				if (dataSet.get(i).getRight()!=0)
				{
					if (allFalse.contains(dataSet.get(i).getLeft()))
					{
						allFalse.remove(dataSet.get(i).getLeft());
					}
				}
			}
			
		}
		return (String[]) allFalse.toArray(new String[allFalse.size()]);
	}
	
	public static void main (String[] Args)
	{
		//String file = "tesMRR.csv";
		/*String file = Utils.saveRetrievedAnswerPath+"percobaan_12.csv";
		System.out.println(file);
		MeanReciprocalRank mrr = new MeanReciprocalRank(file);
		double hasil = mrr.calculateMRR();
		long persen = Math.round(hasil * 100);
		System.out.println(hasil);
		System.out.println("MRR dalam persen = "+persen+"%");*/
		/*String[] list = getProblemQuery (Utils.saveRetrievedAnswerPath,"listPertanyaanUji.txt");
		try
		{
			Writer out = out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("listPertanyaanWA.txt"), "UTF-8"));
			for (int i=0;i<list.length;i++)
			{
				out.write(list[i]+"\n");
			}
			out.close();
		} catch ( IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		MRRImprover im = new MRRImprover ("improveList.txt");
		ArrayList<String> li = im.getImprovedQuery();
		File dir = new File ("C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\listRetrievedAnswer");
		File[] dirList = dir.listFiles();
		dirList = Utils.sortFiles(dirList);
		
		for (File file:dirList)
		{
			System.out.println(file.getName());
			if (!file.getName().equals("allMRR.csv") && file.isFile())
			{
				MeanReciprocalRank mrr = new MeanReciprocalRank(file.getAbsolutePath(),li);
				double hasil = mrr.calculateMRR();
				long persen = Math.round(hasil * 100);
				String per = persen+"%";
				System.out.println(file.getName()+"  = "+per);
			}
			
		}
		
		
		
	}
}
