package com.xdxiaodao.code.sync.component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.xdxiaodao.code.sync.bean.SyncConfig;
import com.xdxiaodao.code.sync.bean.SyncType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
@State(name = "com.xdxiaodao.status.code.sync.component.SyncSetting", storages = {@com.intellij.openapi.components.Storage(file = "$PROJECT_CONFIG_DIR$/code-sync.xml")})
public class SyncSetting implements PersistentStateComponent<SyncSetting> {

  private SyncConfig pullSyncConfig;
  private SyncConfig pushSyncConfig;
  private SyncType syncType = SyncType.PULL;

  /**
   * @return a component state. All properties, public and annotated fields are serialized. Only
   * values, which differ from default (i.e. the value of newly instantiated class) are serialized.
   * {@code null} value indicates that the returned state won't be stored, as a result previously
   * stored state will be used.
   * @see XmlSerializer
   */
  @Nullable
  @Override
  public SyncSetting getState() {
    if (null == pullSyncConfig) {
      pullSyncConfig = new SyncConfig();
    }
    if (null == pushSyncConfig) {
      pushSyncConfig = new SyncConfig();
    }
    if (SyncType.NONE.equals(syncType)) {
      syncType = SyncType.PULL;
    }

    return this;
  }

  /**
   * This method is called when new component state is loaded. The method can and will be called
   * several times, if config files were externally changed while IDEA running.
   *
   * @param state loaded component state
   * @see XmlSerializerUtil#copyBean(Object, Object)
   */
  @Override
  public void loadState(@NotNull SyncSetting state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public SyncConfig getPullSyncConfig() {
    return pullSyncConfig;
  }

  public void setPullSyncConfig(SyncConfig pullSyncConfig) {
    this.pullSyncConfig = pullSyncConfig;
  }

  public SyncConfig getPushSyncConfig() {
    return pushSyncConfig;
  }

  public void setPushSyncConfig(SyncConfig pushSyncConfig) {
    this.pushSyncConfig = pushSyncConfig;
  }

  public SyncType getSyncType() {
    return syncType;
  }

  public void setSyncType(SyncType syncType) {
    this.syncType = syncType;
  }
}
