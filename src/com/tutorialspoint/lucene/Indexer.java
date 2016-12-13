package com.tutorialspoint.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

public class Indexer {

   private IndexWriter writer;

   public Indexer(String indexDirectoryPath) throws IOException{
      //this directory will contain the indexes
      Directory indexDirectory = 
         FSDirectory.open(new File(indexDirectoryPath));
      //StopWord stop = new StopWord("stopword_list_tala.txt");
      
      //create the indexer
      writer = new IndexWriter(indexDirectory, new IndexWriterConfig(Version.LUCENE_36, new IndonesianAnalyzer(Version.LUCENE_36)).setSimilarity(new DefaultSimilarity()).setOpenMode(OpenMode.CREATE));
   }

   public void close() throws CorruptIndexException, IOException{
      writer.close();
   }

   private Document getDocument(File file) throws IOException, SAXException, ParserConfigurationException{
      Document document = new Document();

     /* //index file contents
      Field contentField = new Field(LuceneConstants.CONTENTS, 
         new FileReader(file));
      //index file name
      Field fileNameField = new Field(LuceneConstants.FILE_NAME,
         file.getName(),
         Field.Store.YES,Field.Index.NOT_ANALYZED);
      //index file path
      Field filePathField = new Field(LuceneConstants.FILE_PATH,
         file.getCanonicalPath(),
         Field.Store.YES,Field.Index.NOT_ANALYZED);

      document.add(contentField);
      document.add(fileNameField);
      document.add(filePathField);*/
      
      
      
      	final org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
  		    .newDocumentBuilder()
  		    .parse(file);
      	final org.w3c.dom.NodeList field = doc.getElementsByTagName("field");
      	String text = field.item(2).getTextContent().toLowerCase();
	    Field docidField = new Field(LuceneConstants.DOCID,field.item(0).getTextContent(),Field.Store.YES,Field.Index.NOT_ANALYZED);
	    Field titleField = new Field(LuceneConstants.TITLE,field.item(1).getTextContent().toLowerCase(),Field.Store.YES,Field.Index.NOT_ANALYZED);
	    Field textField = new Field(LuceneConstants.TEXT,text,Field.Store.YES,Field.Index.ANALYZED);
	    Field fileNameField = new Field (LuceneConstants.FILE_NAME,file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED);
	    Field filePathField = new Field (LuceneConstants.FILE_PATH,file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED);
	    document.add(docidField);
	    document.add(titleField);
	    document.add(textField);
	    document.add(fileNameField);
	    document.add(filePathField);


      return document;
   }   

   private void indexFile(File file) throws IOException, SAXException, ParserConfigurationException{
      System.out.println("Indexing "+file.getCanonicalPath());
      Document document = getDocument(file);
      writer.addDocument(document);
   }

   public int createIndex(String dataDirPath, FileFilter filter) 
      throws IOException, SAXException, ParserConfigurationException{
      //get all files in the data directory
      File[] files = new File(dataDirPath).listFiles();

      for (File file : files) {
         if(!file.isDirectory()
            && !file.isHidden()
            && file.exists()
            && file.canRead()
            && filter.accept(file)
         ){
            indexFile(file);
         }
      }
      return writer.numDocs();
   }
}