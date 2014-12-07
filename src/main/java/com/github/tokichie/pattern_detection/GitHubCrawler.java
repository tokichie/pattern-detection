package com.github.tokichie.pattern_detection;


import com.google.common.io.Resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawler {

  public static void getIssueComments(File repositoryListCsv, int limit) {
    try {
      Map<String, String> env = System.getenv();
      if (! (env.containsKey("login") && env.containsKey("token"))) {
        String authInfoJson =
            Resources.toString(Resources.getResource("AuthInfo.json"), StandardCharsets.UTF_8);
        env = new ObjectMapper().readValue(authInfoJson, new TypeReference<HashMap<String, String>>(){});
      }

      GitHub github = GitHub.connect(env.get("login"), env.get("token"));
      GHRepository repo = github.getRepository("tokichie/pattern-detection");
      List<GHIssue> closedIssues = repo.getIssues(GHIssueState.CLOSED);

      System.out.println(closedIssues.size() + " issues");
      for (GHIssue issue : closedIssues) {
        if (issue.getCommentsCount() > 0 && issue.getNumber() == 11) {
          System.out.println("issue #" + issue.getNumber());
          List<GHIssueComment> comments = issue.getComments();
          for (GHIssueComment comment : comments) {
            System.out.println("\t" + comment.getBody() + "\n");
          }

        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }
}
