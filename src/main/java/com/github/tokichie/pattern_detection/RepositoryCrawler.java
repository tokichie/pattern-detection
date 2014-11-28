package com.github.tokichie.pattern_detection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/10/02.
 */
public class RepositoryCrawler {

  private static final String REPOPATH =
      (new File("").getAbsolutePath().replace("pattern-detection", "camel")) + File.separator
      + "diff_root";
  private static List<File> filelist;

  public static void Crawl(String repopath) {
    if (repopath.isEmpty()) {
      repopath = REPOPATH;
    }
    File dir = new File(repopath);
    filelist = new ArrayList<File>();
    readDirectory(dir);
  }

  public static List<File> getFilelist() {
    return filelist;
  }

  private static void readDirectory(File dir) {
    List<File> files = java.util.Arrays.asList(dir.listFiles());
    for (File file : files) {
      if (!file.exists()) {
        continue;
      } else if (file.isDirectory()) {
        readDirectory(file);
      } else if (file.isFile() && (file.getName().endsWith(".java") || file.getName()
          .endsWith(".xml"))) {
        filelist.add(file);
      }
    }
  }


}
