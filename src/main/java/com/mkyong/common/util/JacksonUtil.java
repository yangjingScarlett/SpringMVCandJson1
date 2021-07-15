package com.mkyong.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

public class JacksonUtil {

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  public static String parseObj2Json(Object obj) {
    try {
      return (obj == null) ? null : JSON_MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static Map<String, Object> parseJson2Obj(String str) {
    try {
      return JSON_MAPPER.readValue(str, new TypeReference<Map<String, Object>>() {
      });
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}
