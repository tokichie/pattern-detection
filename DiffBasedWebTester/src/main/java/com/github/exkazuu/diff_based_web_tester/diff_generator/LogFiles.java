package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;

public class LogFiles {
  private static final Charset charset = Charsets.UTF_8;
  private static String prefixFileName = "";

  public static String getPrefixFileName() {
    return prefixFileName;
  }

  public static void setPrefixFileName(String prefixFileName) {
    LogFiles.prefixFileName = prefixFileName;
  }

  public static void writeLogFile(String suffixFileName, String content) {
    try {
      new File("log").mkdir();
      File file = createFile(suffixFileName);
      try (FileOutputStream stream = new FileOutputStream(file);
          OutputStreamWriter writer = new OutputStreamWriter(stream, charset);) {
        writer.write(content);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String readLogFile(String suffixFileName) {
    File file = createFile(suffixFileName);
    if (file.exists()) {
      try {
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        try (FileInputStream stream = new FileInputStream(file);
            InputStreamReader writer = new InputStreamReader(stream, charset);
            BufferedReader br = new BufferedReader(writer);) {
          String line;
          while ((line = br.readLine()) != null) {
            builder.append(line).append(lineSeparator);
          }
        }
        return builder.toString();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static File createFile(String suffixFileName) {
    return new File("log" + File.separatorChar + prefixFileName + "_" + suffixFileName);
  }
}
