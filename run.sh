EARCH_BASE=/home/gravano/Classes/E6111_f15/Html/Proj1
JAR_DIR=$SEARCH_BASE/jars
JAVACLASSPATH=$JAR_DIR/*:.

# Bing API
# Usage: bash run.sh <bing appId> <precision> <query>
# e.g. bash run.sh ghTYY7wD6LpyxUO9VRR7e1f98WFhHWYERMcw87aQTqQ 0.9 'gates'

#/usr/bin/java -classpath $JAVACLASSPATH:$SEARCH_BASE FeedbackBing "${1}" $2 "${3}" 1 2 0 $SEARCH_BASE
rm -f *.class;
javac MainClassify.java;
java MainClassify "${1}" $2 $3 "${4}";