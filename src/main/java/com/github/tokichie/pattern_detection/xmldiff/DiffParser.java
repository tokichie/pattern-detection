package com.github.tokichie.pattern_detection.xmldiff;

import com.github.tokichie.pattern_detection.PullRequest;
import com.github.tokichie.pattern_detection.RepositoryCrawler;
import com.github.tokichie.pattern_detection.RepositoryInfo;
import com.github.tokichie.pattern_detection.Serializer;

import org.apache.commons.lang3.StringUtils;

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
  private final int thresholdLength = 1000;
  private List<RepositoryInfo> repoInfoList;

  public DiffParser(List<RepositoryInfo> repoInfoList) {
    this.repoInfoList = repoInfoList;
  }

  public List<String> getDiffs() {
    List<String> diffs = new ArrayList<>();
    for (RepositoryInfo repoInfo : repoInfoList) {
      String repoIdentifier = repoInfo.getRepoIdentifier();
      List<PullRequest> pullRequests = repoInfo.getPullRequests();

      for (PullRequest pullRequest : pullRequests) {
        int number = pullRequest.getNumber();
        String path = repoInfo.getGitDirectory().getAbsolutePath() + "/diffs/" + number;
        if (! new File(path).exists()) {
          System.out.println("\t pull #" + number + " skipped");
          continue;
        }
        RepositoryCrawler crawler = new RepositoryCrawler(path);
        List<File> diffFiles = crawler.crawl(".diff");

        System.out.println("Taking diff of " + repoInfo.getRepoIdentifier() + " pull #" + number);
        for (File diffFile : diffFiles) {
          diffs.addAll(this.parse(diffFile));
        }
      }
      Serializer.serialize(new File("data/" + repoIdentifier + "_diffs.json"), diffs);
    }
    return diffs;
  }

  private List<String> parse(File file) {
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
            String diff = line.substring(1);
            diff = StringUtils.strip(diff);
            if (diff.equals("") || diff.startsWith("//") ||
                diff.startsWith("*") || diff.startsWith("/") || diff.endsWith("*/") ||
                diff.startsWith("import"))
              continue;
            builder.append(diff);
          } else if (line.startsWith("-")) {
            /* TO BE IMPLEMENTED */
          } else {
            if (builder.length() > 0) {
              String res = builder.toString();
              if (res.length() > thresholdLength) continue;
              if (res.equals("}")) continue;
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
