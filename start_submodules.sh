# start memory
# make sure you have the following env variables set
# export NEO4J_USERNAME=
# export NEO4J_ADDRESS=
# export NEO4J_PASSWORD=

{ # try
    rostopic list
} || { # catch
    echo "[INFO]: ROS core wasn't running. Starting now..."
    roscore
}


echo "[INFO]: Checking if the environment is set correctly"
declare -a arr=(NEO4J_USERNAME NEO4J_ADDRESS NEO4J_PASSWORD)
for var in "${arr[@]}"
do
   if [[ ! ${!var} ]]; then echo "$var environmental variable is unset. Memory module is not going to work properly" && exit; else echo "'$var' is set"; fi
done

echo "[INFO]: Compiling memory"
cd roboy_memory
mvn install

echo "[INFO]: Starting memory"
cd target
java -jar roboy_memory-1.0.0-jar-with-dependencies.jar &

echo "[INFO]: Compiling parser"
cd ../../roboy_parser
mvn clean
mvn install

echo "[INFO]: Starting parser"
mvn exec:java@demo -Dexec.mainClass=edu.stanford.nlp.sempre.Main