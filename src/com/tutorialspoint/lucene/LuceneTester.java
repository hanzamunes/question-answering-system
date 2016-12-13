package com.tutorialspoint.lucene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import answerProcessing.AnswerExtraction;
import answerProcessing.AnswerModel;
import core.Query;
import core.RelevanceCheckker;
import core.Utils;
import edu.stanford.nlp.ling.CoreLabel;
import passage.PassageModel;
import passage.PassageRanking;
import passage.PassageRetrieval;
import passage.QueryFunction;
import rocchio.RocchioExpander;
import suggestion.SuggestionSearch;

public class LuceneTester {
	static Scanner scan;
	public String indexDir = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\Index";
	public String dataDir = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\dokumenSejarah";
   Indexer indexer;
   Searcher searcher;
   public static Writer out;
   
   public static String fuzzyQueryCreatorString (String Query)
   {
	   String ask = "";
	   String[] t = Query.split(" ");
	   for (String e:t)
	   {
		   ask = ask + e+"~0.5 ";
	   }
	   ask = ask.trim();
	   return ask;
   }
   
   public static BooleanQuery fuzzyQueryCreator (String Query)
   {
	   BooleanQuery q = new BooleanQuery();
	   String ask = "";
	   String[] t = Query.split(" ");
	   QueryParser queryParser = new QueryParser(Version.LUCENE_36,
               LuceneConstants.TEXT,
               new IndonesianAnalyzer(Version.LUCENE_36));
	   for (String e:t)
	   {
		   try
		{
			q.add(queryParser.parse(e.replace("~","")+"~"),BooleanClause.Occur.SHOULD);
		} catch (ParseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	   } 
	   return q;
   }
   
   public ArrayList<AnswerModel> runSearcherWithDebug(String ask,String path,boolean withQueryExpander, boolean sentenceBased,boolean CreateIndex)
   {
	   ArrayList<AnswerModel> hasil = new ArrayList<AnswerModel>();
	   BufferedWriter writeRetrievedDocument =
	null;
	   ask = ask.trim();
	      ask = ask.replaceAll("\\p{Punct}", "");
	      String rocchioIndexdir = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\rocchioIndex\\percobaan "+Utils.percobaanKe+"\\"+ask;
	   try
		{
		   writeRetrievedDocument = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
		   String debugPath = Utils.saveDebug+ask+".txt";
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(debugPath), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	   	
	      //ask = fuzzyQueryCreatorString (ask);
	      //BooleanQuery askQuery = fuzzyQueryCreator (ask);
	      try {
	    	  if (CreateIndex)
	    	  {
	    		  createIndex();
	    	  }
	         StopWord stop = new StopWord("stopword_list_tala.txt");
	         QueryParser queryParser = new QueryParser(Version.LUCENE_36,
	                 LuceneConstants.TEXT,
	                 new IndonesianAnalyzer(Version.LUCENE_36));
	         //tester.search("kereta api");
	         
	         Query originalQuery = new Query(1,queryParser.parse(ask));
	         System.out.println(originalQuery.getQuery().toString());
	         out.write("searching awal\n");
	         ArrayList<Document> topDocument = retrieveDocument(originalQuery, -1);
	         out.write("\n");
	         //SuggestionSearch suggest = new SuggestionSearch (topDocument.get(0));
	         //ask = ask.replace("~0.5", "");
	         //ask = suggest.getSuggestions(ask);
	         //ask = ask.trim();
	         //askQuery = fuzzyQueryCreator (ask);
	         System.out.println(ask);
	         //originalQuery = new Query (1,queryParser.parse(ask));
	         //System.out.println("Showing the result from autocorrect = "+ask);
	         ArrayList<Document> topNewDocument;
	         if (withQueryExpander)
	         {
	        	 RocchioExpander rocchio = new RocchioExpander(new IndonesianAnalyzer(Version.LUCENE_36),LuceneConstants.TEXT,Utils.alpha,Utils.beta,Utils.gamma,Utils.documentLimit,Utils.termLimit);
		         Query newQuery = rocchio.expand(originalQuery, topDocument,rocchioIndexdir);
		         out.write("searching dari query expansion\n");
		         String expandedQuery = newQuery.getQuery().toString(LuceneConstants.TEXT);
		         out.write("query hasil ekspansi = "+expandedQuery+"\n");
		         topNewDocument = retrieveDocument(newQuery, Utils.documentLimit);
	         }
	         else
	         {
	        	 topNewDocument = new ArrayList<Document>();
	        	 for (int i=0;i<Utils.documentLimit;i++)
	        	 {
	        		 if (i<topDocument.size())
	        		 topNewDocument.add(topDocument.get(i));
	        	 }
	         }
	         RelevanceCheckker check = new RelevanceCheckker(Utils.relevanceAnswerPath);
	         writeRetrievedDocument.write("rank,document,verdict\n");
	         for (int i=0;i<topNewDocument.size();i++)
	         {
	        	 String fileName = topNewDocument.get(i).get(LuceneConstants.FILE_NAME);
	        	 String verdict;
	        	 if (check.checkRelevance(ask, fileName))
	        	 {
	        		 verdict = "AC";
	        	 }
	        	 else
	        	 {
	        		 verdict = "WA";
	        	 }
	        	 String out = (i+1)+","+fileName+","+verdict+"\n";
	        	 writeRetrievedDocument.write(out);
	         }
	         writeRetrievedDocument.close();
	         out.write("\n");
	         ask = ask.replace("~0.5", "");
	         PassageRetrieval passage = new PassageRetrieval ();
	         ArrayList<Pair<PassageModel,Double>>ranked = new ArrayList<Pair<PassageModel,Double>>();
	         int index=0;
	         int topNPassage = Utils.topNPassage;
	         QueryFunction qf = new QueryFunction (ask);
	         String entity = qf.getEntity();
	         ArrayList<String> queryToken = qf.getQueryToken();
	         out.write("query token : \n");
	         for (int i=0;i<queryToken.size();i++)
	         {
	        	 out.write(queryToken.get(i)+" ");
	         }
	         out.write("\n");
	         for (Document e : topNewDocument)
	         {
	        	 index++;
	        	 ArrayList<PassageModel> temp = passage.sentenceGenerator(e,index);
	        	 //List<List<CoreLabel>> temp  = passage.getSentenceViaCoreLabel(e);
	        	 out.write("document : "+e.get(LuceneConstants.FILE_PATH)+"\n");
	    		 out.write("title : "+e.get(LuceneConstants.TITLE)+"\n");
	        	 PassageRanking rank = new PassageRanking (temp);
	        	 ArrayList<Pair<PassageModel,Double>> temp1 = rank.getTopRankedPassage(ask, topNPassage);
	        	 for (int i=0;i<temp1.size();i++)
	        	 {
	        		 double peringkat = temp1.get(i).getRight();
	        		 ArrayList<String> pas = temp1.get(i).getLeft().getSentenceAll();
	        		 out.write("passage "+temp1.get(i).getLeft().getPassageNumber()+" score = "+temp1.get(i).getRight()+"\n");
	        		 for (int j=0;j<pas.size();j++)
	        		 {
	        			 out.write("sentence "+(j+1)+": "+pas.get(j)+"\n");
	        		 }
	        		 
	        	 }
	        	 out.write("\n");
	        	 ranked.addAll(temp1);
	         }
	         //long seed = System.nanoTime();
	         //Collections.shuffle(ranked,new Random(seed)); //INI SUMBER PERMASALAHANNYA!!!!
	         Collections.sort(ranked,Comparator.comparing(p -> -p.getRight()));
	         out.write("\nAfter sorted, chosen passage is : \n");
	         for (int i=0;i<topNPassage;i++)
	         {
	        	 out.write("document : "+ranked.get(i).getLeft().getDocumentPath()+"\n");
	        	 out.write("title : "+ranked.get(i).getLeft().getTitle()+"\n");
	        	 ArrayList<String> pas = ranked.get(i).getLeft().getSentenceAll();
	        	 out.write("passage "+ranked.get(i).getLeft().getPassageNumber()+" score = "+ranked.get(i).getRight()+"\n");
	        	 for (int j=0;j<pas.size();j++)
	        	 {
	        		 out.write("sentence "+(j+1)+": "+pas.get(j)+"\n");
	        	 }
	        	 out.write("\n");
	         }
	         out.write("\nPencarian jawaban\n");
	         out.write("---------------------\n");
	         if (sentenceBased)
	         {
	        	 hasil =  AnswerExtraction.extractAnswerSentenceBased(ranked, queryToken, entity, topNPassage);
	         }
	         else
	         {
	        	 hasil = AnswerExtraction.extractAnswer(ranked, queryToken, entity, topNPassage);
	         }
	         out.write("\n\nselesai");
	         out.close();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (ParseException e) {
	         e.printStackTrace();
	      } catch (SAXException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassCastException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	      return hasil;
   }
   
   public ArrayList<AnswerModel> runSearcher(String ask,boolean withQueryExpander, boolean sentenceBased,boolean CreateIndex)
   {
	   System.out.print("masuk loh");
	   ArrayList<AnswerModel> hasil = new ArrayList<AnswerModel>();
	   try
		{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Utils.savePath9), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	   	ask = ask.trim();
	      ask = ask.replaceAll("\\p{Punct}", "");
	      String rocchioIndexdir = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\rocchioIndex\\percobaan "+Utils.percobaanKe+"\\"+ask;
	      //ask = fuzzyQueryCreatorString (ask);
	      //BooleanQuery askQuery = fuzzyQueryCreator (ask);
	      System.out.println("sampai sini");
	      try {
	    	  if (CreateIndex)
	    	  {
	    		  createIndex();
	    		  System.out.println("create index jalan");
	    	  }
	         //StopWord stop = new StopWord("stopword_list_tala.txt");
	         QueryParser queryParser = new QueryParser(Version.LUCENE_36,
	                 LuceneConstants.TEXT,
	                 new IndonesianAnalyzer(Version.LUCENE_36));
	         //tester.search("kereta api");
	         
	         Query originalQuery = new Query(1,queryParser.parse(ask));
	         System.out.println(originalQuery.getQuery().toString());
	         out.write("searching awal\n");
	         ArrayList<Document> topDocument = retrieveDocument(originalQuery, -1);
	         out.write("\n");
	         //SuggestionSearch suggest = new SuggestionSearch (topDocument.get(0));
	         //ask = ask.replace("~0.5", "");
	         //ask = suggest.getSuggestions(ask);
	         //ask = ask.trim();
	         //askQuery = fuzzyQueryCreator (ask);
	         System.out.println(ask);
	         //originalQuery = new Query (1,queryParser.parse(ask));
	         //System.out.println("Showing the result from autocorrect = "+ask);
	         ArrayList<Document> topNewDocument;
	         if (withQueryExpander)
	         {
	        	 RocchioExpander rocchio = new RocchioExpander(new IndonesianAnalyzer(Version.LUCENE_36),LuceneConstants.TEXT,Utils.alpha,Utils.beta,Utils.gamma,Utils.documentLimit,Utils.termLimit);
		         Query newQuery = rocchio.expand(originalQuery, topDocument,rocchioIndexdir);
		         out.write("searching dari query expansion\n");
		         String expandedQuery = newQuery.getQuery().toString(LuceneConstants.TEXT);
		         out.write("query hasil ekspansi = "+expandedQuery+"\n");
		         topNewDocument = retrieveDocument(newQuery, Utils.documentLimit);
	         }
	         else
	         {
	        	 topNewDocument = new ArrayList<Document>();
	        	 for (int i=0;i<Utils.documentLimit;i++)
	        	 {
	        		 if (i<topDocument.size())
	        		 topNewDocument.add(topDocument.get(i));
	        	 }
	         }
	         out.write("\n");
	         ask = ask.replace("~0.5", "");
	         PassageRetrieval passage = new PassageRetrieval ();
	         ArrayList<Pair<PassageModel,Double>>ranked = new ArrayList<Pair<PassageModel,Double>>();
	         int index=0;
	         int topNPassage = Utils.topNPassage;
	         QueryFunction qf = new QueryFunction (ask);
	         String entity = qf.getEntity();
	         ArrayList<String> queryToken = qf.getQueryToken();
	         out.write("query token : \n");
	         for (int i=0;i<queryToken.size();i++)
	         {
	        	 out.write(queryToken.get(i)+" ");
	         }
	         out.write("\n");
	         for (Document e : topNewDocument)
	         {
	        	 index++;
	        	 ArrayList<PassageModel> temp = passage.sentenceGenerator(e,index);
	        	 //List<List<CoreLabel>> temp  = passage.getSentenceViaCoreLabel(e);
	        	 out.write("document : "+e.get(LuceneConstants.FILE_PATH)+"\n");
	    		 out.write("title : "+e.get(LuceneConstants.TITLE)+"\n");
	        	 PassageRanking rank = new PassageRanking (temp);
	        	 ArrayList<Pair<PassageModel,Double>> temp1 = rank.getTopRankedPassage(ask, topNPassage);
	        	 for (int i=0;i<temp1.size();i++)
	        	 {
	        		 double peringkat = temp1.get(i).getRight();
	        		 ArrayList<String> pas = temp1.get(i).getLeft().getSentenceAll();
	        		 out.write("passage "+temp1.get(i).getLeft().getPassageNumber()+" score = "+temp1.get(i).getRight()+"\n");
	        		 for (int j=0;j<pas.size();j++)
	        		 {
	        			 out.write("sentence "+(j+1)+": "+pas.get(j)+"\n");
	        		 }
	        		 
	        	 }
	        	 out.write("\n");
	        	 ranked.addAll(temp1);
	         }
	         //long seed = System.nanoTime();
	         //Collections.shuffle(ranked,new Random(seed)); //INI SUMBER PERMASALAHANNYA!!!!
	         Collections.sort(ranked,Comparator.comparing(p -> -p.getRight()));
	         out.write("\nAfter sorted, chosen passage is : \n");
	         for (int i=0;i<topNPassage;i++)
	         {
	        	 out.write("document : "+ranked.get(i).getLeft().getDocumentPath()+"\n");
	        	 out.write("title : "+ranked.get(i).getLeft().getTitle()+"\n");
	        	 ArrayList<String> pas = ranked.get(i).getLeft().getSentenceAll();
	        	 out.write("passage "+ranked.get(i).getLeft().getPassageNumber()+" score = "+ranked.get(i).getRight()+"\n");
	        	 for (int j=0;j<pas.size();j++)
	        	 {
	        		 out.write("sentence "+(j+1)+": "+pas.get(j)+"\n");
	        	 }
	        	 out.write("\n");
	         }
	         out.write("\nPencarian jawaban\n");
	         out.write("---------------------\n");
	         if (sentenceBased)
	         {
	        	 hasil =  AnswerExtraction.extractAnswerSentenceBased(ranked, queryToken, entity, topNPassage);
	         }
	         else
	         {
	        	 hasil = AnswerExtraction.extractAnswer(ranked, queryToken, entity, topNPassage);
	         }
	         out.write("\n\nselesai");
	         out.close();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (ParseException e) {
	         e.printStackTrace();
	      } catch (SAXException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassCastException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	      return hasil;
   }

   public static void main(String[] args) throws SAXException, ParserConfigurationException, ClassCastException, ClassNotFoundException {

    	  scan = new Scanner (System.in);
          System.out.print("Input pertanyaan = ");
          String ask = scan.nextLine();
          LuceneTester tester = new LuceneTester();
          tester.runSearcher(ask,false,true,true);
      
   }

   public void createIndex() throws IOException, SAXException, ParserConfigurationException{
      indexer = new Indexer(indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }

   private void search(String searchQuery) throws IOException, ParseException{
      searcher = new Searcher(indexDir);
      long startTime = System.currentTimeMillis();
      TopDocs hits = searcher.search(searchQuery);
      long endTime = System.currentTimeMillis();
   
      System.out.println(hits.totalHits +
         " documents found. Time :" + (endTime - startTime));
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
         Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
            + doc.get(LuceneConstants.FILE_PATH));
      }
      searcher.close();
   }
   
   private void search(Query searchQuery) throws IOException, ParseException{
	      searcher = new Searcher(indexDir);
	      long startTime = System.currentTimeMillis();
	      TopDocs hits = searcher.search(searchQuery);
	      long endTime = System.currentTimeMillis();
	   
	      System.out.println(hits.totalHits +
	         " documents found. Time :" + (endTime - startTime));
	      for(ScoreDoc scoreDoc : hits.scoreDocs) {
	         Document doc = searcher.getDocument(scoreDoc);
	            System.out.println("File: "
	            + doc.get(LuceneConstants.FILE_PATH));
	      }
	      searcher.close();
	   }
   
   public ArrayList<Document> retrieveDocument (Query searchQuery,int topNDocument) throws IOException, ParseException
   {
	   searcher = new Searcher(indexDir);
	   long startTime = System.currentTimeMillis();
	   TopDocs hits = searcher.search(searchQuery);
	   long endTime = System.currentTimeMillis();
	   System.out.println(hits.totalHits +
		         " documents found. Time :" + (endTime - startTime));

	   System.out.println("only take "+topNDocument+" document");
	   ArrayList<Document> result = new ArrayList<Document>();
	   int count=0;
	   for(ScoreDoc scoreDoc : hits.scoreDocs) {
		   count++;
	         Document doc = searcher.getDocument(scoreDoc);
	         result.add(doc); 
	         System.out.println("File: "
	 	            + doc.get(LuceneConstants.FILE_PATH));
	         out.write("File: "+ doc.get(LuceneConstants.FILE_PATH)+"\n");
	         if (topNDocument==count)
	         {
	        	 break;
	         }
	         //System.out.println("isi: "+doc.get(LuceneConstants.TEXT));
	      }
	   return result;
   }
   
   private ArrayList<Document> retrieveDocument (String searchQuery, int topNDocument) throws IOException, ParseException
   {
	   searcher = new Searcher(indexDir);
	   long startTime = System.currentTimeMillis();
	   TopDocs hits = searcher.search(searchQuery);
	   long endTime = System.currentTimeMillis();
	   System.out.println(hits.totalHits +
		         " documents found. Time :" + (endTime - startTime));
	   System.out.println("only take "+topNDocument+" document");
	   ArrayList<Document> result = new ArrayList<Document>();
	   int count=0;
	   for(ScoreDoc scoreDoc : hits.scoreDocs) {
		   count++;
	         Document doc = searcher.getDocument(scoreDoc);
	         result.add(doc); 
	         System.out.println("File: "
	 	            + doc.get(LuceneConstants.FILE_PATH));
	         if (topNDocument==count)
	         {
	        	 break;
	         }
	      }
	   return result;
	   
   }
   
}