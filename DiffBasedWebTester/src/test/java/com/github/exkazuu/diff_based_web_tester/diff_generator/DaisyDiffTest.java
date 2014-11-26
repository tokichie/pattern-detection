package com.github.exkazuu.diff_based_web_tester.diff_generator;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.github.exkazuu.diff_based_web_tester.diff_generator.daisy_diff.DaisyDiffGenerator;
import com.google.common.io.Resources;

public class DaisyDiffTest {
  @Test
  public void testUnifiedDiff() throws Exception {
    DaisyDiffGenerator generator = new DaisyDiffGenerator();
    String input1 =
        Resources.toString(Resources.getResource("diff_generator/original.html"),
            StandardCharsets.UTF_8);
    String input2 =
        Resources.toString(Resources.getResource("diff_generator/modified.html"),
            StandardCharsets.UTF_8);
    String diff = generator.generateDiffContent(input1, input2, System.lineSeparator());
    String expected =
        Resources.toString(Resources.getResource("diff_generator/daisydiff.diff"),
            StandardCharsets.UTF_8);
    assertEquals(expected, diff);
  }
}
