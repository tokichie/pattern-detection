package com.github.tokichie.pattern_detection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/10/02.
 */
public class RepositoryCrawler {
  private String dirPath;

  public RepositoryCrawler(String dirPath) {
    this.dirPath = dirPath;
  }

  public List<File> crawl() {
    List<File> files = new ArrayList<>();
    File dir = new File(this.dirPath);
    readDirectory(dir, files);

    return files;
  }

  private void readDirectory(File dir, List<File> files) {
    File[] dirFiles = dir.listFiles();
    for (File file : dirFiles) {
      if (!file.exists()) {
        continue;
      } else if (file.isDirectory()) {
        readDirectory(file, files);
      } else if (file.isFile() && (file.getName().endsWith(".java"))) {
        files.add(file);
      }
    }
  }
}
