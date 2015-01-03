package com.github.tokichie.pattern_detection.comparator;

import org.junit.Test;

/**
 * Created by tokitake on 2014/12/31.
 */
public class TrigramComparatorTest {
  @Test
  public void test() {
    TrigramComparator comparator = new TrigramComparator();
    float res = comparator.calculateSimilarity("ALEXANDRE", "ALEKSANDER");
    org.junit.Assert.assertEquals(0.316f, res, 0.001f);
  }

  @Test
  public void testSameString() {
    TrigramComparator comparator = new TrigramComparator();
    float res = comparator.calculateSimilarity("HOGEHUGA", "HOGEHUGA");
    org.junit.Assert.assertEquals(1f, res, 0.001f);
  }

  @Test
  public void testLongString() {
    TrigramComparator comparator = new TrigramComparator();
    String ref = "import org.elasticsearch.index.mapper.internal.AllFieldMapper;import org.elasticsearch.index.mapper.internal.AnalyzerMapper;import org.elasticsearch.index.mapper.internal.BoostFieldMapper;import org.elasticsearch.index.mapper.internal.FieldNamesFieldMapper;import org.elasticsearch.index.mapper.internal.IdFieldMapper;import org.elasticsearch.index.mapper.internal.IndexFieldMapper;import org.elasticsearch.index.mapper.internal.ParentFieldMapper;import org.elasticsearch.index.mapper.internal.RoutingFieldMapper;import org.elasticsearch.index.mapper.internal.SizeFieldMapper;import org.elasticsearch.index.mapper.internal.SourceFieldMapper;import org.elasticsearch.index.mapper.internal.TTLFieldMapper;import org.elasticsearch.index.mapper.internal.TimestampFieldMapper;import org.elasticsearch.index.mapper.internal.TypeFieldMapper;import org.elasticsearch.index.mapper.internal.UidFieldMapper;import org.elasticsearch.index.mapper.internal.VersionFieldMapper;";
    String cmp = "import org.elasticsearch.index.mapper.internal.TypeFieldMapper;";
    float res = comparator.calculateSimilarity(ref, cmp);
    org.junit.Assert.assertEquals(0.123f, res, 0.001f);
  }
}
