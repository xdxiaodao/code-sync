package com.xdxiaodao.code.sync.bean;

import java.util.List;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc 解析传输类
 */
public class StatusCodeDTO {

  private String organization;
  private String sysName;
  private String userName;
  private List<SimpleStatusCode> simpleStatusCodeList;

  public static class SimpleStatusCode {
    private String name;
    private long code;
    private String description;
    private String suggestion;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public long getCode() {
      return code;
    }

    public void setCode(long code) {
      this.code = code;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getSuggestion() {
      return suggestion;
    }

    public void setSuggestion(String suggestion) {
      this.suggestion = suggestion;
    }
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getSysName() {
    return sysName;
  }

  public void setSysName(String sysName) {
    this.sysName = sysName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public List<SimpleStatusCode> getSimpleStatusCodeList() {
    return simpleStatusCodeList;
  }

  public void setSimpleStatusCodeList(List<SimpleStatusCode> simpleStatusCodeList) {
    this.simpleStatusCodeList = simpleStatusCodeList;
  }
}
