package com.github.tokichie.pattern_detection;


import com.google.common.io.Resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawler {

  public static void getIssueComments(File repositoryListCsv, int limit) {
    try {
      //CSVParser csvParser = CSVParser.parse(repositoryListCsv, StandardCharsets.UTF_8,
      //                                      CSVFormat.DEFAULT);
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
          GHPullRequest pullRequest = repo.getPullRequest(issue.getNumber());
          PagedIterable<GHPullRequestCommitDetail> commitDetails = pullRequest.listCommits();
          List<GHCommit> commits = new ArrayList<>();

          for (GHPullRequestCommitDetail commit : commitDetails) {
            commits.add(repo.getCommit(commit.getSha()));
          }

          System.out.println(commits.get(0).getFiles().get(0).getPatch());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }
}
