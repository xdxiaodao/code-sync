package com.xdxiaodao.code.sync.configurable;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.xdxiaodao.code.sync.component.SyncSetting;
import com.xdxiaodao.code.sync.form.SyncForm;
import com.xdxiaodao.code.sync.utils.HttpUtil;
import java.io.IOException;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class SyncConfigurable implements Configurable {

  private SyncSetting syncSetting;
  private SyncForm syncForm;
  private Project project;

  public SyncConfigurable(Project project) {
    this.syncSetting = ServiceManager.getService(project, SyncSetting.class);
  }

  /**
   * Returns the visible name of the configurable component. Note, that this method must return the
   * display name that is equal to the display name declared in XML to avoid unexpected errors.
   *
   * @return the visible name of the configurable component
   */
  @Nls(capitalization = Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "CodeSync";
  }

  /**
   * Creates new Swing form that enables user to configure the settings. Usually this method is
   * called on the EDT, so it should not take a long time.
   *
   * Also this place is designed to allocate resources (subscriptions/listeners etc.)
   *
   * @return new Swing form to show, or {@code null} if it cannot be created
   * @see #disposeUIResources
   */
  @Nullable
  @Override
  public JComponent createComponent() {
    if (null == this.syncForm) {
      this.syncForm = new SyncForm(syncSetting);
    }
    return this.syncForm.getRootPanel();
  }

  /**
   * Indicates whether the Swing form was modified or not. This method is called very often, so it
   * should not take a long time.
   *
   * @return {@code true} if the settings were modified, {@code false} otherwise
   */
  @Override
  public boolean isModified() {
    return this.syncForm.isModified(this.syncSetting);
  }

  /**
   * Stores the settings from the Swing form to the configurable component. This method is called on
   * EDT upon user's request.
   *
   * @throws ConfigurationException if values cannot be applied
   */
  @Override
  public void apply() throws ConfigurationException {
    if (null != this.syncForm) {
      this.syncForm.getData(this.syncSetting);
    }

    String url = "http://10.143.45.148:8080/tool/discover?serviceId=portal-dsp";

    try {
      String result = HttpUtil.doGet(url);
      System.out.println(result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads the settings from the configurable component to the Swing form. This method is called on
   * EDT immediately after the form creation or later upon user's request.
   */
  @Override
  public void reset() {
    if (null != this.syncForm) {
      this.syncForm.setData(this.syncSetting);
    }
  }

  /**
   * Notifies the configurable component that the Swing form will be closed. This method should
   * dispose all resources associated with the component.
   */
  @Override
  public void disposeUIResources() {
    this.syncForm = null;
  }
}
