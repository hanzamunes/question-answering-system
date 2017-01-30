package evaluation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import core.Utils;

public class AnswerFiller
{
	Map <String,Map<String,String>> answer;
	
	public AnswerFiller()
	{
		answer = new HashMap<String,Map<String,String>>();
	}
	
	public void loadAnswer (String allBaseAnswerPath)
	{
		File root = new File (allBaseAnswerPath);
		answer = new HashMap<String,Map<String,String>>();
		File[] rootDir = root.listFiles();
		for (File file:rootDir)
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
			for (int i=1;i<dataset.size();i++)
			{
				String question = dataset.get(i)[1].toLowerCase();
				String jawaban = dataset.get(i)[2].toLowerCase();
				String verdict = dataset.get(i)[3].toLowerCase();
				question = question.trim();
				jawaban = jawaban.trim();
				verdict = verdict.trim();
				if (!answer.containsKey(question))
				{
					answer.put(question,new HashMap<String,String>());
				}
				Map<String,String> ans = answer.get(question);
				if (!ans.containsKey(jawaban))
				{
					ans.put(jawaban, verdict);
				}
				answer.replace(question, ans);
			}
		}
	}
	
	public AnswerFiller(String baseAnswerPath)
	{
		answer = new HashMap<String,Map<String,String>>();
		List<String[]> dataset = null;
		try
		{
			CSVReader reader = new CSVReader (new FileReader (baseAnswerPath));
			dataset = reader.readAll();
			reader.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=1;i<dataset.size();i++)
		{
			String question = dataset.get(i)[1].toLowerCase();
			String jawaban = dataset.get(i)[2].toLowerCase();
			String verdict = dataset.get(i)[3].toLowerCase();
			question = question.trim();
			jawaban = jawaban.trim();
			verdict = verdict.trim();
			if (!answer.containsKey(question))
			{
				answer.put(question,new HashMap<String,String>());
			}
			Map<String,String> ans = answer.get(question);
			if (!ans.containsKey(jawaban))
			{
				ans.put(jawaban, verdict);
			}
			answer.replace(question, ans);
		}
	}
	
	public void putAnswer (String path)
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
		CSVWriter writer=null;
		try
		{
			writer = new CSVWriter (new FileWriter (path));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=1;i<dataset.size();i++)
		{
			String question = dataset.get(i)[1];
			String jawaban = dataset.get(i)[2];
			question = question.trim().toLowerCase();
			jawaban = jawaban.trim().toLowerCase();
			if (answer.containsKey(question))
			{
				if (answer.get(question).containsKey(jawaban))
				{
					String verdict = answer.get(question).get(jawaban);
					dataset.get(i)[3] = verdict.toUpperCase();
				}
			}
		}
		writer.writeAll(dataset);
		try
		{
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("selesai");
	}
	
	public static void main (String[] Args)
	{
		/*AnswerFiller ans = new AnswerFiller ("debug question answering/listRetrievedAnswer/percobaan_4.csv");
		for (int i=0;i<ans.answer.size();i++)
		{
			Iterator<String> questionList = ans.answer.keySet().iterator();
			while (questionList.hasNext())
			{
				String question = questionList.next();
				System.out.println("question = "+question);
				Map<String,String> temp = ans.answer.get(question);
				Iterator<String> answerList = temp.keySet().iterator();
				while (answerList.hasNext())
				{
					String jawaban = answerList.next();
					String verdict = temp.get(jawaban);
					System.out.println(jawaban+" -> "+verdict);
				}
				System.out.println();
			}
		}
		ans.putAnswer("debug question answering/listRetrievedAnswer/percobaan_7.csv");*/
		File dir = new File ("debug question answering/hasil bersih/listRetrievedAnswer/");
		File[] dirList = dir.listFiles();
		ArrayList<String> filter = new ArrayList<String>();
		/*filter.add("percobaan_1.csv");
		filter.add("percobaan_2.csv");
		filter.add("percobaan_3.csv");
		filter.add("percobaan_4.csv");
		filter.add("percobaan_5.csv");
		filter.add("percobaan_6.csv");
		filter.add("percobaan_7.csv");
		/*filter.add("percobaan_8.csv");
		filter.add("percobaan_9.csv");
		filter.add("percobaan_10.csv");
		filter.add("percobaan_11.csv");
		filter.add("percobaan_12.csv");
		filter.add("percobaan_13.csv");
		filter.add("percobaan_14.csv");
		filter.add("percobaan_15.csv");
		filter.add("percobaan_16.csv");
		filter.add("percobaan_17.csv");
		filter.add("percobaan_18.csv");
		filter.add("percobaan_19.csv");
		filter.add("percobaan_20.csv");
		filter.add("percobaan_21.csv");
		filter.add("percobaan_22.csv");
		filter.add("percobaan_23.csv");
		filter.add("percobaan_24.csv");
		filter.add("percobaan_25.csv");
		filter.add("percobaan_26.csv");
		filter.add("percobaan_27.csv");
		filter.add("percobaan_28.csv");
		filter.add("percobaan_29.csv");
		filter.add("percobaan_30.csv");*/
		AnswerFiller ans = new AnswerFiller ("debug question answering/hasil bersih/listRetrievedAnswer/percobaan_8.csv");
		//AnswerFiller ans = new AnswerFiller();
		//ans.loadAnswer("C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion\\debug question answering\\listRetrievedAnswer");
		for (File file:dirList)
		{
			String fileName = file.getName();
			if (!filter.contains(fileName) && !fileName.equals("allMRR.csv"))
			{
				ans.putAnswer(file.getAbsolutePath());
			}
			System.out.println(fileName+" -> selesai");
			
		}
	}
}
