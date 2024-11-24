package com.hfsolution.app.util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));  // Customize the date format


  public static <T> T parseJson(String jsonString, Class<T> clazz) {
      try {
          objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
          return objectMapper.readValue(jsonString, clazz);
      } catch (Exception e) {
          return null;
      }
  }

  public static String convertToJson(Object obj) {
    try {

      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      return objectMapper.writeValueAsString(obj);

    } catch (IOException e) {
        return null;
    }
  }
}