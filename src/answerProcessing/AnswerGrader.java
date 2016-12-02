//package answerProcessing;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.apache.commons.lang3.tuple.MutablePair;
//import org.apache.commons.lang3.tuple.Pair;
//
//public class AnswerGrader {
//	
//	private Map<String,AnswerModel> answer;
//	
//	public AnswerGrader()
//	{
//		this.answer = new LinkedHashMap<String,AnswerModel>();
//	}
//	
//	
//	/**
//	 * @param set all answer's parameter
//	 */
//	public void setAllParameter (String answer,String title, String path, int distance, int totalQueryWord, int rank, int order)
//	{
//		if (this.answer.containsKey(answer))
//		{
//			AnswerModel data = this.answer.get(answer);
//			if (totalQueryWord > data.getTotalQueryWord())
//			{
//				if (distance < data.getDistance())
//				{
//					if (rank < data.getDocumentRank())
//					{
//							if (order < data.getProcessingOrder())
//							{
//								data.setAnswerDocumentTitle(title);
//								data.setAnswerDocumentPath(path);
//								data.setDistance(distance);
//								data.setTotalQueryWord(totalQueryWord);
//								data.setDocumentRank(rank);
//								data.setProcessingOrder(order);
//								this.answer.replace(answer, data);
//							}
//						
//					}
//				}
//			}
//		}
//		else
//		{
//			AnswerModel data = new AnswerModel();
//			data.setAnswer(answer);
//			data.setAnswerDocumentTitle(title);
//			data.setAnswerDocumentPath(path);
//			data.setDistance(distance);
//			data.setTotalQueryWord(totalQueryWord);
//			data.setDocumentRank(rank);
//			data.setProcessingOrder(order);
//			this.answer.put(answer, data);
//		}
//	}
//	
//	/**
//	 * @param set total distance from answer candidate to query word
//	 */
//	public void setDistance(String answer,int Distance)
//	{
//		if (this.answer.containsKey(answer))
//		{
//			if (Distance < this.answer.get(answer).getDistance())
//			{
//				AnswerModel temp = this.answer.get(answer);
//				temp.setDistance(Distance);
//				this.answer.replace(answer, temp);
//			}
//		}
//		else
//		{
//			AnswerModel temp = new AnswerModel();
//			temp.setAnswer(answer);
//			temp.setDistance(Distance);
//			this.answer.put(answer, temp);
//		}
//		
//	}
//	
//	/**
//	 * @return get distance from Answer candidate
//	 */
//	public int getDistance (String answer)
//	{
//		return this.answer.get(answer).getDistance();
//	}
//	
//	
//	
//	/**
//	 * @param set answer candidate from Passage along with their frequency
//	 */
//	public void setAnswer (String answers, int frequency)
//	{
//		if (this.answer.containsKey(answers))
//		{
//			if (frequency > this.answer.get(answers).getFrequency())
//			{
//				AnswerModel temp = this.answer.get(answers);
//				temp.setFrequency(frequency);
//				this.answer.replace(answers, temp);
//			}
//		}
//		else
//		{
//			AnswerModel temp = new AnswerModel();
//			temp.setAnswer(answers);
//			temp.setFrequency(frequency);
//			this.answer.put(answers, temp);
//		}
//	}
//	
//	/**
//	 * @param update answer candidate frequency
//	 */
//	public void updateFrequency (String answers,int frequency)
//	{
//		AnswerModel temp = this.answer.get(answers);
//		temp.setFrequency(frequency);
//		answer.replace(answers, temp);
//	}
//	
//	/**
//	 * @return get frequency from answer Candidate
//	 */
//	public int getAnswerFrequency (String answer)
//	{
//		return this.answer.get(answer).getFrequency();
//	}
//	
//	/**
//	 * @return check if answer already exist or not
//	 */
//	public boolean checkAnswerExist (String answer)
//	{
//		return this.answer.containsKey(answer);
//	}
//	
//	
//	
//	/**
//	 * @param set total Query Word from Answer Candidate in Passage
//	 */
//	public void setTotalQueryWord (String answer, int totalQueryWord)
//	{
//		if (this.answer.containsKey(answer))
//		{
//			if (totalQueryWord > this.answer.get(answer).getTotalQueryWord())
//			{
//				AnswerModel temp = this.answer.get(answer);
//				temp.setTotalQueryWord(totalQueryWord);
//				this.answer.replace(answer, temp);
//			}
//		}
//		else
//		{
//			AnswerModel temp = new AnswerModel();
//			temp.setAnswer(answer);
//			temp.setTotalQueryWord(totalQueryWord);
//			this.answer.put(answer, temp);
//		}
//		
//	}
//	
//	/**
//	 * @return get total Query Word from Answer Candidate
//	 */
//	public int getTotalQueryWord (String answer)
//	{
//		return this.answer.get(answer).getTotalQueryWord();
//	}
//	
//	
//	
//	/**
//	 * @param set Answer candidate's document rank
//	 */
//	public void SetDocumentRank (String answer, int rank)
//	{
//		if (this.answer.containsKey(answer))
//		{
//			if (rank < this.answer.get(answer).getDocumentRank())
//			{
//				AnswerModel temp = this.answer.get(answer);
//				temp.setDocumentRank(rank);
//				this.answer.replace(answer, temp);
//			}
//		}
//		else
//		{
//			AnswerModel temp = new AnswerModel();
//			temp.setAnswer(answer);
//			temp.setDocumentRank(rank);
//			this.answer.put(answer, temp);
//		}
//	}
//	
//	/**
//	 * @return get Answer Candidate's document rank
//	 */
//	public int getDocumentRank(String answer)
//	{
//		return this.answer.get(answer).getDocumentRank();
//	}
//	
//	/**
//	 * @param set answer candidate processing order
//	 */
//	public void setOrder(String answer,int order)
//	{
//		if (this.answer.containsKey(answer))
//		{
//			if (order < this.answer.get(answer).getProcessingOrder())
//			{
//				AnswerModel temp = this.answer.get(answer);
//				temp.setProcessingOrder(order);
//				this.answer.replace(answer, temp);
//			}
//		}
//		else
//		{
//			AnswerModel temp = new AnswerModel();
//			temp.setAnswer(answer);
//			temp.setProcessingOrder(order);
//			this.answer.put(answer, temp);
//		}
//	}
//	
//	
//	
//	public AnswerModel getAnswer ()
//	{	
//		ArrayList<AnswerModel> jawaban = new ArrayList<AnswerModel>();
//		Iterator<String> it = this.answer.keySet().iterator();
//		while (it.hasNext())
//		{
//			String key = it.next();
//			AnswerModel temp = this.answer.get(key);
//			if (temp.getTotalQueryWord()!= 0)
//			jawaban.add(temp);
//		}
//		Collections.sort(jawaban, AnswerModelComparator.getComparator(AnswerModelComparator.TOTALQUERY_SORT, AnswerModelComparator.DISTANCE_SORT, AnswerModelComparator.RANK_SORT, AnswerModelComparator.FREQUENCY_SORT, AnswerModelComparator.ORDER_SORT));
//		for (int i=0;i<jawaban.size();i++)
//		{
//			String answer = jawaban.get(i).getAnswer();
//			String path = jawaban.get(i).getAnswerDocumentPath();
//			String title = jawaban.get(i).getAnswerDocumentTitle();
//			int totalkeyword = jawaban.get(i).getTotalQueryWord();
//			int distance = jawaban.get(i).getDistance();
//			int dokrank = jawaban.get(i).getDocumentRank();
//			int freq = jawaban.get(i).getFrequency();
//			int order = jawaban.get(i).getProcessingOrder();
//			System.out.println("jawaban = "+answer);
//			System.out.println("total keyword = "+totalkeyword);
//			System.out.println("distance = "+distance);
//			System.out.println("doc rank = "+dokrank);
//			System.out.println("frequency = "+freq);
//			System.out.println("order process = " +order);
//			System.out.println("document file = "+path);
//			System.out.println("document Title = "+title);
//			System.out.println();
//		}
//		return jawaban.get(0);
//	}
//	
//}
