package com.xdxiaodao.code.sync.component;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.xdxiaodao.code.sync.utils.SyncUtil;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class AutoSyncComponent implements ProjectComponent {

  private Logger log = SyncUtil.getLogger(AutoSyncComponent.class);

  private ExecutorService syncExecutor;
  private ScheduledExecutorService monitorExecutor;
  private AtomicBoolean start = new AtomicBoolean(true);
  private Project project;
  private SyncTaskContainer syncTaskContainer;

  public AutoSyncComponent(Project project) {
    this.project = project;
  }

  /**
   * Invoked when the project corresponding to this component instance is opened.<p> Note that
   * components may be created for even unopened projects and this method can be never invoked for a
   * particular component instance (for example for default project).
   */
  @Override
  public void projectOpened() {
    this.syncExecutor = Executors.newSingleThreadExecutor(new BasicThreadFactory.Builder()
        .namingPattern("auto-sync")
        .daemon(false).build());
    this.monitorExecutor = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
        .namingPattern("auto-sync-monitor").build());

    SyncSetting syncSetting = ServiceManager.getService(project, SyncSetting.class);
    if (null == syncSetting) {
      return;
    }

    syncTaskContainer = new SyncTaskContainer(project, syncSetting);
    this.monitorExecutor.scheduleAtFixedRate(new MonitorTask(project, syncSetting, syncTaskContainer),
                                             30, 5, TimeUnit.SECONDS);

    syncExecutor.submit(syncTaskContainer);


    System.out.println("start timer");
  }

  /**
   * Invoked when the project corresponding to this component instance is closed.<p> Note that
   * components may be created for even unopened projects and this method can be never invoked for a
   * particular component instance (for example for default project).
   */
  @Override
  public void projectClosed() {
    try {
      syncTaskContainer.stop();
      syncExecutor.shutdown();
      monitorExecutor.shutdown();
    } catch (Exception e) {
      log.error("[auto-sync] close task or executor has some exception!", e);
    }
  }
}
