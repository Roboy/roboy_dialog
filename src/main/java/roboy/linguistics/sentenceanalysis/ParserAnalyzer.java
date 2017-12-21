package roboy.linguistics.sentenceanalysis;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import roboy.linguistics.Linguistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;


/**
 * Semantic parser class. Connects DM to Roboy parser and adds its result to interpretation class.
 */
public class ParserAnalyzer implements Analyzer{

  private Socket clientSocket;  /**< Client socket for the parser */
  private PrintWriter out;      /**< Output stream for the parser */
  private BufferedReader in;    /**< Input stream from the parser */
  private boolean debug = true; /**< Boolean variable for debugging purpose */

  /**
   * A constructor.
   * Creates ParserAnalyzer class and connects the parser to DM using a socket.
   */
  public ParserAnalyzer(int portNumber) {
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
        if (this.debug) {System.out.println("> Full response:" + response);}
        try {
          if (this.debug) System.out.println("> Parse:" + response.substring(0, response.indexOf("=>")));
          if (this.debug) System.out.println("> Answer:" + response.substring(response.indexOf("=>") + 3));
          interpretation.getFeatures().put(Linguistics.PARSE, response.substring(0, response.indexOf("=>")));
          interpretation.getFeatures().put(Linguistics.PRED_ANSWER, response.substring(response.indexOf("=>") + 3));
        } catch (RuntimeException e) {
          System.err.println("Exception while parsing intent response: " + e.getStackTrace());
        }
        return interpretation;
      } catch (IOException e) {
        e.printStackTrace();
        return interpretation;
      }
    }
    else
      return interpretation;
  }

}