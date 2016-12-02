package evaluation;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.opencsv.CSVReader;

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
	
	public double calculateMRR()
	{
		double pembilang = 0;
		for (int i=0;i<dataSet.size();i++)
		{
			pembilang = pembilang + ((double)1/(double)dataSet.get(i).getRight());
		}
		double result = pembilang / this.questionCount;
		return result;
	}
	
	public static void main (String[] Args)
	{
		//String file = "tesMRR.csv";
		String file = Utils.saveRetrievedAnswerPath+"percobaan_"+Utils.percobaanKe+".csv";
		System.out.println(file);
		MeanReciprocalRank mrr = new MeanReciprocalRank(file);
		double hasil = mrr.calculateMRR();
		long persen = Math.round(hasil * 100);
		System.out.println(hasil);
		System.out.println("MRR dalam persen = "+persen+"%");
	}
}
