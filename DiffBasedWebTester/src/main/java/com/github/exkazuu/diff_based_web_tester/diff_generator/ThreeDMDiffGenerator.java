package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.io.ByteArrayOutputStream;
import java.io.File;

import tdm.lib.BaseNode;
import tdm.lib.BranchNode;
import tdm.lib.Diff;
import tdm.lib.DiffMatching;
import tdm.lib.XMLParser;
import tdm.lib.XMLPrinter;

import com.google.common.io.Files;

/**
 * Generate diff with "3DM" XML 3-way Merging and Differencing Tool. {@see
 * http://www.cs.hut.fi/~ctl/3dm/}
 */
public class ThreeDMDiffGenerator extends HtmlDiffGenerator {
  private static final String TMP_IN_FILE1 = "tmp1.txt";
  private static final String TMP_IN_FILE2 = "tmp2.txt";

  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    BaseNode baseDocument = null;
    BranchNode branch = null;
    DiffMatching matching = new DiffMatching();
    File tmpFile1, tmpFile2;
    try {
      tmpFile1 = new File(TMP_IN_FILE1);
      tmpFile2 = new File(TMP_IN_FILE2);
      Files.write(input1.getBytes(), tmpFile1);
      Files.write(input2.getBytes(), tmpFile2);
      XMLParser parser = new XMLParser();
      baseDocument = (BaseNode) parser.parse(TMP_IN_FILE1, matching.getBaseNodeFactory());
      branch = (BranchNode) parser.parse(TMP_IN_FILE2, matching.getBranchNodeFactory());
      tmpFile1.delete();
      tmpFile2.delete();
    } catch (Exception e) {
      System.err.println("XML Parse error " + e.toString());
      e.printStackTrace();
      return null;
    }
    try {
      matching.buildMatching(baseDocument, branch);
      Diff diff = new Diff(matching);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      diff.diff(new XMLPrinter(outputStream));
      return outputStream.toString();
    } catch (Exception e) {
      System.err.println("Exception while obtaining diff " + e.toString());
      e.printStackTrace();
      return null;
    }
  }
}
