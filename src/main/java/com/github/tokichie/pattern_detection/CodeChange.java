package com.github.tokichie.pattern_detection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
  private String changeContent;
  //private List<String> changeSequence;
  //private Map<String, Integer> changeSet;

  @JsonCreator
  public CodeChange(@JsonProperty("changeType") ChangeType changeType,
                    @JsonProperty("changeContent") String changeContent) {
    this.changeType = changeType;
    this.changeContent = changeContent;
  }

  public static ChangeType toChangeType(String type) {
    return ChangeType.valueOf(type);
  }
}
