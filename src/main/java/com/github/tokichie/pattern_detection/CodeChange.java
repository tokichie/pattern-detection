package com.github.tokichie.pattern_detection;

import java.util.List;
import java.util.Map;

/**
 * Created by tokitake on 2014/12/23.
 */
public class CodeChange {
  public enum ChangeType {
    INSERT,
    DELETE,
    UPDATE
  }

  private ChangeType changeType;
  private List<String> changeSequence;
  private Map<String, Integer> changeSet;

  public CodeChange(ChangeType changeType, List<String> changeSequence) {
    this.changeType = changeType;
    this.changeSequence = changeSequence;
  }
}
