package com.xdxiaodao.code.sync.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class JsonUtil {

  private static final Gson GSON = new Gson();

  public static String objectToJson(Object obj) {
    return obj == null ? null : GSON.toJson(obj);
  }

  public static <T> T jsonToObject(String json, Class<T> clazz) {
    return !StringUtils.isBlank(json) && clazz != null ? GSON.fromJson(json, clazz) : null;
  }

  public static <T> T jsonToObject(String json, TypeToken typeToken) {
    return !StringUtils.isBlank(json) && typeToken != null ? GSON.fromJson(json, typeToken.getType()) : null;
  }
}
