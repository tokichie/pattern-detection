package com.github.tokichie.pattern_detection;


import com.google.common.io.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawler {

  public static void getIssueComments(File repositoryListCsv, int limit) {
    try {
      String authInfoJson = Resources.toString(Resources.getResource("AuthInfo.json"), StandardCharsets.UTF_8);
      GitHubAuthInfo gitHubAuthInfo = new ObjectMapper().readValue(authInfoJson, GitHubAuthInfo.class);

      GitHub github = GitHub.connect(gitHubAuthInfo.login, gitHubAuthInfo.token);
      GHRepository repo = github.getRepository("tokichie/pattern-detection");
      List<GHIssue> closedIssues = repo.getIssues(GHIssueState.CLOSED);
      List<GHPullRequest> closedPullReqs = repo.getPullRequests(GHIssueState.CLOSED);

      System.out.println(closedIssues.size() + " issues");
      for (GHIssue issue : closedIssues) {
        if (issue.getCommentsCount() > 0) {
          List<GHIssueComment> comments = issue.getComments();
          for (GHIssueComment comment : comments) {
            System.out.println(comment.getBody());
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }
}
