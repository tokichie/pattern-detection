package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;

public class HtmlTreeDiff extends HtmlDiffGenerator {

  private File workingDir;

  public HtmlTreeDiff() {
    try {
      workingDir = new File("temp");
      workingDir.mkdir();
      File libDir = new File("temp/htmltreediff");
      if (!libDir.exists()) {
        execCommand(workingDir, "git", "clone", "https://github.com/exKAZUu/htmltreediff.git");
      }
      execCommand(libDir, "python", "setup.py", "install");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String generateDiffContent(String input1, String input2, String lineSeparator) {
    File file1 = new File("temp/input1.html");
    File file2 = new File("temp/input2.html");
    File outFile = new File("temp/output.html");
    String output = null;
    try {
      Files.write(input1.replace("<?xml version=\"1.1\" encoding=\"UTF-16\"?>", ""), file1,
          Charsets.UTF_16);
      Files.write(input2.replace("<?xml version=\"1.1\" encoding=\"UTF-16\"?>", ""), file2,
          Charsets.UTF_16);
      String[] ret =
          execCommand(workingDir, "python", "-m", "htmltreediff.cli", "input1.html", "input2.html");
      if (!Strings.isNullOrEmpty(ret[1])) {
        System.err.println(ret[1]);
      }
      try (FileInputStream stream = new FileInputStream(outFile)) {
        output = readAllText(stream, Charsets.UTF_16);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    } finally {
      file1.delete();
      file2.delete();
      outFile.delete();
    }
    return output;
  }

  public static String readAllText(InputStream stream) throws IOException {
    StringBuffer buffer = new StringBuffer();
    String separator = System.lineSeparator();
    try (InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(streamReader);) {
      String line;
      while ((line = br.readLine()) != null) {
        buffer.append(line + separator);
      }
      return buffer.toString();
    }
  }

  public static String readAllText(InputStream stream, Charset charset) throws IOException {
    StringBuffer buffer = new StringBuffer();
    String separator = System.lineSeparator();
    try (InputStreamReader streamReader = new InputStreamReader(stream, charset);
        BufferedReader br = new BufferedReader(streamReader);) {
      String line;
      while ((line = br.readLine()) != null) {
        buffer.append(line + separator);
      }
      return buffer.toString();
    }
  }

  public static String[] execCommand(String... cmds) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(cmds);
    try (InputStream in = p.getInputStream(); InputStream err = p.getErrorStream()) {
      return new String[] {readAllText(in), readAllText(err)};
    }
  }

  public static String[] execCommand(File dir, String... cmds) throws IOException,
      InterruptedException {
    Process p = Runtime.getRuntime().exec(cmds, null, dir);
    try (InputStream in = p.getInputStream(); InputStream err = p.getErrorStream()) {
      return new String[] {readAllText(in), readAllText(err)};
    }
  }
}
