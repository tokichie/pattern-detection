package com.github.tokichie.pattern_detection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/14.
 */
public class RepositoryInfo {
  private String repoIdentifier;
  private File gitDirectory;
  private List<PullRequest> pullRequests;

  public RepositoryInfo(String repoIdentifier, File gitDirectory) {
    this.repoIdentifier = repoIdentifier;
    this.gitDirectory = gitDirectory;
    this.pullRequests = new ArrayList<>();
  }

  public void addPullRequest(PullRequest pullRequest) {
    this.pullRequests.add(pullRequest);
  }

  public void setPullRequests(List<PullRequest> pullRequests) {
    this.pullRequests = pullRequests;
  }

  public String getRepoIdentifier() { return this.repoIdentifier; }

  public File getGitDirectory() { return this.gitDirectory; }

  public List<PullRequest> getPullRequests() { return this.pullRequests; }
}
