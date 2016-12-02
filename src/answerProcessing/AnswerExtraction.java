package answerProcessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tutorialspoint.lucene.LuceneTester;

import passage.PassageModel;

public class AnswerExtraction {

	public static boolean contains(ArrayList<String> data,String search)
	{
		for (int i=0;i<data.size();i++)
		{
			if (search.equals(data.get(i)) || (StringUtils.getLevenshteinDistance(search, data.get(i)) < 2) )
			{
				return true;
			}
		}
		return false;
	}
	
	public static int get (Map<String,Integer> data, String search)
	{
		Iterator<String> it = data.keySet().iterator();
		while (it.hasNext())
		{
			String key = it.next();
			int value = data.get(key);
			if (search.equals(key) || StringUtils.getLevenshteinDistance(key, search) < 2)
			{
				return value;
			}
		}
		return -1;
	}
	
	public static ArrayList<AnswerModel> extractAnswerSentenceBased (ArrayList<Pair<PassageModel,Double>> ranked, ArrayList<String> queryToken, String entity, int topNPassage) throws IOException
	{
		AnswerGrader2 grade = new AnswerGrader2();
		int co=0;
		for (int i=0;i<topNPassage;i++)
		{
			if (i < ranked.size())
			{
				ArrayList<String> xmlSentence = ranked.get(i).getLeft().getSentenceXMLAll();
				int sen = 0;
				for (String xml:xmlSentence)
				{
					LuceneTester.out.write("\n\n"+xml+"\n\n");
					sen++;
					Util.splitWithTagAndNoTag(xml);
					ArrayList<String> tokenWithTagStemmed = Util.tokenWithTagStemmed;
					ArrayList<String> tokenWithoutTag = Util.tokenWithoutTag;
					ArrayList<String> tokenWithoutTagStemmed = Util.tokenWithoutTagStemmed;
					ArrayList<Pair<String,Integer>> answerPosition = new ArrayList<Pair<String,Integer>>();
					String answerEntityTag = "<"+entity+">";
					Map<String,Integer> answerFrequency = new HashMap<String,Integer>();
					for (int j=0;j<tokenWithTagStemmed.size();j++)
					{
						String process = tokenWithTagStemmed.get(j);
						//System.out.println(process);
						if (StringUtils.containsIgnoreCase(process, answerEntityTag))
						{
							String answerToken = tokenWithoutTag.get(j);
							answerToken = answerToken.trim();
							if (answerFrequency.containsKey(answerToken))
							{
								int f = answerFrequency.get(answerToken);
								answerFrequency.replace(answerToken, ++f);
							}
							else
							{
								answerFrequency.put(answerToken, 1);
							}
							answerPosition.add(new MutablePair<String,Integer>(answerToken,j));
						}
					}
					if (answerPosition.isEmpty())
					{
						LuceneTester.out.write("\ntidak ada entitas jawaban, skip\n");
						continue;
					}
					LuceneTester.out.write("memproses passage "+ranked.get(i).getLeft().getPassageNumber()+"\n");
					LuceneTester.out.write("sentence : "+sen+"\n");
					LuceneTester.out.write("from document : "+ranked.get(i).getLeft().getDocumentPath()+"\n");
					LuceneTester.out.write("title : "+ranked.get(i).getLeft().getTitle()+"\n");
					LuceneTester.out.write("list jawaban dan posisi dalam passage\n");
					for (int j=0;j<answerPosition.size();j++)
					{
						LuceneTester.out.write(answerPosition.get(j).getLeft()+" | position = "+answerPosition.get(j).getRight()+"\n");
					}
					LuceneTester.out.write("\n\nmelakukan pencarian:\n");
					System.out.println(answerPosition.size());
					System.out.println(queryToken.size());
					System.out.println(tokenWithoutTagStemmed.size());
					for (int j=0;j<queryToken.size();j++)
					{
						System.out.println(queryToken.get(j));
						LuceneTester.out.write("kata kunci = "+queryToken.get(j)+"\n");
						for (int k=0;k<tokenWithoutTagStemmed.size();k++)
						{
							LuceneTester.out.write("tokenwithtagstemmed = "+tokenWithoutTagStemmed.get(k)+"\n");
							if (tokenWithoutTagStemmed.get(k).equalsIgnoreCase(queryToken.get(j)))
							{
								
								int queryPos = k;
								LuceneTester.out.write("posisi dalam passage = "+queryPos+"\n");
								LuceneTester.out.write("jarak dengan tiap kandidat jawaban:\n");
								for (int l=0;l<answerPosition.size();l++)
								{
									int distance = answerPosition.get(l).getRight() - queryPos;
									LuceneTester.out.write(answerPosition.get(l).getLeft()+" "+distance+"\n");
									if (!answerPosition.get(l).getLeft().equalsIgnoreCase(queryToken.get(j)) && (distance > 0))
									{
										co++;
										String answer = answerPosition.get(l).getLeft();
										System.out.println(answer);
										String title = ranked.get(i).getLeft().getTitle();
										String path = ranked.get(i).getLeft().getDocumentPath();
										int passageRank = i+1;
										int documentRank = ranked.get(i).getLeft().getDocumentRank();
										int frequency = answerFrequency.get(answer);
										int order = co;
										grade.setAllParameter(answer, title, path, distance,passageRank, documentRank, frequency, order);
									}
								}
								LuceneTester.out.write("\n");
							}
						}
					}
				}
			}
		}
		LuceneTester.out.write("\nPengurutan kandidat jawaban\n");
		ArrayList<AnswerModel> jawaban = grade.getAnswer();
		if (!jawaban.isEmpty())
		{
			AnswerModel jawaban1 = jawaban.get(0);
			System.out.println("jawaban dari queri adalah = "+jawaban1.getAnswer());
	        System.out.println("jawaban diperoleh dari dokumen = "+ jawaban1.getAnswerDocumentPath());
	        System.out.println("judul dari dokumen = "+jawaban1.getAnswerDocumentTitle());
		}
		else
		{
			System.out.println("no answer");
		}
        return jawaban;
	}
	
