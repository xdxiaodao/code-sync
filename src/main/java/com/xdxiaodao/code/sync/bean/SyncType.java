package com.xdxiaodao.code.sync.bean;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public enum SyncType {
  NONE("none", 1), PULL(), PUSH("push", 3);

  private String name;
  private int type;

  private SyncType(String name, int type) {
    this.name = name;
    this.type = type;
  }

  private SyncType() {
    this.name="";
    this.type = 0;
  }

  SyncType(int type, String cname) {
    this.type = type;
    this.name = cname;
  }
}
