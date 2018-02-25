package roboy.linguistics.sentenceanalysis;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import roboy.linguistics.Linguistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Map;

import java.net.Socket;
import java.net.ConnectException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


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
    this.debug = true;
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
        if (response!=null && !response.contains("no answer")) {
          // Convert JSON string back to Map.
          Gson gson = new Gson();
          Type type = new TypeToken<Map<String, Object>>(){}.getType();

          try {
            Map<String, Object> full_response = gson.fromJson(response, type);
            if (this.debug) {
              System.out.println("> Parse:" + full_response.get("parse"));
            }
            if (this.debug) {
              System.out.println("> Answer:"+ full_response.get("answer"));
            }
            if (full_response.containsKey("parse")){
              interpretation.getFeatures().put(Linguistics.PARSE_ANSWER, full_response.get("answer").toString());
              interpretation.getFeatures().put(Linguistics.PARSE, full_response.get("parse").toString());
            }
            if (full_response.containsKey("tokens")) {
                interpretation.getFeatures().put(Linguistics.TOKENS, full_response.get("tokens").toString().split(","));
            }
            if (full_response.containsKey("postags")) {
                interpretation.getFeatures().put(Linguistics.POSTAGS, full_response.get("postags").toString().split(","));
            }
            if (full_response.containsKey("lemma_tokens")) {
                interpretation.getFeatures().put(Linguistics.LEMMAS, full_response.get("lemma_tokens").toString().split(","));
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

}