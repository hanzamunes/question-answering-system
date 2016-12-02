package evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.opencsv.CSVReader;

import core.Utils;

public class PrecisionAndRecall
{
	ArrayList<Pair<String,String>> dataSet;
	private double relevantDocumentCount;
	
	
	public PrecisionAndRecall(String path)
	{
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
		this.dataSet = new ArrayList<Pair<String,String>>();
		this.relevantDocumentCount = 0;
		for (int i=1;i<dataset.size();i++)
		{
			String documentName = dataset.get(i)[1];
			String verdict = dataset.get(i)[2];
			this.dataSet.add(new MutablePair(documentName,verdict));
			if (verdict.equalsIgnoreCase("AC"))
			{
				this.relevantDocumentCount++;
			}
		}
	}
	
	public double[] calculateRecall ()
	{
		double[] recall = new double[this.dataSet.size()];
		double relevantCount=0;
		for (int i=0;i<this.dataSet.size();i++)
		{
			if (this.dataSet.get(i).getRight().equalsIgnoreCase("AC"))
			{
				relevantCount++;
			}
			recall[i] = relevantCount/this.relevantDocumentCount;
		}
		return recall;
	}
	
	public double[] calculatePrecision()
	{
		double[] precision = new double[this.dataSet.size()];
		double relevantCount = 0;
		for (int i=0;i<this.dataSet.size();i++)
		{
			if (this.dataSet.get(i).getRight().equalsIgnoreCase("AC"))
			{
				relevantCount++;
			}
			precision[i] = relevantCount/(double)(i+1);
			
		}
		return precision;
	}
	
	public double calculateAvaragePrecision (double[] precision)
	{
		double result = 0;
		for (int i=0;i<precision.length;i++)
		{
			result = result + precision[i];
		}
		result = result / (double)precision.length;
		return result;
	}
	
	public static double calculateMeanAveragePrecision (ArrayList<Double> averagePrecision)
	{
		double result = 0;
		for (int i=0;i<averagePrecision.size();i++)
		{
			result = result + averagePrecision.get(i);
		}
		result = result / (double)averagePrecision.size();
		return result;
	}
	
	public void savePrecisionAndRecall (String path, String retrievedCSVFilePath, String question, double[] precision, double[] recall, double averagePrecision) 
	{
		List<String[]> dataset = null;
		try
		{
			CSVReader reader = new CSVReader (new FileReader (retrievedCSVFilePath));
			dataset = reader.readAll();
			reader.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String csvOutput = dataset.get(0)[0]+","+dataset.get(0)[1]+","+dataset.get(0)[2]+",Precision,Recall\n";
		for (int i=1;i<dataset.size();i++)
		{
			csvOutput = csvOutput + dataset.get(i)[0]+","+dataset.get(i)[1]+","+dataset.get(i)[2]+","+precision[i-1]+","+recall[i-1]+"\n";
		}
		csvOutput = csvOutput + "average precision,"+averagePrecision;
		try
		{
			String fileName = path+question;
			BufferedWriter writeResult = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
			writeResult.write(csvOutput);
			writeResult.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main (String[] Args)
	{
		/*String file = "tesPrecisionAndRecall.csv";
		PrecisionAndRecall par = new PrecisionAndRecall(file);
		double[] precision = par.calculatePrecision();
		double[] recall = par.calculateRecall();
		double averagePrecision = par.calculateAvaragePrecision(precision);
		for (int i=0;i<precision.length;i++)
		{
			System.out.println(recall[i]+"\t"+precision[i]);
		}*/
		
		File dir = new File (Utils.saveRetrievedDocumentPath);
		File[] dirList = dir.listFiles();
		ArrayList<Double> averagePrecision = new ArrayList<Double>();
		for (File file:dirList)
		{
			if (file.isFile())
			{
				String question = file.getName();
				PrecisionAndRecall par = new PrecisionAndRecall(file.getAbsolutePath());
				double[] precision = par.calculatePrecision();
				double[] recall = par.calculateRecall();
				double avgPrecision = par.calculateAvaragePrecision(precision);
				averagePrecision.add(avgPrecision);
				par.savePrecisionAndRecall(Utils.savePrecisionAndRecallResultPath, file.getAbsolutePath(), question, precision, recall, avgPrecision);
			}
		}
		double meanAveragePrecision = calculateMeanAveragePrecision (averagePrecision);
		long percent = Math.round(meanAveragePrecision * 100);
		System.out.println("Mean average Precision = "+meanAveragePrecision);
		System.out.println("mean average precision in percent = "+percent+"%");
	}
	

}
