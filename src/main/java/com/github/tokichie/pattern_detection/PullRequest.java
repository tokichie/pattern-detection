package com.github.tokichie.pattern_detection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/15.
 */
public class PullRequest {
  private List<String> commits;

  public PullRequest() {
    this.commits = new ArrayList<>();
  }

  public PullRequest(List<String> commits) {
    this.commits = commits;
  }

  public void addCommit(String commitId) { this.commits.add(commitId); }

  public List<String> getCommits() { return this.commits; }

}