	public static ArrayList<AnswerModel> extractAnswer (ArrayList<Pair<PassageModel,Double>> ranked, ArrayList<String> queryToken, String entity, int topNPassage) throws SAXException, IOException, ParserConfigurationException
	{
		AnswerGrader2 grade = new AnswerGrader2();
		int co = 0;
		for (int i=0;i<topNPassage;i++)
		{
			if (i < ranked.size())
			{
				System.out.println("passage -"+i);
				String xml = ranked.get(i).getLeft().toStringWithXML();
				LuceneTester.out.write("\n\n"+xml+"\n\n");
				Util.splitWithTagAndNoTag(xml);
				ArrayList<String> tokenWithTagStemmed = Util.tokenWithTagStemmed;
				ArrayList<String> tokenWithoutTag = Util.tokenWithoutTag;
				ArrayList<String> tokenWithoutTagStemmed = Util.tokenWithoutTagStemmed;
				ArrayList<Pair<String,Integer>> answerPosition = new ArrayList<Pair<String,Integer>>();
				String answerEntityTag = "<"+entity+">";
				Map<String,Integer> answerFrequency = new HashMap<String,Integer>();
				for (int j=0;j<tokenWithTagStemmed.size();j++)
				{
					String process = tokenWithTagStemmed.get(j);
					//System.out.println(process);
					if (StringUtils.containsIgnoreCase(process, answerEntityTag))
					{
						String answerToken = tokenWithoutTag.get(j);
						answerToken = answerToken.trim();
						if (answerFrequency.containsKey(answerToken))
						{
							int f = answerFrequency.get(answerToken);
							answerFrequency.replace(answerToken, ++f);
						}
						else
						{
							answerFrequency.put(answerToken, 1);
						}
						answerPosition.add(new MutablePair<String,Integer>(answerToken,j));
					}
				}
				if (answerPosition.isEmpty())
				{
					LuceneTester.out.write("\ntidak ada entitas jawaban, skip\n");
					continue;
				}
				LuceneTester.out.write("memproses passage "+ranked.get(i).getLeft().getPassageNumber()+"\n");
				LuceneTester.out.write("from document : "+ranked.get(i).getLeft().getDocumentPath()+"\n");
				LuceneTester.out.write("title : "+ranked.get(i).getLeft().getTitle()+"\n");
				LuceneTester.out.write("list jawaban dan posisi dalam passage\n");
				for (int j=0;j<answerPosition.size();j++)
				{
					LuceneTester.out.write(answerPosition.get(j).getLeft()+" | position = "+answerPosition.get(j).getRight()+"\n");
				}
				LuceneTester.out.write("\n\nmelakukan pencarian:\n");
				System.out.println(answerPosition.size());
				System.out.println(queryToken.size());
				System.out.println(tokenWithoutTagStemmed.size());
				for (int j=0;j<queryToken.size();j++)
				{
					System.out.println(queryToken.get(j));
					LuceneTester.out.write("kata kunci = "+queryToken.get(j)+"\n");
					for (int k=0;k<tokenWithoutTagStemmed.size();k++)
					{
						LuceneTester.out.write("tokenwithtagstemmed = "+tokenWithoutTagStemmed.get(k)+"\n");
						if (tokenWithoutTagStemmed.get(k).equalsIgnoreCase(queryToken.get(j)))
						{
							
							int queryPos = k;
							LuceneTester.out.write("posisi dalam passage = "+queryPos+"\n");
							LuceneTester.out.write("jarak dengan tiap kandidat jawaban:\n");
							for (int l=0;l<answerPosition.size();l++)
							{
								int distance = answerPosition.get(l).getRight() - queryPos;
								LuceneTester.out.write(answerPosition.get(l).getLeft()+" "+distance+"\n");
								if (!answerPosition.get(l).getLeft().equalsIgnoreCase(queryToken.get(j)) && (distance > 0))
								{
									co++;
									String answer = answerPosition.get(l).getLeft();
									System.out.println(answer);
									String title = ranked.get(i).getLeft().getTitle();
									String path = ranked.get(i).getLeft().getDocumentPath();
									int passageRank = i+1;
									int documentRank = ranked.get(i).getLeft().getDocumentRank();
									int frequency = answerFrequency.get(answer);
									int order = co;
									grade.setAllParameter(answer, title, path, distance,passageRank, documentRank, frequency, order);
								}
							}
							LuceneTester.out.write("\n");
						}
					}
				}
				
			}
		}
		LuceneTester.out.write("\nPengurutan kandidat jawaban\n");
		ArrayList<AnswerModel> jawaban = grade.getAnswer();
		if (!jawaban.isEmpty())
		{
			AnswerModel jawaban1 = jawaban.get(0);
			System.out.println("jawaban dari queri adalah = "+jawaban1.getAnswer());
	        System.out.println("jawaban diperoleh dari dokumen = "+ jawaban1.getAnswerDocumentPath());
	        System.out.println("judul dari dokumen = "+jawaban1.getAnswerDocumentTitle());
		}
		else
		{
			System.out.println("no answer");
		}
        return jawaban;
        
		
	}
	
