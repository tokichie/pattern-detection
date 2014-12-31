package com.github.tokichie.pattern_detection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tokichie.pattern_detection.comparator.LcsComparator;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/31.
 */
public class GitHubDiffTest {
  @Test
  public void test() {
    RepositoryCrawler crawler = new RepositoryCrawler("data");
    List<File> diffFiles = crawler.crawl("_diffs.json");

    List<String> allDiffs = new ArrayList<>();
    addDiffsOfFile(diffFiles, allDiffs);
    compare(allDiffs);
  }

  private void addDiffsOfFile(List<File> diffFiles, List<String> allDiffs) {
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

  private void compare(List<String> allDiffs) {
    int size = allDiffs.size();
    //List<Score> scores = new ArrayList<>();
    LcsComparator comparator = new LcsComparator();
    int scoreCount = 0;
    try {
      PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File("tmp/scores.txt"))));
      for (int i = 0; i < size; i++) {
        System.out.println("Comparing #" + i + "...");
        String ref = allDiffs.get(i);
        for (int j = 0; j < size; j++) {
          if (i == j) continue;
          float score = comparator.calculateSimilarity(ref, allDiffs.get(j));
          if (score >= 0.75f) {
            //scores.add(new Score(i, j, score));
            writer.print(i + "," + j + ",");
            writer.printf("%.3f\n", score);
            scoreCount++;
          }
        }
        System.out.println("\tSize of scores is " + scoreCount);
      }
      writer.close();
      System.out.println("done.");
      //ObjectMapper mapper = new ObjectMapper();
      //mapper.writeValue(new FileOutputStream(new File("tmp/scores.json")), scores);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private class Score {
    private int ref;
    private int index;
    private float score;
    @JsonCreator
    public Score(@JsonProperty("ref") int ref,
                 @JsonProperty("index") int index,
                 @JsonProperty("score") float score) {
      this.ref = ref;
      this.index = index;
      this.score = score;
    }
  }
}
