/**
 * digunakan untuk query expansion
 * @author c00kiemon5ter
 */
package rocchio;

import core.Query;
import core.ScoreComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.tutorialspoint.lucene.StopWord;

import core.Utils;
import java.util.AbstractMap.SimpleEntry;

public class RocchioExpander implements QueryExpander {

	private static final double doclimit = 0;
	private final double alpha;
	private final double beta;
	private final double gama;
	private final int docsLimit;
	private final int termsLimit;
	private Analyzer analyzer;
	private final String field;

	public RocchioExpander(Analyzer analyzer, final String field,
			       double alpha, double beta, double gama,
			       int docsLimit, int termsLimit) {
		this.analyzer = analyzer;
		this.field = field;
		this.alpha = alpha;
		this.beta = beta;
		this.gama = gama;
		this.docsLimit = docsLimit;
		this.termsLimit = termsLimit;
	}

	@Override
	public Query expand(final Query original, final List<Document> relevantDocs, final String indexDirectoryPath)
		throws ParseException, CorruptIndexException,
		       LockObtainFailedException, IOException {
		Directory index = createIndex(indexDirectoryPath, relevantDocs);
		// newQVector = alpha * oldQVector + beta * Sum{i=1..docs}( DocsVector )
		List<Entry<String, Double>> newQVector = getTermScoreList(index);
		for (String term : Arrays.asList(original.getQuery().toString(field).split("\\s+"))) {
			double score = alpha * Utils.getScore(index, term);
			boolean found = false;
			for (Entry<String, Double> entry : newQVector) {
				if (entry.getKey().equalsIgnoreCase(term)) {
					entry.setValue(entry.getValue() + score);
					found = true;
					break;
				}
			}
			if (!found) {
				newQVector.add(new SimpleEntry<String, Double>(term, score));
			}
		}
		Collections.sort(newQVector, new ScoreComparator<String>());
		Collections.reverse(newQVector);
		StringBuilder rocchioTerms = new StringBuilder();
		for (int limit = 0; limit < termsLimit && limit < newQVector.size(); limit++) {
			rocchioTerms.append(' ').append(newQVector.get(limit).getKey());
		}
		Query rocchioQuery = new Query(original.getQid(), Utils.normalizeQuery(rocchioTerms.toString(), field, analyzer));
		return rocchioQuery;
	}

	private Directory createIndex(String indexDirectoryPath, List<Document> relevantDocs)
		throws CorruptIndexException, LockObtainFailedException, IOException {
		Directory indexDirectory = 
		         FSDirectory.open(new File(indexDirectoryPath));
		      StopWord stop = new StopWord("stopword_list_tala.txt");
		      
		      //create the indexer
		      IndexWriter idxWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(Version.LUCENE_36, new IndonesianAnalyzer(Version.LUCENE_36)).setSimilarity(new DefaultSimilarity()).setOpenMode(OpenMode.CREATE));
		for (int limit = 0; limit < docsLimit && limit < relevantDocs.size(); limit++) {
			idxWriter.addDocument(relevantDocs.get(limit));
		}
		idxWriter.close();
		return indexDirectory;
	}

	private List<Entry<String, Double>> getTermScoreList(Directory index) throws CorruptIndexException, IOException {
		Map<String, Double> termScoreMap = new HashMap<String, Double>();
		IndexReader idxreader = IndexReader.open(index);
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
				termScoreMap.put(term, (beta/(double)doclimit) * tfidf);
			}
		}
		idxreader.close();
		return new ArrayList<Entry<String, Double>>(termScoreMap.entrySet());
	}
}