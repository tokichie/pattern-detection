package com.github.tokichie.pattern_detection;

import com.google.common.io.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tokichie.pattern_detection.xmldiff.DiffParser;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawlerTest {
  @Test
  public void test() {
    GitHubCrawler
        githubCrawler = new GitHubCrawler(new File(Resources.getResource("java.csv").getFile()));
    List<RepositoryInfo> repoInfoList = githubCrawler.crawl(10);

    DiffParser parser = new DiffParser();
    for (RepositoryInfo repoInfo : repoInfoList) {
      List<String> diffs = new ArrayList<>();
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
          diffs.addAll(parser.parse(diffFile));
        }
      }
      serialize(diffs, repoInfo.getRepoIdentifier());
    }
    System.out.println();
  }

  private void serialize(List<String> diffs, String repoIdentifier) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      File outputFile = new File("data/" + repoIdentifier + "_diffs.json");
      mapper.writeValue(new FileOutputStream(outputFile), diffs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
