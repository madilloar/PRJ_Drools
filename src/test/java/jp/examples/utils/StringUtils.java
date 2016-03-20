package jp.examples.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtils {
  /**
   * 引数のオブジェクトをJSON文字列に変換します。
   * @param o
   * @return JSON文字列。
   * @throws RuntimeException {@link JsonProcessingException}をラップします。
   */
  public static String toJson(Object o) {
    ObjectMapper mapper = new ObjectMapper();
    String json = null;
    try {
      json = mapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return json;
  }
}
