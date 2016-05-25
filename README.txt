=================================================================
|                           Project 2                           |
|                                                               |
|Team Members: Vibhor Gaur (vg2376) and Joaquín Ruales (jar2262)|
=================================================================

-------------------------------------
List of all files that we are submitting:
-------------------------------------
- Code:
    
    - MainClassify.java
    - CategoryNode.java
    - Query.java
    - WebDocument.java
    - GetWordsLynx
    - Summary.java     
    - run.sh

- Library
    -org.apache.commons.codec.binary.Base64

- Documentation:
    - README.txt

--------------------------------------------------------------------
A clear description of how to run our program (in the clic machines)
--------------------------------------------------------------------
To run, use:
> bash run.sh <account_key> <Coverage_Threshold> <Specificity_Threshold> <Database_Name> 

The values for coverage and specificity thresholds used are same as given in the project:

Coverage_Threshold = 100
Specificity_Threshold = 0.6

For example:
For diabetes.org

> bash run.sh ghTYY7wD6LpyxUO9VRR7e1f98WFhHWYERMcw87aQTqQ 100 0.6 diabetes.org 

---------------------------------------------------------
A clear description of the internal design of our project for each part
---------------------------------------------------------
Our project is designed as follows


>>>>Algorithm Design<<<<

Part 1. Web Database Classification

The algorithm used is the same as outlined in the paper "QProber: A System for Automatic Classification of Hidden-Web Databases" paper by Gravano, Ipeirotis, and Sahami.

Part 2. Distributed Search over Web Databases

2a.Document Sampling

We use the document sampling method as outlined in the project page. We use the data structure "set" so that duplicate documents are eliminated (where equality is determined based on the url). For the current node we simply add documents sampled for this node to the set of the documents sampled from its child nodes. Since, we use a set, duplicate documents are automatically eliminated at each node.

2b.Content Summary construction

For each Category Node (except the leaf node) in the final classification result for the input database we use the documents sampled for this node (duplicates removed) in part 2 a. and generate the summary using the method outlined in the project.The name of the corresponding text file for each summary is generated in the format outlined in the project.

>>>>Code Design<<<<

MainClassify.java
--------------
Runs the recursive implementation outlined in the algorithm for classifying the input web database. It also samples the documents at each node that will be required to generate the summary for that node. We use the data structure "set" so that duplicate documents are eliminated. For the current node we simply add documents sampled for this node to the set of the documents sampled from its child nodes. Since we use a set, duplicate documents are automatically eliminated. 

CategoryNode.java
----------
This class contains attributes and methods associated with a particular node in the given hierarchical representation of category nodes. It computes the specificity and coverage for a category node as required by MainClassify.java.

Query.Java 
-----------------
Processes queries assosciated with a CategoryNode and the input database.We first check the cache to see if we already have results for this query.If yes we set the numResults and topResults for this query by reading the cache corresponding to the particular query and database else we proceed to get these results in this cache.It first serializes the query to remove spaces and then constructs the url based on the query and input database (format given in project)to get the top 4 results .We then parse the generated xml to get the count of the number of matching documents and the top 4 urls ( to be used for generating summary).

WebDocument.java
--------------
This class contains attributes and methods associated with documents(url,word tokens). The documents will be sampled in MainClassify.java and used for generating the summary for a particular node in the recursion. The file summary.java uses the word tokens attribute associated with the documents to get the tokens for generating summaries for each document.

GetWordsLynx.java
-------------
Code provided in the project for getting tokens from the HTML for generating output summary.

Summary.java
-----------
Code to generate summary for each category node (except leaf node) in the final classification result for the input database.The summary and the name of the corresponding text file are generated in the format outlined in the project. 

run.sh
------
Bash commands to run the project.

--------------------------------------------------------------------
Rule for generating document summary
--------------------------------------------------------------------

For generating documents summaries we do not include multiple-word information (e.g. bigrams) in the content summaries, only single word document frequencies.


-----------------------
Bing Search Account Key
-----------------------
ghTYY7wD6LpyxUO9VRR7e1f98WFhHWYERMcw87aQTqQ

----------------------------------------------------
Additional information that we consider significant.
----------------------------------------------------
N/A
