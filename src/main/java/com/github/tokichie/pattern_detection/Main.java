package com.github.tokichie.pattern_detection;

import com.google.common.io.Resources;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tokichie.pattern_detection.comparator.Comparator;
import com.github.tokichie.pattern_detection.comparator.LcsComparator;
import com.github.tokichie.pattern_detection.comparator.TrigramComparator;
import com.github.tokichie.pattern_detection.xmldiff.DiffParser;
import com.github.tokichie.pattern_detection.xmldiff.xdiff.XDiffGenerator;

/**
 * Created by tokitake on 2014/11/26.
 */
public class Main {

  public static void main(String args[]) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      System.out.println("Please specify operation.");
      System.out.println("  [1] Crawling GitHub to gather pull requests data.");
      System.out.println("  [2] Mining diff data using tri-gram algorithm.");
      System.out.print("Input operation number: ");
      String str = reader.readLine();

      if (str.equals("1"))
        crawlGitHub();
      else if (str.equals("2"))
        miningDiffData();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void crawlGitHub() {
    GitHubCrawler
        githubCrawler = new GitHubCrawler(new File(Resources.getResource("java.csv").getFile()));
    List<RepositoryInfo> repoInfoList = githubCrawler.crawl(10);

    DiffParser parser = new DiffParser(repoInfoList);
    parser.getDiffs();
  }

  private static void miningDiffData() {
    RepositoryCrawler crawler = new RepositoryCrawler("data");
    List<File> diffFiles = crawler.crawl("_diffs.json");

    List<String> allDiffs = new ArrayList<>();
    addDiffsOfFile(diffFiles, allDiffs);
    compareWithTrigram(allDiffs);
  }

  private static void addDiffsOfFile(List<File> diffFiles, List<String> allDiffs) {
    try {
      for (File file : diffFiles) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> diffs = mapper.readValue(file, new TypeReference<List<String>>(){});
        allDiffs.addAll(diffs);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void compareWithTrigram(List<String> allDiffs) {
    int size = allDiffs.size();
    Comparator comparator = new TrigramComparator();
    Set<Long> exclusion = new HashSet<>();
    int scoreCount = 0;
    try {
      PrintWriter
          writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File("tmp/scores_tri.txt"))));
      for (int i = 0; i < size; i++) {
        System.out.println("Comparing #" + i + "...");
        String ref = allDiffs.get(i);
        for (int j = i + 1; j < size; j++) {
          if (exclusion.contains((long)(i+1)*(j+1))) continue;
          float score = comparator.calculateSimilarity(ref, allDiffs.get(j));

          if (score >= 0.75f) {
            writer.print(i + "," + j + ",");
            writer.printf("%.3f\n", score);
            scoreCount++;
            exclusion.add((long)(i+1)*(j+1));
          }
        }
        System.out.println("\tSize of scores is " + scoreCount);
      }
      writer.close();
      System.out.println("done.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
