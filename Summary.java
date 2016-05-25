import java.util.*;
import java.io.*;

public class Summary {
    /**
    * Generates the word summary for a category node.
    */
    public static void generateSingleSummary(String NodeName,
            String DatabaseName, Set<WebDocument> documentsForSummary) {
        int total_docs = 0;
        int total_words = 0;

        Set<String> allWords = new TreeSet<String>();
        Map<String, Integer> tokenToIndex = new HashMap<String, Integer>();
        Map<Integer, String> indexToToken = new HashMap<Integer, String>();
        Map<Integer, Integer> tokenLastDoc = new HashMap<Integer, Integer>();
        Map<Integer, Integer> tokenDocFreq = new HashMap<Integer, Integer>();
        for (WebDocument d : documentsForSummary) {
            total_docs++;
            allWords.addAll(d.getWordTokens());
            for (String s : d.getWordTokens())  {
                if (tokenToIndex.get(s) == null) {
                    total_words++;
                    tokenToIndex.put(s, total_words);
                    indexToToken.put(total_words, s);
                    tokenLastDoc.put(total_words, total_docs);
                    tokenDocFreq.put(total_words, 1);
                } else {
                    int ind = tokenToIndex.get(s);
                    if (tokenLastDoc.get(ind) != total_docs) {
                        tokenLastDoc.put(ind, total_docs);
                        tokenDocFreq.put(ind, tokenDocFreq.get(ind) + 1);
                    }
                }
            }
        }

        String file_name = NodeName + "-" + DatabaseName + ".txt";
        try {
            PrintWriter writer = new PrintWriter(file_name, "UTF-8");
            for (String word : allWords) {
                int ind = tokenToIndex.get(word);
                int docfreq = tokenDocFreq.get(ind);
                writer.println(word + "#" + docfreq);
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Set<WebDocument> documentsForSummary = new HashSet<WebDocument>();
        // documentsForSummary.add(
        //     new WebDocument("cool.database", "http://karpathy.github.io/2015/10/25/selfie/"));
        // documentsForSummary.add(
        //     new WebDocument("cool.database", "http://karpathy.github.io/2015/05/21/rnn-effectiveness/"));
        documentsForSummary.add(
            new WebDocument("cool.database", "http://400pixels.net/uploadf/aa_bb_cc.html"));
        documentsForSummary.add(
            new WebDocument("cool.database", "http://400pixels.net/uploadf/bb_dd_ee.html"));
        generateSingleSummary("CoolNode", "cool.database", documentsForSummary);
    }
}
