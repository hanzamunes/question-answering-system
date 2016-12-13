package core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class FolderCleaner
{
	
	ArrayList<String> questionList;
	
	public FolderCleaner (String listQuestionPath)
	{
		MRRImprover im = new MRRImprover (listQuestionPath);
		questionList = new ArrayList<String>(im.getImprovedQuery());
		for (int i=0;i<questionList.size();i++)
		{
			String fix = questionList.get(i).replaceAll("\\p{Punct}","").toLowerCase().trim();
			questionList.set(i, fix);
		}
	}
	
	public void cleanFile(String path)
	{
		File dir = new File (path);
		String folderName = dir.getName();
		String saveLocation = "debug question answering/hasil bersih/"+folderName+"/";
		File saveLocationFile = new File (saveLocation);
		saveLocationFile.mkdirs();
		File[] dirList = dir.listFiles();
		for (File file:dirList)
		{
			if (file.isFile())
			{
				String fileName = file.getName();
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
				int i=0;
				while (i < dataset.size())
				{
					String question = dataset.get(i)[1].replaceAll("\\p{Punct}","").toLowerCase().trim();
					if (questionList.contains(question))
					{
						dataset.remove(i);
					}
					else
					{
						i++;
					}
				}
				String save = saveLocation+fileName;
				CSVWriter write;
				try
				{
					write = new CSVWriter (new FileWriter (save));
					write.writeAll(dataset);
					write.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public void cleanDir (String path)
	{
		File root = new File (path);
		String folderName = root.getName();
		File[] rootDir = root.listFiles();
		for (File dir:rootDir)
		{
			if (dir.isDirectory())
			{
				File[] dirList = dir.listFiles();
				String percobaan = dir.getName();
				String saveLocation = "debug question answering/hasil bersih/"+folderName+"/"+percobaan+"/";
				File saveLocationFile = new File (saveLocation);
				saveLocationFile.mkdirs();
				for (File file:dirList)
				{
					String question = file.getName().toLowerCase().replace(".csv", "").replace(".txt", "").trim();
					if (!questionList.contains(question))
					{
						try
						{
							FileUtils.copyFileToDirectory(file.getAbsoluteFile(),saveLocationFile);
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		FolderCleaner fc = new FolderCleaner ("improveList.txt");
		//fc.cleanDir("debug question answering/listRetrievedDocument");
		//fc.cleanDir("debug question answering/listQuestionDebug");
		//fc.cleanDir("debug question answering/listPrecisionAndRecallResult");
		fc.cleanFile("debug question answering/listRetrievedAnswer");
		System.out.println("selesai");
	}

}
