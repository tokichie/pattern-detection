package com.github.tokichie.pattern_detection;

import com.google.common.io.Resources;

import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawlerTest {
  @Test
  public void test() {
    GitHubCrawler
        githubCrawler = new GitHubCrawler(new File(Resources.getResource("java2.csv").getFile()));
    List<RepositoryInfo> repoInfoList = githubCrawler.crawl();

    for (RepositoryInfo repoInfo : repoInfoList) {
      GitHubDiff githubDiff = new GitHubDiff(repoInfo);
      githubDiff.takeDiff();
      System.out.println();
    }
  }
}
