package com.github.tokichie.pattern_detection.comparator;

import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3Extended;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tokitake on 2014/12/31.
 */
public class TrigramComparator implements Comparator {
  public float calculateSimilarity(String ref, String cmp) {
    int refLength = ref.length();
    int cmpLength = cmp.length();
    if (refLength < 3 || cmpLength < 3) return 0f;

    TokeniserQGram3Extended tokenizer = new TokeniserQGram3Extended();
    List<String> smallerList, largerList;
    if (refLength < cmpLength) {
      smallerList = tokenizer.tokenizeToArrayList(ref);
      largerList = tokenizer.tokenizeToArrayList(cmp);
    } else {
      smallerList = tokenizer.tokenizeToArrayList(cmp);
      largerList = tokenizer.tokenizeToArrayList(ref);
    }
    smallerList = smallerList.subList(1, smallerList.size() - 1);
    largerList = largerList.subList(1, largerList.size() - 1);

    Set<Integer> hashCodes = new HashSet<>();
    for (String str : largerList) {
      hashCodes.add(str.hashCode());
    }

    int matchCount = 0;
    for (String str : smallerList) {
      if (hashCodes.contains(str.hashCode())) matchCount++;
    }

    return (2f * matchCount) / (smallerList.size() + largerList.size());
  }

}
