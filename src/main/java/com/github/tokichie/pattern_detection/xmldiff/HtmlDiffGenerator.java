package com.github.tokichie.pattern_detection.xmldiff;

public abstract class HtmlDiffGenerator {
  public abstract String generateDiffContent(String input1, String input2, String lineSeparator);

  public String generateDiffContent(String input1, String input2) {
    return generateDiffContent(input1, input2, System.lineSeparator());
  }
}
