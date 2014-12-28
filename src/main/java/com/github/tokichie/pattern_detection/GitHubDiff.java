package com.github.tokichie.pattern_detection;

import com.github.tokichie.pattern_detection.xmldiff.xdiff.XDiffGenerator;

import org.apache.bcel.classfile.Code;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tokitake on 2014/12/16.
 */
public class GitHubDiff {
  private RepositoryInfo repoInfo;

  public GitHubDiff(RepositoryInfo repoInfo) {
    this.repoInfo = repoInfo;
  }

  public void takeDiff() {
    List<PullRequest> pullRequests = this.repoInfo.getPullRequests();

    try {
      for (PullRequest pullRequest : pullRequests) {
        int number = pullRequest.getNumber();
        String path = this.repoInfo.getGitDirectory().getAbsolutePath()
                      + "/diffs/" + number + "/older";
        RepositoryCrawler crawler = new RepositoryCrawler(path);
        List<File> olderFiles = crawler.crawl();
        List<CodeChange> codeChanges = new ArrayList<>();

        for (File olderFile : olderFiles) {
          File newerFile = new File(olderFile.getAbsolutePath().replace("older", "newer"));
          if (newerFile.exists()) {
            if (this.executeCode2Xml(olderFile, newerFile)) {
              System.out.println(pullRequest.getNumber() + ": " + olderFile.getName());
              this.takeXmlDiff(codeChanges);
            }
          }
        }


      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean executeCode2Xml(File older, File newer) {
    String cmd = "mono ./code2xml_bin/code2xml.exe "
        + older.getAbsolutePath() + " "
        + newer.getAbsolutePath() + " "
        + "./tmp/older.xml "
        + "./tmp/newer.xml";

    try {
      Process process = Runtime.getRuntime().exec(cmd);
      if (process.waitFor() != 0) return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private void takeXmlDiff(List<CodeChange> codeChanges) {
    try {
      XDiffGenerator generator = new XDiffGenerator();
      String diff = generator.generateDiffContent(
          FileUtils.readFileToString(new File("tmp/older.xml")),
          FileUtils.readFileToString(new File("tmp/newer.xml")),
          System.lineSeparator());
      System.out.println(diff);

      if (diff.length() < 8) return;
      String type = diff.substring(0, 6);
      if (type.equals("UPDATE")) {
        FileUtils.writeStringToFile(new File("tmp/updates.txt"), diff, true);
      } else {
        String content = diff.substring(7);
        CodeChange codeChange = new CodeChange(CodeChange.toChangeType(type), content);
        codeChanges.add(codeChange);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
