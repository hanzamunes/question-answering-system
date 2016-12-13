package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class Utils {

	public static String savePath1 = "debug question answering/coba1/hasilJalan1.txt";
	public static String savePath2 = "debug question answering/coba1/hasilJalan2.txt";
	public static String savePath3 = "debug question answering/coba1/hasilJalan3.txt";
	public static String savePath4 = "debug question answering/coba1/hasilJalan4.txt";
	public static String savePath5 = "debug question answering/coba1/hasilJalan5.txt";
	public static String savePath6 = "debug question answering/coba1/hasilJalan6.txt";
	public static String savePath7 = "debug question answering/coba1/hasilJalan7.txt";
	public static String classifierPath = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\classifier\\sejarahIndonesia-model.ser.gz";
	public static String savePath8 = "debug question answering/coba1/hasilJalan8.txt";
	public static String savePath9 = "hasilJalan9.txt";
	public static int percobaanKe = 100;
	public static String saveDebug = "debug question answering/listQuestionDebug/percobaan "+percobaanKe+"/";
	public static String saveRetrievedDocumentPath = "debug question answering/listRetrievedDocument/percobaan "+percobaanKe+"/";
	public static String saveRetrievedAnswerPath = "debug question answering/listRetrievedAnswer/";
	public static String relevanceAnswerPath = "debug question answering/listJawabanDokumenRelevan/";
	public static String savePrecisionAndRecallResultPath = "debug question answering/listPrecisionAndRecallResult/percobaan "+percobaanKe+"/";
	public static ArrayList<String> blockedQueryList = new ArrayList<String>();
	
	public static void addBlockedQueryList(String query)
	{
		blockedQueryList.add(query);
	}
	
	public static void changePath(int PercobaanKe)
	{
		percobaanKe = PercobaanKe;
		saveDebug = "debug question answering/listQuestionDebug/percobaan "+percobaanKe+"/";
		saveRetrievedDocumentPath = "debug question answering/listRetrievedDocument/percobaan "+percobaanKe+"/";
		savePrecisionAndRecallResultPath = "debug question answering/listPrecisionAndRecallResult/percobaan "+percobaanKe+"/";
	}
	
	public static double alpha = 1;
	public static double beta = 0.5;
	public static double gamma = 0;
	public static int documentLimit=10;
	public static int termLimit = 10;
	public static int topNPassage = 5;
	public static int topNAnswer = 5;
	
	public static File[] sortDir(File[] dirList)
	{
		ArrayList<Pair<Integer,File>> dirlist = new ArrayList<Pair<Integer,File>>();
		for (int i=0;i<dirList.length;i++)
		{
			if (dirList[i].getName().equals("allMAP.csv"))
			{
				continue;
			}
			int urutan = Integer.parseInt(dirList[i].getName().trim().split(" ")[1]);
			dirlist.add(new MutablePair(urutan,dirList[i]));
		}
		Collections.sort(dirlist);
		File[] result = new File[dirlist.size()];
		for (int i=0;i<dirlist.size();i++)
		{
			result[i] = dirlist.get(i).getRight();
		}
		return result;
	}
	
	public static File[] sortFiles(File[] dirList)
	{
		ArrayList<Pair<Integer,File>> dirlist = new ArrayList<Pair<Integer,File>>();
		for (int i=0;i<dirList.length;i++)
		{
			if (dirList[i].getName().equals("allMRR.csv"))
			{
				continue;
			}
			int urutan = Integer.parseInt(dirList[i].getName().trim().split("_")[1].replace(".csv", ""));
			dirlist.add(new MutablePair(urutan,dirList[i]));
		}
		Collections.sort(dirlist);
		File[] result = new File[dirlist.size()];
		for (int i=0;i<dirlist.size();i++)
		{
			result[i] = dirlist.get(i).getRight();
		}
		return result;
	}
	
	public static void folderPreparation ()
	{
		File saveDebugDir = new File(saveDebug);
	    if (! saveDebugDir.exists()){
	    	saveDebugDir.mkdir();
	    }
	    File saveRetrievedDocumentPathDir = new File(saveRetrievedDocumentPath);
	    if (! saveRetrievedDocumentPathDir.exists()){
	    	saveRetrievedDocumentPathDir.mkdirs();
	    }
	    File savePrecisionAndRecallResultPathDir = new File(savePrecisionAndRecallResultPath);
	    if (! savePrecisionAndRecallResultPathDir.exists()){
	    	savePrecisionAndRecallResultPathDir.mkdirs();
	    }

	}

	public static double getScore(Directory index, String term) throws CorruptIndexException, IOException {
		float tfidf = 0;
		IndexReader idxreader = IndexReader.open(index);
		TermEnum termEnum = idxreader.terms();
		TermDocs termDocs = idxreader.termDocs();
		int docsnum = idxreader.numDocs();
		while (termEnum.next()) {
			if (termEnum.term().text().equalsIgnoreCase(term)) {
				termDocs.seek(termEnum);
				if (termDocs.next()) {
					int tf = termDocs.freq();
					int df = termEnum.docFreq();
					float idf = Similarity.getDefault().idf(df, docsnum);
					tfidf = tf * idf;
					break;
				}
			}
		}
		idxreader.close();
		return tfidf;
	}
	
	public static Query normalizeQuery(String query, String field, Analyzer analyzer) throws ParseException {
		QueryParser queryParser = new QueryParser(Version.LUCENE_36, field, analyzer);
		String pattern = "[!|@|#|$|%|^|&|*|(|)|\\{|\\}|\\-|\\+|;|:|\'|\"|<|>|,|.|/|?|\\\\]";
		Set<String> termSet = new HashSet<String>(Arrays.asList(query.replaceAll(pattern, " ").split("\\s+")));
		StringBuilder terms = new StringBuilder(termSet.size());
		for (String term : termSet) {
			terms.append(' ').append(term);
		}
		return queryParser.parse(terms.toString());
	}
}