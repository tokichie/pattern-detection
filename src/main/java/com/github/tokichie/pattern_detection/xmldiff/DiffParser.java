package com.github.tokichie.pattern_detection.xmldiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/29.
 */
public class DiffParser {
  public DiffParser() {

  }

  public List<String> parse(File file) {
    List<String> changes = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      boolean skipFlag = false;
      boolean parseFlag = false;
      StringBuilder builder = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        if (skipFlag) continue;

        if (line.startsWith("diff --git")) {
          skipFlag = parseFlag = false;
          String[] elems = line.split(" ");
          if (! (elems[2].endsWith(".java") && elems[3].endsWith(".java"))) {
            skipFlag = true;
            continue;
          }
        } else if (line.startsWith("deleted")) {
          skipFlag = true;
          continue;
        } else if (line.startsWith("@")) {
          parseFlag = true;
          continue;
        } else if (parseFlag) {
          if (line.startsWith("+")) {
            if (! line.equals("+\n")) builder.append(line.substring(1));
          } else if (line.startsWith("-")) {
            /* TO BE IMPLEMENTED */
          } else {
            if (builder.length() > 0) {
              changes.add(builder.toString());
              builder.setLength(0);
            }
          }
        }
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return changes;
  }

}
