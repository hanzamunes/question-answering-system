package answerProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.tutorialspoint.lucene.LuceneTester;

public class AnswerGrader2
{

	ArrayList<AnswerModel> answer;

	public AnswerGrader2()
	{
		this.answer = new ArrayList<AnswerModel>();
	}

	public void setAllParameter(String answer, String title, String path, int distance,int passageRank, int documentRank, int frequency,
			int order)
	{
		AnswerModel data = new AnswerModel();
		data.setAnswer(answer);
		data.setAnswerDocumentTitle(title);
		data.setAnswerDocumentPath(path);
		data.setDistance(distance);
		data.setPassageRank(passageRank);
		data.setDocumentRank(documentRank);
		data.setFrequency(frequency);
		data.setProcessingOrder(order);
		this.answer.add(data);
	}

	public ArrayList<AnswerModel> getAnswer()
	{
		System.out.println(this.answer.size());
		for (int i=0;i<this.answer.size();i++)
		{
			System.out.println("answer = "+this.answer.get(i).getAnswer()+" "+this.answer.get(i).getPassageRank());
		}
		Collections.sort(answer,
				AnswerModelComparator.getComparator(AnswerModelComparator.DISTANCE_SORT,
						AnswerModelComparator.RANK_SORT, AnswerModelComparator.FREQUENCY_SORT,
						AnswerModelComparator.ORDER_SORT));
		/*Collections.sort(answer,
				AnswerModelComparator.getComparator(AnswerModelComparator.TOTAL_SCORE_SORT, AnswerModelComparator.FREQUENCY_SORT,
						AnswerModelComparator.ORDER_SORT));*/
		for (int i = 0; i < this.answer.size(); i++)
		{
			String answer = this.answer.get(i).getAnswer();
			String path = this.answer.get(i).getAnswerDocumentPath();
			String title = this.answer.get(i).getAnswerDocumentTitle();
			int distance = this.answer.get(i).getDistance();
			int dokrank = this.answer.get(i).getDocumentRank();
			int freq = this.answer.get(i).getFrequency();
			int passageRank = this.answer.get(i).getPassageRank();
			int totalScore = this.answer.get(i).getTotalScore();
			int order = this.answer.get(i).getProcessingOrder();
			try
			{
				LuceneTester.out.write("this.answer = " + answer+"\n");
				LuceneTester.out.write("passage rank = "+passageRank+"\n");
				LuceneTester.out.write("distance = " + distance+"\n");
				LuceneTester.out.write("doc rank = " + dokrank+"\n");
				LuceneTester.out.write("total Score = "+totalScore+"\n");
				LuceneTester.out.write("frequency = " + freq+"\n");
				LuceneTester.out.write("order process = " + order+"\n");
				LuceneTester.out.write("document file = " + path+"\n");
				LuceneTester.out.write("document Title = " + title+"\n");
				LuceneTester.out.write("\n");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("this.answer = " + answer);
			System.out.println("passage rank = "+passageRank);
			System.out.println("distance = " + distance);
			System.out.println("doc rank = " + dokrank);
			System.out.println("total score = "+totalScore);
			System.out.println("frequency = " + freq);
			System.out.println("order process = " + order);
			System.out.println("document file = " + path);
			System.out.println("document Title = " + title);
			System.out.println();
		}
		return this.answer;

	}

}
