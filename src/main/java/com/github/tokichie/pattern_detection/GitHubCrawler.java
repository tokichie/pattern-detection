package com.github.tokichie.pattern_detection;


import com.google.common.io.Resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Limit;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.github.Pulls;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.wire.CarefulWire;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.api.Git;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawler {

  private String token;
  private File repositoryListCsv;
  private Github github;
  private int apiRemaining;
  private Date apiResetDate;

  public GitHubCrawler(File repositoryListCsv) {
    this(repositoryListCsv, null);
  }

  public GitHubCrawler(File repositoryListCsv, String token) {
    this.repositoryListCsv = repositoryListCsv;

    if (token == null) {
      this.token = getToken();
    } else {
      this.token = token;
    }

    this.github = new RtGithub(
        // this statement control api limit automatically.
        new RtGithub(this.token).entry().through(CarefulWire.class, 50)
    );
  }

  private String getToken() {

    try {
      Map<String, String> env = System.getenv();
      if (!(env.containsKey("token"))) {
        String authInfoJson =
            Resources.toString(Resources.getResource("AuthInfo.json"), StandardCharsets.UTF_8);
        env =
            new ObjectMapper()
                .readValue(authInfoJson, new TypeReference<HashMap<String, String>>() {
                });
      }
      return env.get("token");
    } catch (IOException e) {
      e.printStackTrace();
    }

    return "";
  }

  public void crawl() { this.crawl(0); }

  public void crawl(int limit) {
    if (limit != 0) limit++;
    try {
      List<RepositoryInfo> repoInfoList = this.getRepositoryInfoList(limit);
      this.getRepositoryPullRequests(repoInfoList);
      //this.serializeData(new FileOutputStream(new File("data/repoinfo.json")), repoInfoList);
      this.serializeRepositoryInfoList(repoInfoList);
      this.extractCommittedFiles(repoInfoList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<RepositoryInfo> getRepositoryInfoList(int limit) {
    List<RepositoryInfo> repoInfoList = new ArrayList<>();

    try {
      CSVParser parser = CSVParser.parse(this.repositoryListCsv, StandardCharsets.UTF_8,
                                            CSVFormat.DEFAULT.withHeader("url"));
      List<CSVRecord> records = parser.getRecords();
      ListIterator<CSVRecord> iterator = records.listIterator();
      iterator.next();

      while (iterator.hasNext()) {
        if (limit != 0 && iterator.nextIndex() >= limit) break;

        String repoUrl = iterator.next().get("url");
        String repoIdentifier = repoUrl.replace("https://github.com/", "").replace(".git", "");
        File repoFile = new File("repos" + File.separator + repoIdentifier);
        if (repoFile.exists()) {
          System.out.println(repoIdentifier + " exists. going on...");
          repoInfoList.add(new RepositoryInfo(repoIdentifier, repoFile));
          continue;
        }

        if (! this.cloneRepository(repoIdentifier)) break;
        repoInfoList.add(new RepositoryInfo(repoIdentifier, repoFile));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return repoInfoList;
  }

  private boolean cloneRepository(String repoIdentifier) {
    if (new File("/").getFreeSpace() < 10000000000L) {
      System.out.println("Cancelled: Free disk space is lower than 10GB now");
      return false;
    }

    try {
      String command = "git clone https://github.com/" + repoIdentifier + ".git " + repoIdentifier;
      Process process = Runtime.getRuntime().exec(command, null, new File("repos"));
      System.out.println("Cloning " + repoIdentifier + "...");
      process.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private void getRepositoryPullRequests(List<RepositoryInfo> repoInfoList) {
    try {
      for (RepositoryInfo repoInfo : repoInfoList) {
        this.checkApiLimit();
        if (this.apiRemaining < 50) {
          System.out.println("Cancelled: Remain of API access is lower than 50");
          break;
        }

        String repoIdentifier = repoInfo.getRepoIdentifier();
        Repo repo = this.github.repos().get(new Coordinates.Simple(repoIdentifier));

        System.out.println(repoIdentifier);
        repoInfo.setPullRequests(this.getPullRequests(repo.pulls()));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private List<PullRequest> getPullRequests(Pulls pulls) {

    List<PullRequest> pullRequests = new ArrayList<>();

    try {
      Map<String, String> pullParams = new HashMap<>();
      pullParams.put("state", "closed");

      for (Pull pull : pulls.iterate(pullParams)) {
        System.out.print("\tfetching pull #" + pull.number() + "... ");
        checkApiLimit();

        List<String> commitShaList = new ArrayList<>();
        Iterator<Commit> commits = pull.commits().iterator();
        while (commits.hasNext()) {
          String sha = commits.next().sha();
          System.out.print(sha + " ");
          commitShaList.add(sha);
        }

        if (commitShaList.size() < 2) {
          System.out.println("skipped.");
          continue;
        }

        /**
         * when using body of comments for mining, I will uncomment this.
         *
        PullComments comments = pull.comments();

        for (PullComment comment : comments.iterate(new HashMap<String, String>())) {
          PullComment.Smart smartComment = new PullComment.Smart(comment);
          String originalCommitId = smartComment.json().getString("original_commit_id");
          if (!commitShaList.contains(originalCommitId)) {
            commitShaList.add(originalCommitId);
          }
        }
        */

        pullRequests.add(new PullRequest(commitShaList, pull.number()));
        System.out.println("done.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return pullRequests;
  }

  private void extractCommittedFiles(List<RepositoryInfo> repoInfoList) {
    for (RepositoryInfo repoInfo : repoInfoList) {
      String repoIdentifier = repoInfo.getRepoIdentifier();

      List<PullRequest> pullRequests = repoInfo.getPullRequests();
      for (PullRequest pullRequest : pullRequests) {
        List<String> commits = pullRequest.getCommits();
        int commitCount = commits.size();

        for (int i = 0; i < commitCount - 1; i++) {
          this.getDiffArchive(repoIdentifier, commits.get(i), commits.get(i + 1));
        }
      }
    }
  }

  public void getDiffArchive(String repoIdentifier, String olderCommitId, String newerCommitId) {
    String command =
        new File("git_archive.sh").getAbsolutePath() + " "
        + repoIdentifier + " "
        + olderCommitId.substring(0, 6) + " "
        + newerCommitId.substring(0, 6);

    try {
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      if (process.exitValue() != 0) throw new Exception();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void checkApiLimit() {
    try {
      Limit.Smart apiLimit = new Limit.Smart(this.github.limits().get("core"));
      this.apiRemaining = apiLimit.remaining();
      this.apiResetDate = apiLimit.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void serializeRepositoryInfoList(List<RepositoryInfo> repoInfoList) throws IOException{
    ObjectMapper mapper = new ObjectMapper();
    for (RepositoryInfo repoInfo : repoInfoList) {
      String repoIdentifier = repoInfo.getRepoIdentifier();
      File dir = new File("data/" + repoIdentifier.substring(0, repoIdentifier.indexOf('/')));
      if (!dir.exists()) dir.mkdir();

      File outputFile = new File("data/" + repoIdentifier + ".json");
      mapper.writeValue(new FileOutputStream(outputFile), repoInfo);
    }
  }

  private Object deserializeRepositoryInfo(File inputFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    RepositoryInfo repoInfo = mapper.readValue(inputFile, RepositoryInfo.class);
    return repoInfo;
  }
}
