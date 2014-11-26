package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.util.Arrays;
import java.util.List;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Generate diff with java-diff-utils library which implements Myer's diff algorithm.
 * 
 * {@see http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.4.6927}. {@see
 * https://code.google.com/p/java-diff-utils/}.
 */
public class MyersDiffGenerator extends HtmlDiffGenerator {
  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    List<String> originalContent = Arrays.asList(input1.split(lineSeparator));
    List<String> revisedContent = Arrays.asList(input2.split(lineSeparator));
    Patch patch = DiffUtils.diff(originalContent, revisedContent);

    StringBuilder builder = new StringBuilder();
    for (Delta delta : patch.getDeltas()) {
      Chunk original = delta.getOriginal();
      Chunk revised = delta.getRevised();
      StartAndCount originalCount = new StartAndCount(original);
      StartAndCount revisedCount = new StartAndCount(revised);
      builder.append("@@ ");
      if (originalCount.getCount() > 0) {
        builder.append("-").append(originalCount).append(" ");
      }
      if (revisedCount.getCount() > 0) {
        builder.append("+").append(revisedCount).append(" ");
      }
      builder.append("@@").append(System.lineSeparator());
      for (Object originalLine : original.getLines()) {
        builder.append("-" + originalLine).append(System.lineSeparator());
      }
      for (Object originalLine : revised.getLines()) {
        builder.append("+" + originalLine).append(System.lineSeparator());
      }
    }
    return builder.toString();
  }

  private class StartAndCount {
    private final int start;
    private final int count;

    private StartAndCount(Chunk chunk) {
      start = chunk.getPosition();
      count = chunk.getLines().size();
    }

    public int getCount() {
      return count;
    }

    @Override
    public String toString() {
      return start + "," + count;
    }
  }
}
