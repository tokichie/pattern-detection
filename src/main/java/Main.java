import com.github.exkazuu.diff_based_web_tester.diff_generator.xdiff.XDiffGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by tokitake on 2014/11/26.
 */
public class Main {

    public static void main(String args[]) {
        RepositoryCrawler.Crawl();
        List<File> files = RepositoryCrawler.getFilelist();
        HashMap<String, String> fileMap = new HashMap<>();
        List<String[]> xmls = new ArrayList<String[]>();
        System.out.println(files.size());
        int i = 1;
        for (File file : files) {
            //if (i < 11) {
            //    i++;
            //    continue;
            //}
            String filename = file.getName();
            //String regex = "[\\s\\S]*\\\\head_\\d*\\\\([\\s\\S]*)";
            String regex = "[\\s\\S]*" + File.separator + "head_\\d*" + File.separator + "([\\s\\S]*)";
            Pattern p = Pattern.compile(regex);
            Matcher mat = p.matcher(file.getPath());
            String suffix = mat.replaceFirst("$1");

            String originalFilePath = "";
            if (! fileMap.containsKey(suffix)) {
                String currentPath = new File("").getAbsolutePath().replace("pattern-detection", "camel" + File.separator);
                originalFilePath = currentPath + suffix;
            } else {
                originalFilePath = fileMap.get(suffix);
            }
            fileMap.put(suffix, file.getAbsolutePath());

            String originalXmlName = new File("").getAbsolutePath() + File.separator + "xmls" + File.separator + i + "_" + filename + "_org.xml";
            String comparisonXmlName = new File("").getAbsolutePath() + File.separator + "xmls" + File.separator + i + "_" + filename + "_cmp.xml";
            System.out.println(originalXmlName);
            xmls.add(new String[] {originalXmlName, comparisonXmlName, file.getAbsolutePath()});
            String cmd = "mono " + new File("").getAbsolutePath() + File.separator + "code2xml_bin"
                    //+ File.separator + "code2xml" + File.separator + "code2xml" + File.separator + "bin" + File.separator
                    //+ "Release"
                    + File.separator + "code2xml_mono.exe"
                    + " " + originalFilePath
                    + " " + file.getAbsolutePath()
                    + " " + originalXmlName
                    + " " + comparisonXmlName;

            System.out.println(cmd);

            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (i++ > 3) break;
            //i++;
        }

        i = 1;
        XDiffGenerator generator = new XDiffGenerator();

        for (String[] xml : xmls) {
            try {
                String original = FileUtils.readFileToString(new File(xml[0]));
                String comparison = FileUtils.readFileToString(new File(xml[1]));
                String diff = generator.generateDiffContent(original, comparison, System.lineSeparator());

                File saveFile = new File(new File("").getAbsolutePath() + File.separator + "diffs" + File.separator + "diff" + i + ".txt");
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
