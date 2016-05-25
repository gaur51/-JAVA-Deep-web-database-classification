import java.util.*;

/**
* Represents a web page. We store the page's url an the words that it contains.
*/
public class WebDocument {
	public String databaseName;
	public String url;
	private TreeSet<String> wordTokens;


	/**
	* Initializes a document object based only on the document's URL, using the
	* GetWordsLynx class to get word counts.
	*/
	public WebDocument(String databaseName, String url) {
		this.databaseName = databaseName;
		this.url = url;

		this.wordTokens = null;
	}


	/**
	* Initializes a document object based on its fields.
	*/
	public WebDocument(String databaseName, String url, Set<String> wordTokens) {
		this.databaseName = databaseName;
		this.url = url;
		this.wordTokens = new TreeSet<String>(wordTokens);
	}


	/**
	* Lazily load word tokens.
	*/
	public Set<String> getWordTokens() {
		if (this.wordTokens != null) {
			return this.wordTokens;
		}

		this.wordTokens = new TreeSet<String>();
		// TODO(Joaquín): uncomment the following.
		this.wordTokens = new TreeSet<String>(GetWordsLynx.runLynx(url));
		
		System.out.println("Processing url: " + url);

		return this.wordTokens;
	}


	@Override
	public int hashCode() {
		return this.url.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		WebDocument other = (WebDocument) obj;

		return this.url.equals(other.url);
	}
}