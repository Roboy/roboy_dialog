package edu.stanford.nlp.sempre;

import java.util.*;

import edu.stanford.nlp.sempre.roboy.utils.logging.*;
import fig.basic.Evaluation;
import fig.basic.Parallelizer;
import fig.exec.Execution;

/**
 * Parallel version of the Learner.
 *
 * Most of the codes are copied from the paraphrase package.
 *
 * @author ppasupat
 */
public class LearnerParallelProcessor implements Parallelizer.Processor<Example> {

  private final Parser parser;
  private final String prefix;
  private final boolean computeExpectedCounts;
  private Params params;         // this is common to threads and should be synchronized
  private Evaluation evaluation; // this is common to threads and should be synchronized

  public LearnerParallelProcessor(Parser parser, Params params, String prefix, boolean computeExpectedCounts, Evaluation evaluation) {
    this.prefix = prefix;
    this.parser = parser;
    this.computeExpectedCounts = computeExpectedCounts;
    this.params = params;
    this.evaluation = evaluation;
  }

  @Override
  public void process(Example ex, int i, int n) {
    LogInfoToggle.begin_track_printAll(
        "%s: example %s/%s: %s", prefix, i, n, ex.id);
    ex.log();
    Execution.putOutput("example", i);

    StopwatchSetToggle.begin("Parser.parse");
    ParserState state = parser.parse(params, ex, computeExpectedCounts);
    StopwatchSetToggle.end();

    if (computeExpectedCounts) {
      Map<String, Double> counts = new HashMap<>();
      SempreUtils.addToDoubleMap(counts, state.expectedCounts);

      // Gathered enough examples, update parameters
      StopwatchSetToggle.begin("Learner.updateWeights");
      LogInfoToggle.begin_track("Updating learner weights");
      if (Learner.opts.verbose >= 2)
        SempreUtils.logMap(counts, "gradient");
      double sum = 0;
      for (double v : counts.values()) sum += v * v;
      LogInfoToggle.logs("L2 norm: %s", Math.sqrt(sum));
      synchronized (params) {
        params.update(counts);
      }
      counts.clear();
      LogInfoToggle.end_track();
      StopwatchSetToggle.end();
    }

    LogInfoToggle.logs("Current: %s", ex.evaluation.summary());
    synchronized (evaluation) {
      evaluation.add(ex.evaluation);
      LogInfoToggle.logs("Cumulative(%s): %s", prefix, evaluation.summary());
    }

    LogInfoToggle.end_track();

    // To save memory
    ex.clean();
  }

}
