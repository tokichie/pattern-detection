package com.github.tokichie.pattern_detection;


import com.google.common.io.Resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.github.Pulls;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;

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
      if (! (env.containsKey("token"))) {
        String authInfoJson =
            Resources.toString(Resources.getResource("AuthInfo.json"), StandardCharsets.UTF_8);
        env = new ObjectMapper().readValue(authInfoJson, new TypeReference<HashMap<String, String>>(){});
      }

      Github github = new RtGithub(env.get("token"));
      Repo repo = github.repos().get(new Coordinates.Simple("tokichie/pattern-detection"));

      Pulls pulls = repo.pulls();
      Map<String, String> params = new HashMap<>();
      params.put("state", "close");
      for (Pull pull : pulls.iterate(params)) {
        PullComments comments = pull.comments();
        List<String> commitShaList = new ArrayList<>();

        for (PullComment comment : comments.iterate(new HashMap<String, String>())) {
          PullComment.Smart smartComment = new PullComment.Smart(comment);
          commitShaList.add(smartComment.json().getString("original_commit_id"));
          System.out.println(smartComment.body());


        }

      }



    } catch (IOException e) {
      e.printStackTrace();
    }


  }
}
