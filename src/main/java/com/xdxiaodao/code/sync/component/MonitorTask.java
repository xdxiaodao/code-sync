package com.xdxiaodao.code.sync.component;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class MonitorTask implements Runnable {

  private Project project;
  private SyncSetting latestSyncSetting;
  private SyncTaskContainer syncTaskContainer;

  public MonitorTask(Project project,
                     SyncSetting syncSetting,
                     SyncTaskContainer syncTaskContainer) {
    this.project = project;
    this.latestSyncSetting = syncSetting;
    this.syncTaskContainer = syncTaskContainer;
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

    // 判断是否有更改
    SyncSetting syncSetting = ServiceManager.getService(project, SyncSetting.class);

    if (null == syncSetting
        || null == latestSyncSetting
        || null == syncSetting.getPushSyncConfig()
        || null == syncSetting.getPullSyncConfig()) {
      return;
    }

    // 比较是否有更改
    boolean isModify = !syncSetting.getSyncType().equals(this.latestSyncSetting.getSyncType())
        || !syncSetting.getPullSyncConfig().equals(this.latestSyncSetting.getPullSyncConfig())
        || !syncSetting.getPushSyncConfig().equals(this.latestSyncSetting.getPushSyncConfig());

    if (isModify) {
      syncTaskContainer.setSyncSetting(syncSetting);
    }
  }
}
