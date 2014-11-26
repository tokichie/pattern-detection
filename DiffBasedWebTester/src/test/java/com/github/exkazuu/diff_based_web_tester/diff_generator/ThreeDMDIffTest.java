package com.github.exkazuu.diff_based_web_tester.diff_generator;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.Resources;

public class ThreeDMDIffTest {
  @Test
  public void testUnifiedDiff() throws Exception {
    HtmlDiffGenerator generator = new ThreeDMDiffGenerator();
    String input1 =
        Resources.toString(Resources.getResource("diff_generator/original.html"),
            StandardCharsets.UTF_8);
    String input2 =
        Resources.toString(Resources.getResource("diff_generator/modified.html"),
            StandardCharsets.UTF_8);
    String diff = generator.generateDiffContent(input1, input2, System.lineSeparator());
    String expected =
        Resources
            .toString(Resources.getResource("diff_generator/3dm.diff"), StandardCharsets.UTF_8);
    assertEquals(expected, diff);
  }
}
