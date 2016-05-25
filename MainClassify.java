import java.util.*;
import java.io.*;

public class MainClassify {
    public static String ACCOUNT_KEY;

    public static void main(String[] args) {
        // args databasename, coverageThreshold, specificityThreshold
        if (args.length != 4) {
            System.err.println("Usage: java MainClassify <BING_ACCOUNT_KEY>"
                               + " <t_es> <t_ec> <host>");
            return;
        }

        ACCOUNT_KEY = args[0];
        double specificityThreshold = Double.parseDouble(args[1]);
        int coverageThreshold = Integer.parseInt(args[2]);
        String databaseName = args[3];

        // Validate input. to make sure coverage >= 0 and
        //   0.0 <= specificity <= 1.0
        validateInputs(databaseName, coverageThreshold, specificityThreshold);


        System.out.println("=============================");
        System.out.println("====     WELCOME TO      ====");
        System.out.println("=============================");
        System.out.println("==                         ==");
        System.out.println("==   /~\\|~)._ _ |_  _._    ==");
        System.out.println("==   \\_X|~ | (_)|_)}_|     ==");
        System.out.println("==                         ==");
        System.out.println("=============================");
        System.out.println();

        System.out.println("account key: " + ACCOUNT_KEY);
        System.out.println("coverage threshold: " + coverageThreshold);
        System.out.println("specificity threshold: " + specificityThreshold);
        System.out.println("database name: " + databaseName);
        System.out.println();

        CategoryNode categoryTreeRootNode = null;
        try {
            System.out.println("Building category tree...");
            categoryTreeRootNode =
                CategoryNode.buildCategoryTree(databaseName);
            System.out.println("Building category tree...Done!");
        } catch (FileNotFoundException e) {
            System.err.println("Error: The expected files containing the query "
                               + "strings don't exist.");
            return;
        }

        System.out.println("Classifying database...");
        List<CategoryNode> categoryList = classify(categoryTreeRootNode,
                                          coverageThreshold,
                                          specificityThreshold);
        System.out.println("Classifying database...Done!");

        printCategories(databaseName, categoryList);

        // System.out.println(categoryTreeRootNode);

        System.out.println("Generating content summaries...");
        generateContentSummary(categoryTreeRootNode);
        System.out.println("Generating content summaries...Done!");

        printCategories(databaseName, categoryList);
    }


    public static void validateInputs(String databaseName, int coverageThreshold,
                                      double specificityThreshold) {
        if (coverageThreshold < 0) {
            throw new RuntimeException("Error: The coverage you used isn't "
                                       + "> 0.");
        }
        if (specificityThreshold < 0 || specificityThreshold > 1) {
            throw new RuntimeException("Error: The specificity you used isn't "
                                       + "in the range [0, 1].");
        }
    }


    public static void printCategories(String databaseName,
                                       List<CategoryNode> categoryList) {
        System.out.println();
        System.out.println("=============================");
        System.out.println("The database " + databaseName
                           + " falls into the following "
                           + (categoryList.size() == 1 ?
                              "category" : "categories")
                           + ":");
        for (CategoryNode category : categoryList) {
            System.out.println("- " + category.getFullName());
        }
        System.out.println("=============================");
        System.out.println();
    }


    public static List<CategoryNode> classify(CategoryNode categoryNode,
            int coverageThreshold,
            double specificityThreshold) {

        List<CategoryNode> result = new ArrayList<CategoryNode>();

        if (categoryNode.isLeaf()) {
            // Base case: reached a leaf node.
            result.add(categoryNode);
            categoryNode.isCategoryForDatabase = true;
            return result;
        }

        // Probe database D with the probes derived from the classifier for the
        // subcategories of C.

        // Calculate ECoverage from the number of matches for the probes.

        // Calculate the ESpecificity vector, using ECoverage(D)
        // and ESpecificity(D, C).

        // for all subcategories C_i of C
        for (CategoryNode subCategoryNode : categoryNode.children.values()) {
            int subCategoryCoverage = subCategoryNode.getECoverage();
            double subCategorySpecificity = subCategoryNode.getESpecificity();
            if (subCategorySpecificity >= specificityThreshold
                    && subCategoryCoverage >= coverageThreshold) {
                result.addAll(classify(subCategoryNode,
                                       coverageThreshold,
                                       specificityThreshold));
                if (subCategoryNode.isCategoryForDatabase) {
                    categoryNode.isCategoryForDatabase = true;
                }
            }
        }

        if (result.isEmpty()) {
            result.add(categoryNode);
            categoryNode.isCategoryForDatabase = true;
            return result;
        } else {
            return result;
        }
    }


    public static Set<WebDocument> generateContentSummary(
        CategoryNode categoryNode) {
        Set<WebDocument> queryDocumentsForSummary = new HashSet<WebDocument>();
        if (categoryNode.isCategoryForDatabase && !categoryNode.isLeaf()) {
            for (CategoryNode subCategoryNode :
                    categoryNode.children.values()) {
                // Add subcategory the top documents for this subcategory's
                // queries.
                queryDocumentsForSummary.addAll(
                    subCategoryNode.getAllQueryTopDocuments());
                // Recursive call.
                queryDocumentsForSummary.addAll(
                    generateContentSummary(subCategoryNode));
            }
            Summary.generateSingleSummary(categoryNode.name,
                                          categoryNode.databaseName,
                                          queryDocumentsForSummary);
        }
        return queryDocumentsForSummary;
    }
}