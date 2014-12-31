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

  public List<File> crawl(String suffix) {
    List<File> files = new ArrayList<>();
    File dir = new File(this.dirPath);
    readDirectory(dir, files, suffix);

    return files;
  }

  private void readDirectory(File dir, List<File> files, String suffix) {
    File[] dirFiles = dir.listFiles();
    for (File file : dirFiles) {
      if (!file.exists()) {
        continue;
      } else if (file.isDirectory()) {
        readDirectory(file, files, suffix);
      } else if (file.isFile() && (file.getName().endsWith(suffix))) {
        files.add(file);
      }
    }
  }
}
