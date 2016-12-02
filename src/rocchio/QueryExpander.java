/**
 * query expander
 * @author c00kiemon5ter
 */
package rocchio;

import core.Query;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;

/**
 *
 * @author c00kiemon5ter
 */
public interface QueryExpander {

	Query expand(final Query original, final List<Document> relevantDocs, String indexDirectoryPath)
		throws ParseException, CorruptIndexException,
		       LockObtainFailedException, IOException;

	
}