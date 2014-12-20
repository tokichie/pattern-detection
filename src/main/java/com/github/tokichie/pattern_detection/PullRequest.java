package com.github.tokichie.pattern_detection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/15.
 */
public class PullRequest {
  private List<String> commits;
  private int number;

  @JsonCreator
  public PullRequest() {
    this.commits = new ArrayList<>();
  }

  @JsonCreator
  public PullRequest(@JsonProperty("commits") List<String> commits,
                     @JsonProperty("number") int number) {
    this.commits = commits;
    this.number = number;
  }

  public void addCommit(String commitId) { this.commits.add(commitId); }

  public List<String> getCommits() { return this.commits; }

  public int getNumber() { return this.number; }
}
