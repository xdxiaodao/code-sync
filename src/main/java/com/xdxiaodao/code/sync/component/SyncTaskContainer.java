package com.xdxiaodao.code.sync.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.xdxiaodao.code.sync.bean.SyncType;
import com.xdxiaodao.code.sync.utils.SyncUtil;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class SyncTaskContainer implements Runnable {

  private static Logger logger = SyncUtil.getLogger(SyncTaskContainer.class);
  private SyncSetting syncSetting;
  private Project project;
  private PullSyncTask pullSyncTask;
  private PushSyncTask pushSyncTask;
  private AtomicBoolean running = new AtomicBoolean(false);

  public SyncTaskContainer(Project project, SyncSetting syncSetting) {
    this.project = project;
    this.syncSetting = syncSetting;
    this.pushSyncTask = new PushSyncTask(project, syncSetting.getPushSyncConfig());
    this.pullSyncTask = new PullSyncTask(project, syncSetting.getPullSyncConfig());
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

    this.running.set(true);

    while (true) {

      try {
        if (!running.get()) {
          logger.info("[sync task container] stop task");
          break;
        }

        // 空转
        if (null == syncSetting || SyncType.NONE.equals(syncSetting.getSyncType())) {
          logger.info("[sync task container] no effective task, empty running");
          Thread.sleep(10000);
          continue;
        }

        if (SyncType.PULL.equals(syncSetting.getSyncType())
            && null != syncSetting.getPullSyncConfig()) {
          pullSyncTask.setSyncConfig(syncSetting.getPullSyncConfig());
          pullSyncTask.run();
          Thread.sleep(syncSetting.getPullSyncConfig().getInterval() * 1000);
          continue;
        }

        if (SyncType.PUSH.equals(syncSetting.getSyncType()) && null != syncSetting.getPushSyncConfig()) {
          pushSyncTask.setSyncConfig(syncSetting.getPushSyncConfig());
          pushSyncTask.run();
          Thread.sleep(syncSetting.getPushSyncConfig().getInterval() * 1000);
          continue;
        }

        logger.info("[sync task container] no effective task, empty running");
        Thread.sleep(10000);
      } catch (Exception e) {
        logger.warn("[sync task container] running has some exception!");
      }
    }
  }

  public SyncSetting getSyncSetting() {
    return syncSetting;
  }

  public synchronized void setSyncSetting(SyncSetting syncSetting) {
    this.syncSetting = syncSetting;
  }

  public synchronized void stop() {
    this.running.set(false);
  }
}
