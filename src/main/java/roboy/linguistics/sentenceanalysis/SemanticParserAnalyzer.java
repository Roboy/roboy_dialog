package roboy.linguistics.sentenceanalysis;

import com.google.gson.stream.JsonReader;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.jena.base.Sys;
import roboy.linguistics.Linguistics;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.net.Socket;
import java.net.ConnectException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import roboy.linguistics.Triple;
import roboy.util.ConfigManager;


/**
 * Semantic parser class. Connects DM to Roboy parser and adds its result to interpretation class.
 */
public class SemanticParserAnalyzer implements Analyzer{

  private Socket clientSocket;  /**< Client socket for the parser */
  private PrintWriter out;      /**< Output stream for the parser */
  private BufferedReader in;    /**< Input stream from the parser */
  private boolean debug = true; /**< Boolean variable for debugging purpose */

  /**
   * A constructor.
   * Creates ParserAnalyzer class and connects the parser to DM using a socket.
   */
  public SemanticParserAnalyzer(int portNumber) {
    this.debug = ConfigManager.DEBUG;
    try {
      // Create string-string socket
      this.clientSocket = new Socket("localhost", portNumber);
      // Declaring input
      this.in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
      // Declaring output
      this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    } catch (IOException e) {
      System.err.println("Semantic Parser Client Error: " + e.getMessage());
    }
  }

  /**
   * An analyzer function.
   * Sends input sentence to the parser and saves its response in output interpretation.
   *
   * @param interpretation  Input interpretation with currently processed sentence
   *                        and results from previous analysis.
   * @return Input interpretation with semantic parser result.
   */
  @Override
  public Interpretation analyze(Interpretation interpretation) {
    if (this.clientSocket!=null && this.clientSocket.isConnected()) {
      try {
        String response;
        if (this.debug) {System.out.println("SEMANTIC PARSER:" + interpretation.getFeature("sentence")); }
        this.out.println(interpretation.getFeature("sentence"));
        response = this.in.readLine();
        if (this.debug) {
          System.out.println("> Full response:" + response);
        }
        if (response!=null) {
          // Convert JSON string back to Map.
          Gson gson = new Gson();
          Type type = new TypeToken<Map<String, Object>>(){}.getType();

          try {
            Map<String, Object> full_response = gson.fromJson(response, type);
            // Read formula and answer
            if (full_response.containsKey("parse")){
                if (full_response.get("answer").toString().equals("(no answer)"))
                    interpretation.getFeatures().put(Linguistics.PARSER_RESULT,Linguistics.PARSER_OUTCOME.FAILURE);
                else {
                    interpretation.getFeatures().put(Linguistics.PARSE_ANSWER, full_response.get("answer").toString());
                    interpretation.getFeatures().put(Linguistics.PARSE, full_response.get("parse").toString());
                    interpretation.getFeatures().put(Linguistics.SEM_TRIPLE, extract_triples(full_response.get("parse").toString()));
                    interpretation.getFeatures().put(Linguistics.PARSER_RESULT, Linguistics.PARSER_OUTCOME.SUCCESS);
                }
            }
            // Read followUp questions for underspecified terms
            if (full_response.containsKey("followUp")){
              interpretation.getFeatures().put(Linguistics.UNDERSPECIFIED_TERM_QUESTION, (Map<String,String>) full_response.get("followUp"));
              interpretation.getFeatures().put(Linguistics.PARSER_RESULT,Linguistics.PARSER_OUTCOME.UNDERSPECIFIED);
            }
            // Read tokens
            if (full_response.containsKey("tokens")) {
                interpretation.getFeatures().put(Linguistics.TOKENS, full_response.get("tokens").toString().split(","));
            }
            // Read extracted non-semantic relations
            if (full_response.containsKey("relations")) {
              interpretation.getFeatures().put(Linguistics.TRIPLE, extract_relations((Map<String,Double>)full_response.get("relations")));
            }
            // Read extracted sentiment
            if (full_response.containsKey("sentiment")) {
              interpretation.getFeatures().put(Linguistics.SENTIMENT, full_response.get("sentiment").toString());
            }
            // Read POS-tags
            if (full_response.containsKey("postags")) {
                interpretation.getFeatures().put(Linguistics.POSTAGS, full_response.get("postags").toString().split(","));
            }
            // Read lemmatized tokens
            if (full_response.containsKey("lemma_tokens")) {
                interpretation.getFeatures().put(Linguistics.LEMMAS, full_response.get("lemma_tokens").toString().split(","));
            }
            // Read utterance type
            if (full_response.containsKey("type")) {
              interpretation.getFeatures().put(Linguistics.UTTERANCE_TYPE, full_response.get("type").toString());
            }
          }
          catch (Exception e) {
            System.err.println("Exception while parsing semantic response: " + e.getStackTrace());
          }
        }
        return interpretation;
      }
      catch (IOException e) {
        e.printStackTrace();
        return interpretation;
      }
    }
    else
      return interpretation;
  }

  public List<Triple> extract_relations(Map<String, Double> relations){
    List<Triple> result = new ArrayList<>();
    for (String key: relations.keySet()){
      key = key.replaceAll("\\(","");
      key = key.replaceAll("\\)","");
      String[] triple = key.split(",");
      if (triple.length == 3)
        result.add(new Triple(triple[1],triple[0],triple[2]));
    }
    return result;
  }

  public List<Triple> extract_triples(String input){
    List<Triple> result = new ArrayList<>();
    input = input.replace(")"," )");
    input = input.replace("(","( ");
    String[] tokens = input.split(" ");
    for (int i = 0; i < tokens.length; i++)
    {
      if (tokens[i].contains("triple") && i+3 < tokens.length && !tokens[i].contains("triples"))
      {
        result.add(new Triple(tokens[i+2], tokens[i+1], tokens[i+3]));
      }
      else if (tokens[i].contains("(") && i+1 < tokens.length && !tokens[i+1].contains("triple"))
      {
        if (tokens[i].contains("!"))
          result.add(new Triple(tokens[i], tokens[i+1], null));
        else
          result.add(new Triple( tokens[i], null, tokens[i+1]));
      }
    }
    return result;
  }

  public static void main(String[] args) {
    SemanticParserAnalyzer analyzer = new SemanticParserAnalyzer(ConfigManager.PARSER_PORT);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String line;
      System.out.print("Enter utterance: ");
      while ((line = reader.readLine()) != null) {
        Interpretation inter = new Interpretation(line);
        analyzer.analyze(inter);
        for (String key: inter.getFeatures().keySet()) {
          System.out.println(key + " : " + inter.getFeature(key).toString());
        }
        if (inter.getFeatures().keySet().contains(Linguistics.UNDERSPECIFIED_TERM_QUESTION)) {
            for (String key : ((Map<String, String>) inter.getFeature(Linguistics.UNDERSPECIFIED_TERM_QUESTION)).keySet()) {
                System.out.println(key + " : " + ((Map<String, String>) inter.getFeature(Linguistics.UNDERSPECIFIED_TERM_QUESTION)).get(key));
            }
        }
      }
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

}