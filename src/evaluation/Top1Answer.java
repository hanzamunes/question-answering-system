package evaluation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import core.Utils;

public class Top1Answer
{
	public Top1Answer()
	{
	}
	
	public void calculateTop1Answer(String folderPath)
	{
		List<String[]> hasil = new ArrayList<String[]>();
		hasil.add(new String[]{"nama file","persentasi","Jumlah Pertanyaan Benar"});
		File dir = new File (folderPath);
		File[] dirList = Utils.sortFiles(dir.listFiles());
		for (File file:dirList)
		{
			if (file.isFile() && !file.getName().equals("allMRR.csv") && !file.getName().equals("allAkurasi1Answer.csv"))
			{
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
				List<String[]> debug = new ArrayList<String[]>();
				debug.add(dataset.get(0));
				String lastQuestion="";
				double counter = 0;
				double questioncount= 0;
				for (int i=1;i<dataset.size();i++)
				{
					String question = dataset.get(i)[1].toLowerCase().replaceAll("\\p{Punct}", "").trim();
					String verdict = dataset.get(i)[3];
					if (!lastQuestion.equalsIgnoreCase(question))
					{
						lastQuestion = question;
						questioncount++;
						if (verdict.equalsIgnoreCase("AC"))
						{
							counter++;
						}
						debug.add(dataset.get(i));
					}	
				}
				String savePath1 = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\hasil bersih\\listTop1Answer\\"+file.getName();
				try
				{
					CSVWriter write = new CSVWriter (new FileWriter (savePath1));
					write.writeAll(debug);
					write.close();
					debug.clear();
					debug = null;
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(questioncount);
				double persen = (counter/questioncount)*100;
				String out = Double.toString(persen)+"%";
				String total = Double.toString(counter);
				hasil.add(new String[]{file.getName(),out,total});
				
			}
		}
		String savePath = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\hasil bersih\\listRetrievedAnswer\\allAkurasi1Answer.csv";
		try
		{
			CSVWriter write = new CSVWriter (new FileWriter (savePath));
			write.writeAll(hasil);
			write.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("selesai");
		
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String folderBersihPath = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\hasil bersih\\listRetrievedAnswer";
		Top1Answer an = new Top1Answer();
		an.calculateTop1Answer(folderBersihPath);
		
	}

}
