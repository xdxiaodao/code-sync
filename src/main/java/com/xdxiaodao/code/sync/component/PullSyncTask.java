package com.xdxiaodao.code.sync.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.xdxiaodao.code.sync.bean.SyncConfig;
import com.xdxiaodao.code.sync.utils.SyncUtil;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class PullSyncTask implements Runnable {

  private Logger logger = SyncUtil.getLogger(PullSyncTask.class);

  private Project project;
  private SyncConfig pullSyncConfig;
  private int count = 0;

  public PullSyncTask(Project project, SyncConfig syncConfig) {
    this.project = project;
    this.pullSyncConfig = syncConfig;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    System.out.println("pull sync task:" + count + ", interval:" + pullSyncConfig.getInterval());
  }

  /**
   * 设置同步配置
   *
   * @param syncConfig 同步配置
   */
  public void setSyncConfig(SyncConfig syncConfig) {
    this.pullSyncConfig = syncConfig;
  }
}
