package com.xdxiaodao.code.sync.bean;

import java.util.Objects;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class SyncConfig {

  public String url = "";
  public int interval = 60;
  public String path = "";
  public String author = "";
  public String email = "";
  public String organization;
  public String template = "code=code\n"
      + "description=desc\n"
      + "suggestion=";
  public String data = "";

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SyncConfig that = (SyncConfig) o;
    return interval == that.interval &&
        Objects.equals(url, that.url) &&
        Objects.equals(path, that.path) &&
        Objects.equals(author, that.author) &&
        Objects.equals(email, that.email) &&
        Objects.equals(organization, that.organization) &&
        Objects.equals(template, that.template) &&
        Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, interval, path, author, email);
  }

  public synchronized SyncConfig snapshot() {
    SyncConfig syncConfig = new SyncConfig();
    syncConfig.setUrl(this.getUrl());
    syncConfig.setInterval(this.getInterval());
    syncConfig.setPath(this.getPath());
    syncConfig.setAuthor(this.getAuthor());
    syncConfig.setEmail(this.getEmail());
    syncConfig.setOrganization(this.getOrganization());
    syncConfig.setTemplate(this.getTemplate());
    syncConfig.setData(this.getData());
    return syncConfig;
  }
}
