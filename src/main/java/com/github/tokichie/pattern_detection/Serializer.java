package com.github.tokichie.pattern_detection;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by tokitake on 2015/01/02.
 */
public class Serializer {

  public static boolean serialize(File outputFile, Object obj) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.writeValue(new FileOutputStream(outputFile), obj);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
