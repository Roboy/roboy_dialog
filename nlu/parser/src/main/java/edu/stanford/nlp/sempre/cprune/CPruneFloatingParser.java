package edu.stanford.nlp.sempre.cprune;

import java.util.List;

import edu.stanford.nlp.sempre.*;
import edu.stanford.nlp.sempre.roboy.utils.logging.LogInfoToggle;

/**
 * A parser that first tries to exploit the macro grammar and only fall back to full search when needed.
 */
public class CPruneFloatingParser extends FloatingParser {

  FloatingParser exploreParser;

  public CPruneFloatingParser(Spec spec) {
    super(spec);
    exploreParser = new FloatingParser(spec).setEarlyStopping(true, CollaborativePruner.opts.maxDerivations);
  }

  @Override
  public void onBeginDataGroup(int iter, int numIters, String group) {
    if (CollaborativePruner.uidToCachedNeighbors == null) {
      CollaborativePruner.customGrammar.init(grammar);
      CollaborativePruner.loadNeighbors();
    }
    CollaborativePruner.stats.reset(iter + "." + group);
  }

  @Override
  public ParserState newParserState(Params params, Example ex, boolean computeExpectedCounts) {
    return new CPruneFloatingParserState(this, params, ex, computeExpectedCounts);
  }

}

class CPruneFloatingParserState extends ParserState {

  public CPruneFloatingParserState(Parser parser, Params params, Example ex, boolean computeExpectedCounts) {
    super(parser, params, ex, computeExpectedCounts);
  }

  @Override
  public void infer() {
    LogInfoToggle.begin_track("CPruneFloatingParser.infer()");
    boolean exploitSucceeds = exploit();
    if (computeExpectedCounts) {
      LogInfoToggle.begin_track("Summary of Collaborative Pruning");
      LogInfoToggle.logs("Exploit succeeds: " + exploitSucceeds);
      LogInfoToggle.logs("Exploit success rate: " + CollaborativePruner.stats.successfulExploit + "/" + CollaborativePruner.stats.totalExploit);
      LogInfoToggle.end_track();
    }
    // Explore only on the first training iteration
    if (CollaborativePruner.stats.iter.equals("0.train") && computeExpectedCounts && !exploitSucceeds
        && (CollaborativePruner.stats.totalExplore <= CollaborativePruner.opts.maxExplorationIters)) {
      explore();
      LogInfoToggle.logs("Consistent pattern: " + CollaborativePruner.getConsistentPattern(ex));
      LogInfoToggle.logs("Explore success rate: " + CollaborativePruner.stats.successfulExplore + "/" + CollaborativePruner.stats.totalExplore);
    }
    LogInfoToggle.end_track();
  }

  @Override
  public void execute() {
  }

  public void explore() {
    LogInfoToggle.begin_track("Explore");
    CollaborativePruner.initialize(ex, CollaborativePruner.Mode.EXPLORE);
    ParserState exploreParserState = ((CPruneFloatingParser) parser).exploreParser.newParserState(params, ex, computeExpectedCounts);
    exploreParserState.infer();
    predDerivations.clear();
    predDerivations.addAll(exploreParserState.predDerivations);
    expectedCounts = exploreParserState.expectedCounts;
    if (computeExpectedCounts) {
      for (Derivation deriv : predDerivations)
        CollaborativePruner.updateConsistentPattern(parser.valueEvaluator, ex, deriv);
    }
    CollaborativePruner.stats.totalExplore += 1;
    if (CollaborativePruner.foundConsistentDerivation)
      CollaborativePruner.stats.successfulExplore += 1;
    LogInfoToggle.end_track();
  }

  public boolean exploit() {
    LogInfoToggle.begin_track("Exploit");
    CollaborativePruner.initialize(ex, CollaborativePruner.Mode.EXPLOIT);
    Grammar miniGrammar = new MiniGrammar(CollaborativePruner.predictedRules);
    Parser exploitParser = new FloatingParser(new Parser.Spec(miniGrammar, parser.extractor, parser.executor, parser.valueEvaluator));
    ParserState exploitParserState = exploitParser.newParserState(params, ex, computeExpectedCounts);
    exploitParserState.infer();
    predDerivations.clear();
    predDerivations.addAll(exploitParserState.predDerivations);
    expectedCounts = exploitParserState.expectedCounts;
    if (computeExpectedCounts) {
      for (Derivation deriv : predDerivations)
        CollaborativePruner.updateConsistentPattern(parser.valueEvaluator, ex, deriv);
    }
    boolean succeeds = CollaborativePruner.foundConsistentDerivation;
    CollaborativePruner.stats.totalExploit += 1;
    if (succeeds)
      CollaborativePruner.stats.successfulExploit += 1;
    LogInfoToggle.end_track();
    return succeeds;
  }
}

// ============================================================
// Helper classes
// ============================================================

class MiniGrammar extends Grammar {

  public MiniGrammar(List<Rule> rules) {
    this.rules.addAll(rules);
    if (CollaborativePruner.opts.verbose >= 2) {
      LogInfoToggle.begin_track("MiniGrammar Rules");
      for (Rule rule : rules)
        LogInfoToggle.logs("%s %s", rule, rule.isAnchored() ? "[A]" : "[F]");
      LogInfoToggle.end_track();
    }
  }

}