	/*public static void extractAnswer (ArrayList<Pair<PassageModel,Double>> ranked, ArrayList<String> queryToken, String entity, int topNPassage) throws SAXException, IOException, ParserConfigurationException
	{
		PrintWriter write = new PrintWriter("hasilPassageAll.txt","UTF-8");
		AnswerGrader grade = new AnswerGrader();
		IndonesianStemmer idStem = new IndonesianStemmer();
        for (int i=0;i<topNPassage;i++)
        {
        	if (i < ranked.size())
        	{
        		String clean = ranked.get(i).getLeft().toString();
            	String xml = ranked.get(i).getLeft().toStringWithXML();
            	System.out.println(xml);
            	System.out.println(ranked.get(i).getLeft().getDocumentPath());
            	System.out.println(ranked.get(i).getLeft().getTitle());
            	//xml = xml.replaceAll("\\p{Punct}+", "");
            	String parseXML = "<passage>"+xml+"</passage>";
            	parseXML = parseXML.replaceAll("&\\s+", "&amp;");
            	InputSource is = new InputSource();
            	is.setCharacterStream(new StringReader (parseXML));
            	final org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
              		    .newDocumentBuilder()
              		    .parse(is);
                final org.w3c.dom.NodeList field = doc.getElementsByTagName(entity);
                ArrayList<String> allAnswerEntity = new ArrayList<String>();
                for (int j=0;j<field.getLength();j++)
                {
                	String answer = field.item(j).getTextContent();
                	System.out.println(answer);
                	answer = answer.replaceAll("[,]", "");
                	allAnswerEntity.add(answer);
                	if (grade.checkAnswerExist(answer))
                	{
                		int freq = grade.getAnswerFrequency(answer);
                		grade.updateFrequency(answer, ++freq );
                	}
                	else
                	{
                		grade.setAnswer(answer,1);
                	}
                }
            	ArrayList<String> token = new ArrayList<String> (Arrays.asList(xml.split(" ")));
            	ArrayList<String> tokenClean = new ArrayList<String> (Arrays.asList(clean.split(" ")));
            	Map<String,Integer> counter = new HashMap<String,Integer>();
            	int jumlah = 0;
            	for (int j=0;j<queryToken.size();j++)
            	{
            		counter.put(queryToken.get(j),0);
            	}
            	String xmlEntity = "<"+entity+">";
            	int urutan=0;
            	for (int j=0;j<token.size();j++)
            	{
            		if (token.get(j).contains(xmlEntity))
            		{
            			urutan++;
            			int tandaBaca=0;
            			for (int k=j-1;k>=0;k--)
            			{
            				String tokenK = tokenClean.get(k);
            				if (tokenK.equals("pattimura"))
            				{
            					System.out.println("distance = "+StringUtils.getLevenshteinDistance(tokenK, "patimura"));
            				}
            				char[] qu = tokenK.toCharArray();
                			int le = idStem.stem(qu, qu.length, true);
                			tokenK = new String (qu,0,le);
            				if (Pattern.matches("\\p{Punct}", tokenK))
            				{
            					tandaBaca++;
            				}
            				if (queryToken.contains(tokenK) && counter.get(tokenK)==0)
            				{
            					counter.replace(tokenK,(counter.get(tokenK)+(j-k-tandaBaca)));
            					
            				}
            			}
            			System.out.println(allAnswerEntity.get(urutan-1)+" : ");
                		Iterator<String> it = counter.keySet().iterator();
                		//System.out.println("masuk");
                		jumlah = 0;
                		int totalQueryWord = 0;
                    	while (it.hasNext())
                    	{
                    		String key = it.next();
                    		int value = counter.get(key);
                    		if (value != 0)
                    		{
                    			totalQueryWord++;
                    		}
                    		jumlah = jumlah + value;
                    		System.out.println(key+" "+value);
                    		counter.replace(key, 0);
                    	}
                    	if (jumlah == 0)
                    	{
                    		jumlah = Integer.MAX_VALUE;
                    	}
                    	grade.setAllParameter(allAnswerEntity.get(urutan-1), ranked.get(i).getLeft().getTitle(), ranked.get(i).getLeft().getDocumentPath(), jumlah, totalQueryWord, ranked.get(i).getLeft().getDocumentRank(), urutan);
                    	/*grade.setTotalQueryWord(allAnswerEntity.get(urutan-1),totalQueryWord);
                    	grade.setDistance(allAnswerEntity.get(urutan-1),jumlah);
                    	grade.SetDocumentRank(allAnswerEntity.get(urutan-1), ranked.get(i).getLeft().getDocumentRank());
                    	grade.setOrder(allAnswerEntity.get(urutan-1), urutan);
            		}
            		
            	}
        	}
        	
        	
        }
        AnswerModel jawaban = grade.getAnswer();
        System.out.println("jawaban dari queri adalah = "+jawaban.getAnswer());
        System.out.println("jawaban diperoleh dari dokumen = "+ jawaban.getAnswerDocumentPath());
        System.out.println("judul dari dokumen = "+jawaban.getAnswerDocumentTitle());
        write.close();
	}*/

}
