package com.github.tokichie.pattern_detection.comparator;

/**
 * Created by tokitake on 2014/12/31.
 */
public interface Comparator {
  public float calculateSimilarity(String ref, String cmp);
}
