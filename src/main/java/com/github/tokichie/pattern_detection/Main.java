package com.github.tokichie.pattern_detection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.github.tokichie.pattern_detection.comparator.LcsComparator;
import com.github.tokichie.pattern_detection.xmldiff.xdiff.XDiffGenerator;

/**
 * Created by tokitake on 2014/11/26.
 */
public class Main {

  public static void main(String args[]) {
    int i = 1;
    List<String[]> xmls = new ArrayList<>();

    if (false && new File(new File("").getAbsolutePath().replace(
        "pattern-detection", "camel")).exists()) {
      //RepositoryCrawler.Crawl("");
      List<File> files = new ArrayList<>();//RepositoryCrawler.getFilelist();
      HashMap<String, String> fileMap = new HashMap<>();
      System.out.println(files.size());

      for (File file : files) {
        String filename = file.getName();
        String regex = "[\\s\\S]*" + File.separator + "head_\\d*"
                       + File.separator + "([\\s\\S]*)";
        Pattern p = Pattern.compile(regex);
        Matcher mat = p.matcher(file.getPath());
        String suffix = mat.replaceFirst("$1");

        String originalFilePath = "";
        if (!fileMap.containsKey(suffix)) {
          String currentPath = new File("").getAbsolutePath()
              .replace("pattern-detection",
                       "camel" + File.separator);
          originalFilePath = currentPath + suffix;
        } else {
          originalFilePath = fileMap.get(suffix);
        }
        fileMap.put(suffix, file.getAbsolutePath());

        String originalXmlName = new File("").getAbsolutePath()
                                 + File.separator + "xmls" + File.separator + i + "_"
                                 + filename + "_org.xml";
        String comparisonXmlName = new File("").getAbsolutePath()
                                   + File.separator + "xmls" + File.separator + i + "_"
                                   + filename + "_cmp.xml";
        System.out.println(originalXmlName);
        xmls.add(new String[]{originalXmlName, comparisonXmlName,
                              file.getAbsolutePath()});
        String cmd = "mono " + new File("").getAbsolutePath()
                     + File.separator + "code2xml_bin" + File.separator
                     + "code2xml_mono.exe" + " " + originalFilePath + " "
                     + file.getAbsolutePath() + " " + originalXmlName + " "
                     + comparisonXmlName;

        System.out.println(cmd);

        try {
          Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (i++ > 11) {
          break;
        }
      }
    } else {
      //RepositoryCrawler.Crawl(new File("").getAbsolutePath()
                              //+ File.separator + "template-xmls");
      Object[] files = null;//RepositoryCrawler.getFilelist().toArray();
      Arrays.sort(files, new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
          return ((File) o1).getName().compareTo(
              ((File) o2).getName());
        }
      });

      for (int c = 0; c < files.length; c += 2) {
        xmls.add(new String[]{
            ((File) files[c + 1]).getAbsolutePath(),
            ((File) files[c]).getAbsolutePath(),
            ((File) files[c + 1]).getName()});
      }
    }

    i = 1;
    XDiffGenerator generator = new XDiffGenerator();

    for (String[] xml : xmls) {
      try {
        String original = FileUtils.readFileToString(new File(xml[0]));
        String comparison = FileUtils
            .readFileToString(new File(xml[1]));
        String diff = generator.generateDiffContent(original,
                                                    comparison, System.lineSeparator());

        String[] lines = diff.split(System.lineSeparator());
        String ref = lines[0];
        List<Double> scoreList = new ArrayList<>();
        LcsComparator comparator = new LcsComparator();
        for (int j = 1; j < lines.length; j++) {
          String line = lines[j];
          double score = comparator.calculateSimilarity(ref, line);
          scoreList.add(score);
        }
        System.out.println(scoreList.toString());


        File saveFile = new File(new File("").getAbsolutePath()
                                 + File.separator + "diffs" + File.separator + "diff"
                                 + i + ".txt");
        FileUtils.writeStringToFile(saveFile, xml[2] + "\n" + diff);
        System.out.println(i + "done.");
        i++;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("done.");
  }
}
