package com.github.tokichie.pattern_detection;


import com.google.common.io.Resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Pull;
import com.jcabi.github.Pulls;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.wire.CarefulWire;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.json.JsonArray;

/**
 * Created by tokitake on 2014/12/05.
 */
public class GitHubCrawler {

  private String token;
  private Github github;
  private List<CSVRecord> csvRecords;

  public GitHubCrawler(File repositoryListCsv) {
    this(repositoryListCsv, null);
  }

  public GitHubCrawler(File repositoryListCsv, String token) {
    try {
      CSVParser parser = CSVParser.parse(repositoryListCsv, StandardCharsets.UTF_8,
                                         CSVFormat.DEFAULT.withHeader("url"));
      this.csvRecords = parser.getRecords();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (token == null) {
      this.token = getToken();
    } else {
      this.token = token;
    }

    this.github = new RtGithub(
        // this statement controls api limit automatically.
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
    List<RepositoryInfo> repoInfoList = this.getRepositoryInfoList(limit);
    this.getRepositoryPullRequests(repoInfoList);
    this.extractCommittedFiles(repoInfoList);
  }

  private List<RepositoryInfo> getRepositoryInfoList(int limit) {
    List<RepositoryInfo> repoInfoList = new ArrayList<>();

    try {
      ListIterator<CSVRecord> iterator = csvRecords.listIterator();
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
      for (int i = 0; i < repoInfoList.size(); i++) {
        RepositoryInfo repoInfo = repoInfoList.get(i);
        String repoIdentifier = repoInfo.getRepoIdentifier();
        System.out.println("Fetching pulls of " + repoIdentifier);

        File cachedJsonFile = new File("data/" + repoIdentifier + ".json");
        if (cachedJsonFile.exists()) {
          System.out.println("\tCache of pulls exists. loading...");
          repoInfoList.set(i, this.deserializeRepositoryInfo(cachedJsonFile));
          continue;
        }
        Repo repo = this.github.repos().get(new Coordinates.Simple(repoIdentifier));

        repoInfo.setPullRequests(this.getPullRequests(repo.pulls()));
        this.serializeRepositoryInfo(repoInfo);
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
        System.out.print("\tFetching pull #" + pull.number() + "... ");

        List<String> commitShaList = new ArrayList<>();
        Iterator<Commit> commits = pull.commits().iterator();
        if (! commits.hasNext()) {
          System.out.println("skipped.");
          continue;
        }

        Commit firstCommit = commits.next();
        JsonArray parents = firstCommit.json().getJsonArray("parents");
        if (parents.isEmpty()) {
          System.out.println("skipped.");
          continue;
        }

        String parentSha = parents.getJsonObject(0).getString("sha");
        commitShaList.add(parentSha);
        commitShaList.add(firstCommit.sha());

        while (commits.hasNext()) {
          String sha = commits.next().sha();
          commitShaList.add(sha);
        }

        /**
         * when using body of comments for mining, I will uncomment this.
         *
        this.getPullRequestComments();
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
      System.out.println("Extracting files of " + repoIdentifier + "...");

      for (PullRequest pullRequest : pullRequests) {
        List<String> commits = pullRequest.getCommits();
        int commitCount = commits.size();

        for (int i = 0; i < commitCount - 1; i++) {
          this.getDiffArchive(
              repoIdentifier,
              commits.get(i),
              commits.get(i + 1),
              pullRequest.getNumber());
        }
      }
    }
  }

  private void getDiffArchive(
      String repoIdentifier, String olderCommitId, String newerCommitId, int commitNumber) {
    String command =
        new File("git_archive.sh").getAbsolutePath() + " "
        + repoIdentifier + " "
        + olderCommitId.substring(0, 6) + " "
        + newerCommitId.substring(0, 6) + " "
        + commitNumber;

    try {
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      if (process.exitValue() != 0) throw new Exception();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * when using body of comments for mining, I will uncomment this.
   *
  private void getPullRequestComments() {
    PullComments comments = pull.comments();

    for (PullComment comment : comments.iterate(new HashMap<String, String>())) {
      PullComment.Smart smartComment = new PullComment.Smart(comment);
      String originalCommitId = smartComment.json().getString("original_commit_id");
      if (!commitShaList.contains(originalCommitId)) {
        commitShaList.add(originalCommitId);
      }
    }

  }
  */

  private void serializeRepositoryInfo(RepositoryInfo repoInfo) throws IOException{
    ObjectMapper mapper = new ObjectMapper();
    String repoIdentifier = repoInfo.getRepoIdentifier();
    File dir = new File("data/" + repoIdentifier.substring(0, repoIdentifier.indexOf('/')));
    if (!dir.exists()) dir.mkdir();

    File outputFile = new File("data/" + repoIdentifier + ".json");
    mapper.writeValue(new FileOutputStream(outputFile), repoInfo);
  }

  private RepositoryInfo deserializeRepositoryInfo(File inputFile) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    RepositoryInfo repoInfo = mapper.readValue(inputFile, RepositoryInfo.class);
    return repoInfo;
  }
}
