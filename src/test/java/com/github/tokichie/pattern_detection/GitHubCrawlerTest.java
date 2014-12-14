package com.github.tokichie.pattern_detection;

import com.google.common.io.Resources;

import org.junit.Test;

import java.io.File;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawlerTest {
  @Test
  public void test() {
    //GitHubCrawler.getIssueComments(null, 0);
    GitHubCrawler crawler = new GitHubCrawler(new File(Resources.getResource("java.csv").getFile()));
    crawler.crawl(10);
  }
}
