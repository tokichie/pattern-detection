package com.github.tokichie.pattern_detection.comparator;

/**
 * Created by tokitake on 2014/11/29.
 */
public class LcsComparator implements Comparator{

  public float calculateSimilarity(String ref, String cmp) {
    int n = ref.length();
    int m = cmp.length();
    int[][] dp = new int[n + 1][m + 1];

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if (ref.charAt(i) == cmp.charAt(j)) {
          dp[i + 1][j + 1] = dp[i][j] + 1;
        } else {
          dp[i + 1][j + 1] = Math.max(dp[i][j + 1], dp[i + 1][j]);
        }
      }
    }

    float res = (float) dp[n][m] / n;

    return res;
  }
}
