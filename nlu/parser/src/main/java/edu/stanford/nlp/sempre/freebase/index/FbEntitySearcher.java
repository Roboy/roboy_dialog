package edu.stanford.nlp.sempre.freebase.index;

import edu.stanford.nlp.sempre.roboy.utils.logging.LogInfoToggle;
import fig.basic.StopWatch;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class FbEntitySearcher {

  private final IndexSearcher indexSearcher;
  private int numOfDocs = 50;
  private String searchStrategy;
  private QueryParser queryParser;

  private synchronized QueryParser freshQueryParser(){//needed since queryparser is not threadsafe and queryparser is very lightweight
    return new QueryParser(
            Version.LUCENE_44,
            FbIndexField.TEXT.fieldName(),
            this.searchStrategy.equals("exact") ? new KeywordAnalyzer() : new StandardAnalyzer(Version.LUCENE_44));
  }


  public FbEntitySearcher(String indexDir, int numOfDocs, String searchingStrategy) throws IOException {

    LogInfoToggle.begin_track("Constructing Searcher");
    if (!searchingStrategy.equals("exact") && !searchingStrategy.equals("inexact"))
      throw new RuntimeException("Bad searching strategy: " + searchingStrategy);
    this.searchStrategy = searchingStrategy;

    queryParser = freshQueryParser();
    LogInfoToggle.log("Opening index dir: " + indexDir);
    IndexReader indexReader = DirectoryReader.open(SimpleFSDirectory.open(new File(indexDir)));
    indexSearcher = new IndexSearcher(indexReader);
    LogInfoToggle.log("Opened index with " + indexReader.numDocs() + " documents.");

    this.numOfDocs = numOfDocs;
    LogInfoToggle.end_track();
  }

  public List<Document> searchDocs(String question) throws IOException, ParseException {

    List<Document> res = new LinkedList<Document>();
    if (searchStrategy.equals("exact"))
      question = "\"" + question + "\"";

    ScoreDoc[] hits = getHits(question);

    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document doc = indexSearcher.doc(docId);
      res.add(doc);
    }
    return res;
  }

  private ScoreDoc[] getHits(String question) throws IOException, ParseException {
    Query luceneQuery;
    synchronized (queryParser) {
      luceneQuery = queryParser.parse(question);
      queryParser = freshQueryParser();
    }
    ScoreDoc[] hits = indexSearcher.search(luceneQuery, numOfDocs).scoreDocs;
    return hits;
  }

  public static void main(String[] args) throws IOException, ParseException {

    Pattern quit =
        Pattern.compile("quit|exit|q|bye", Pattern.CASE_INSENSITIVE);
    FbEntitySearcher searcher = new FbEntitySearcher(args[0], 10000, args[1]);
    BufferedReader is = new BufferedReader(new InputStreamReader(System.in));
    StopWatch watch = new StopWatch();
    while (true) {
      System.out.print("Search> ");
      String question = is.readLine().trim();
      if (quit.matcher(question).matches()) {
        System.out.println("Quitting.");
        break;
      }
      if (question.equals(""))
        continue;

      watch.reset();
      watch.start();
      List<Document> docs = searcher.searchDocs(question);
      watch.stop();
      for (Document doc : docs) {
        LogInfoToggle.log(
            "Mid: " + doc.get(FbIndexField.MID.fieldName()) + "\t" +
                "id: " + doc.get(FbIndexField.ID.fieldName()) + "\t" +
                "types: " + doc.get(FbIndexField.TYPES.fieldName()) + "\t" +
                "Name: " + doc.get(FbIndexField.TEXT.fieldName()) + "\t" +
                "Popularity: " + doc.get(FbIndexField.POPULARITY.fieldName()));
      }
      LogInfoToggle.logs("Number of docs: %s, Time: %s", docs.size(), watch);
    }
  }
}
