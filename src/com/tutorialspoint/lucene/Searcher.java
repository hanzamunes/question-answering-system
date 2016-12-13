package com.tutorialspoint.lucene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import core.Query;


public class Searcher {
	
   IndexSearcher indexSearcher;
   QueryParser queryParser;
   org.apache.lucene.search.Query query;
   
   public Searcher(String indexDirectoryPath) 
      throws IOException{
      FSDirectory indexDirectory = 
         FSDirectory.open(new File(indexDirectoryPath));
      IndexReader idxreader = IndexReader.open(indexDirectory);
      //StopWord stop = new StopWord("stopword_list_tala.txt");
      indexSearcher = new IndexSearcher(idxreader);
      PrintWriter write = new PrintWriter ("tfidf.txt");
      TermEnum termEnum = idxreader.terms();
		TermDocs termDocs = idxreader.termDocs();
		int docsnum = idxreader.numDocs();
		while (termEnum.next()) {
			termDocs.seek(termEnum);
			while (termDocs.next()) {
				String term = termEnum.term().text();
				int tf = termDocs.freq();
				int df = termEnum.docFreq();
				float idf = Similarity.getDefault().idf(df, docsnum);
				float tfidf = tf * idf;
				write.println(term+" tf = "+tf+" df = "+df+" idf = "+idf + " tfidf = "+tfidf);
			}
		}
		write.close();
      queryParser = new QueryParser(Version.LUCENE_36,
         LuceneConstants.TEXT,
         new IndonesianAnalyzer(Version.LUCENE_36));
   }
   
   public TopDocs search( String searchQuery) 
      throws IOException, ParseException{
      query = queryParser.parse(searchQuery);
      return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
   }
   
   public TopDocs search(Query query) throws IOException, ParseException{
	      return indexSearcher.search(query.getQuery(), LuceneConstants.MAX_SEARCH);
	   }

   public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException{
      return indexSearcher.doc(scoreDoc.doc);	
   }

   public void close() throws IOException{
      indexSearcher.close();
   }
}