package com.github.exkazuu.diff_based_web_tester.diff_generator;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

import com.github.exkazuu.diff_based_web_tester.diff_generator.xdiff.XDiffGenerator;
import org.junit.Test;

import com.google.common.io.Resources;

public class XDiffTest {
  @Test
  public void testUnifiedDiff() throws Exception {
    XDiffGenerator generator = new XDiffGenerator();
    String input1 =
        Resources.toString(Resources.getResource("diff_generator/original.html"),
            StandardCharsets.UTF_8);
    String input2 =
        Resources.toString(Resources.getResource("diff_generator/modified.html"),
            StandardCharsets.UTF_8);
    String diff = generator.generateDiffContent(input1, input2, System.lineSeparator());
    String expected =
        Resources.toString(Resources.getResource("diff_generator/xdiff.diff"),
            StandardCharsets.UTF_8);
    equalsIgnoreSpaceAndLinebreaks(expected, diff);
  }

  private void equalsIgnoreSpaceAndLinebreaks(String expected, String diff) {
    assertEquals(expected.replaceAll("[\\s|\\n|\\r]", ""), diff.replaceAll("[\\s|\\n|\\r]", ""));
  }
}
