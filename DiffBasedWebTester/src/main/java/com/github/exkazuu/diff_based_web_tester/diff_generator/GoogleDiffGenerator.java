package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.util.List;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * Generate diff with java-diff-utils library which implements Myer's diff algorithm.
 * 
 * {@see http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.4.6927}. {@see
 * https://code.google.com/p/java-diff-utils/}.
 */
public class GoogleDiffGenerator extends HtmlDiffGenerator {
  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    diff_match_patch diff = new diff_match_patch();
    List<Diff> differences = diff.diff_main(input1, input2);
    StringBuilder html = new StringBuilder();
    for (Diff aDiff : differences) {
      String text = aDiff.text;
      switch (aDiff.operation) {
        case INSERT:
          html.append("<ins class='diff'>").append(text).append("</ins>");
          break;
        case DELETE:
          html.append("<del class='diff'>").append(text).append("</del>");
          break;
        case EQUAL:
          break;
      }
    }
    return html.toString();
  }
}
