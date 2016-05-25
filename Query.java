import java.util.*;
import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.commons.codec.binary.Base64;

/**
* Contains a query string. Once executeQuery() has been called, this class also
* contains the .
*/
public class Query implements Comparable<Query> {
    public String databaseName;
    public String queryString;

    private Integer numResults;
    private HashSet<WebDocument> topResults;
    private boolean executed;


    ////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////////////////////////////////
    public Query(String databaseName, String queryString) {
        this.databaseName = databaseName;
        this.queryString = queryString;
        this.numResults = null;
        this.topResults = null;
        executed = false;
    }


    ////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ////////////////////////////////////////////////////////////////////////////

    public boolean hasExecuted() {
        return executed;
    }


    /**
    * Runs the bing query and populates the numResults, topResults, and
    * executed fields.
    */
    public void executeQuery() {
        if (this.hasExecuted()) {
            return;
        }

        this.topResults = new HashSet<WebDocument>();

        // Check if result is cached. If it is, read cached result.
        try {
            Scanner matchesScanner = new Scanner(new File("cache/matches/"
                                                 + this.databaseName + "/"
                                                 + this.queryString));
            Scanner urlsScanner = new Scanner(new File("cache/urls/"
                                              + this.databaseName + "/"
                                              + this.queryString));

            this.numResults = matchesScanner.nextInt();

            while (urlsScanner.hasNextLine() && this.topResults.size() < 4) {
                String thisUrl = urlsScanner.nextLine();
                if (!thisUrl.isEmpty()) {
                    this.topResults.add(new WebDocument(this.databaseName,
                                                        thisUrl));
                }
            }

            executed = true;
            return;
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // Cached files not found, so proceed with retrieving the files.
        } catch (InputMismatchException e) {
        }


        System.out.println("Querying Bing for query: " + this.queryString);


        // TODO(Joaquín): run bing query to get count and top urls.
        // TODO(Joaquín): turn urls into counts.
        String bingAccountKey = MainClassify.ACCOUNT_KEY;
        // String bingAccountKey = "ghTYY7wD6LpyxUO9VRR7e1f98WFhHWYERMcw87aQTqQ";
        String bingUrl = getQueryURL();
        String accountKey = bingAccountKey;

        byte[] accountKeyBytes
            = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);

        try {
            URL url = new URL(bingUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization",
                                             "Basic " + accountKeyEnc);
            InputStream inputStream = (InputStream) urlConnection.getContent();
            byte[] contentRaw = new byte[urlConnection.getContentLength()];
            inputStream.read(contentRaw);
            String content = new String(contentRaw);
            try {
                File file = new File("Results.xml");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(content);
                //fileWriter.write("a test");
                fileWriter.flush();
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                //Using factory get an instance of document builder
                DocumentBuilder db = dbf.newDocumentBuilder();

                //parse using builder to get DOM representation of the XML file
                Document dom = db.parse("Results.xml");
                Element docEle = (Element) dom.getDocumentElement();

                //get a nodelist of elements

                NodeList n1 = docEle.getElementsByTagName("d:Url");
                NodeList count = docEle.getElementsByTagName("d:WebTotal");
                Element e2 = (Element)count.item(0);
                numResults = Integer.parseInt(e2.getTextContent());
                String matches_dir_name = "cache/matches/" + databaseName;
                String urls_dir_name = "cache/urls/" + databaseName;
                String matches_name = matches_dir_name + "/" + queryString;
                String urls_name = urls_dir_name + "/" + queryString;

                new File(matches_dir_name).mkdirs();
                new File(urls_dir_name).mkdirs();

                try {
                    PrintWriter writer1 = new PrintWriter(matches_name, "UTF-8");
                    writer1.println(numResults);
                    writer1.close();
                    PrintWriter writer2 = new PrintWriter(urls_name, "UTF-8");
                    for (int i = 0; i < Math.min(n1.getLength(), 4); i++) {
                        Element e1 = (Element)n1.item(i);
                        String url_store = e1.getTextContent();
                        topResults.add(new WebDocument(this.databaseName, url_store));
                        writer2.println(url_store);
                    }
                    writer2.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (SAXException se) {
                se.printStackTrace();
            } catch (ParserConfigurationException pe) {
                pe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executed = true;
    }


    public int getNumResults() {
        // Lazily load number of results.
        if (!this.hasExecuted()) {
            this.executeQuery();
        }
        return numResults;
    }


    public Set<WebDocument> getTopResults() {
        // Lazily load top results.
        if (!this.hasExecuted()) {
            this.executeQuery();
        }
        if (this.topResults.size() > 4) {
            throw new RuntimeException("Error: We have stored more than 4 results for a query!");
        }
        return topResults;

    }


    public String getQueryURL() {
        // https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a
        return "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a"
               +  databaseName + "%20" + this.serialize() + "%27&$top=4&$format=Atom";
    }


    public String toString() {
        return queryString;
    }


    public int compareTo(Query other) {
        return this.queryString.compareTo(other.queryString);
    }


    @Override
    public int hashCode() {
        return this.queryString.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Query other = (Query) obj;

        return this.queryString.equals(other.queryString);
    }

    ////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////////////////


    private void initialize(int numResults, TreeSet<WebDocument> topResults) {
        this.numResults = numResults;
        this.topResults = new HashSet<WebDocument>(topResults);
        executed = true;
    }


    public String serialize() {
        try {
            return URLEncoder.encode(this.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}