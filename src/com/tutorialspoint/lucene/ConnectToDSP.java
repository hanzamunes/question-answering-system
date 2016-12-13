package com.tutorialspoint.lucene;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import answerProcessing.AnswerModel;

public class ConnectToDSP
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (args.length!=0)
		{
			String hiddenMarkov = "C:\\Users\\hobert\\workspace\\Hidden Markov Model 1\\myData.ser";
			String ask = args[0];
			LuceneTester tester = new LuceneTester();
			System.out.println(ask);
			ArrayList<AnswerModel> hasil = tester.runSearcher(ask,false,true,true);
			System.out.println(hasil);
			ObjectOutputStream stream;
			try
			{
				stream = new ObjectOutputStream(new FileOutputStream(hiddenMarkov));
				stream.writeObject(hasil);
				stream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
