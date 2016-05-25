import java.util.*;
import java.io.*;

public class CategoryNode {
    public String databaseName;
    public String name;
    public String queryFilename;
    public Set<Query> queries;
    public TreeMap<String, CategoryNode> children;
    public CategoryNode parent;
    public boolean isCategoryForDatabase;

    private Integer coverage = null;
    private Double specificity = null;


    public CategoryNode(String databaseName, String name, String queryFilename) {
        this.databaseName = databaseName;
        this.name = name;
        this.queryFilename = queryFilename;
        this.queries = new LinkedHashSet<Query>();
        this.parent = null;
        this.children = new TreeMap<String, CategoryNode>();
        isCategoryForDatabase = false;
    }


    public boolean isLeaf() {
        return children.isEmpty();
    }


    public boolean isRoot() {
        return parent == null;
    }


    public String getFullName() {
        if (this.isRoot()) {
            return this.name;
        } else {
            return this.parent.getFullName() + "/" + this.name;
        }
    }


    @Override
    public String toString() {
        String returnable =
            "Category name: " + name + "\n"
            + "Query filename: " + "\n"
            + "Queries: " + queries + "\n"
            + "isCategoryForDatabase: " + isCategoryForDatabase + "\n"
            + "Children:\n";
        for (CategoryNode child : children.values()) {
            returnable += "{\n  " + child.toString().replace("\n", "\n ")
                          + "}\n";
        }
        return returnable;
    }


    public void addChild(CategoryNode child) {
        children.put(child.name, child);
        child.parent = this;
    }


    public void loadAndOrganizeQueryStrings() throws FileNotFoundException {
        if (this.isLeaf()) {
            // Base case.
            return;
        }

        // Read file with query strings.
        Scanner file = new Scanner(new File(this.queryFilename));

        while (file.hasNext()) {
            String childName = file.next();
            String queryString = file.nextLine().trim();
            CategoryNode relevantChild = this.children.get(childName);
            relevantChild.queries.add(new Query(this.databaseName, queryString));
        }

        for (CategoryNode child : this.children.values()) {
            child.loadAndOrganizeQueryStrings();
        }
    }


    public static CategoryNode buildCategoryTree(String databaseName)
    throws FileNotFoundException {
        CategoryNode rootNode = new CategoryNode(databaseName, "Root", "data/root.txt");

        CategoryNode computersNode = new CategoryNode(databaseName, "Computers",
                "data/computers.txt");
        CategoryNode hardwareNode = new CategoryNode(databaseName, "Hardware", null);
        CategoryNode programmingNode = new CategoryNode(databaseName, "Programming", null);

        CategoryNode healthNode = new CategoryNode(databaseName, "Health", "data/health.txt");
        CategoryNode fitnessNode = new CategoryNode(databaseName, "Fitness", null);
        CategoryNode diseasesNode = new CategoryNode(databaseName, "Diseases", null);

        CategoryNode sportsNode = new CategoryNode(databaseName, "Sports", "data/sports.txt");
        CategoryNode basketballNode = new CategoryNode(databaseName, "Basketball", null);
        CategoryNode soccerNode = new CategoryNode(databaseName, "Soccer", null);

        computersNode.addChild(hardwareNode);
        computersNode.addChild(programmingNode);
        rootNode.addChild(computersNode);

        healthNode.addChild(fitnessNode);
        healthNode.addChild(diseasesNode);
        rootNode.addChild(healthNode);

        sportsNode.addChild(basketballNode);
        sportsNode.addChild(soccerNode);
        rootNode.addChild(sportsNode);

        rootNode.loadAndOrganizeQueryStrings();

        // System.out.println("The tree has been created:");
        // System.out.println(rootNode);

        return rootNode;
    }


    public int getECoverage() {
        if (this.isRoot()) {
            throw new RuntimeException("Coverage undefined for root node!");
        }
        if (coverage != null) {
            return coverage;
        }
        int r;
        int results = 0;

        //for each query assosciated with this node get the number of results = r;
        for (Query q : this.queries) {
            r = q.getNumResults();
            results += r;
        }

        this.coverage = results;
        return results;
    }


    public double getESpecificity() {
        if (this.isRoot()) {
            return 1.0;
        }
        if (specificity != null) {
            return specificity;
        }

        double spec;
        double parspec = this.parent.getESpecificity();
        int cov = this.getECoverage();
        int totalcov = 0;
        for (CategoryNode c : this.parent.children.values()) {
            totalcov += c.getECoverage();
        }

        spec = parspec * (double)cov / (double)totalcov;
        this.specificity = spec;
        return spec;
    }


    public Set<WebDocument> getAllQueryTopDocuments() {
        Set<WebDocument> returnable = new HashSet<WebDocument>();
        for (Query query : this.queries) {
            returnable.addAll(query.getTopResults());
        }
        return returnable;
    }
}