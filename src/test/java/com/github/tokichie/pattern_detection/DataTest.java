package com.github.tokichie.pattern_detection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tokitake on 2015/01/01.
 */
public class DataTest {
  //@Test
  // For data analysis, TO BE IMPLEMENTED AS A CLASS
  public void test() {
    try {
      CSVParser parser = CSVParser.parse(new File("tmp/scores_tri_trimmed.txt"), StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> records = parser.getRecords();

      ObjectMapper mapper = new ObjectMapper();
      List<String> diffs = mapper.readValue(new File("tmp/alldiffs_trimmed.json"),
                                            new TypeReference<List<String>>(){});

      Map<String, Integer> histogram = new HashMap<>();
      for (CSVRecord record : records) {
        int ref = Integer.parseInt(record.get(0));
        int cmp = Integer.parseInt(record.get(1));
        String refStr = diffs.get(ref);
        String cmpStr = diffs.get(cmp);
        if (histogram.containsKey(refStr))
          histogram.put(refStr, histogram.get(refStr) + 1);
        else
          histogram.put(refStr, 1);

        if (histogram.containsKey(cmpStr))
          histogram.put(cmpStr, histogram.get(cmpStr) + 1);
        else
          histogram.put(cmpStr, 1);
      }


      List<Map.Entry<String, Integer>> histogramList =
          new ArrayList<>(histogram.entrySet());
      Collections.sort(histogramList, new Comparator<Map.Entry<String, Integer>>(){
        @Override
        public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
          return right.getValue().compareTo(left.getValue());
        }
      });

      System.out.println();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
