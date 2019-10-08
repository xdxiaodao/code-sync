package com.xdxiaodao.code.sync.form;

import com.xdxiaodao.code.sync.bean.SyncConfig;
import com.xdxiaodao.code.sync.bean.SyncType;
import com.xdxiaodao.code.sync.component.SyncSetting;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class SyncForm {

  private JPanel typePanel;
  private JPanel rootPanel;
  private JTabbedPane tabbedPane1;
  private JRadioButton pullRadioButton;
  private JRadioButton pushRadioButton;
  private JTextField pullUrl;
  private JTextField pullInterval;
  private JTextField pullSavePath;
  private JTextField pushUrl;
  private JTextField pushInterval;
  private JTextField pushPaths;
  private JButton pushPathSelector;
  private JButton pullPathSelector;
  private JTextField author;
  private JTextField email;
  private JTextField pushAuthor;
  private JTextField pushOrgnization;
  private JTextArea pushTemplate;

  public SyncForm(SyncSetting syncSetting) {
    super();
    this.setData(syncSetting);
    this.pushRadioButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (ItemEvent.SELECTED == e.getStateChange()) {
          pullRadioButton.setSelected(false);
          tabbedPane1.setSelectedIndex(1);
        } else {
          pullRadioButton.setSelected(true);
        }
      }
    });

    this.pullRadioButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (ItemEvent.SELECTED == e.getStateChange()) {
          pushRadioButton.setSelected(false);
          tabbedPane1.setSelectedIndex(0);
        } else {
          pushRadioButton.setSelected(true);
        }
      }
    });

    this.pullPathSelector.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int i = fileChooser.showOpenDialog(SyncForm.this.rootPanel);
        if (i == 0) {
          File file = fileChooser.getSelectedFile();
          SyncForm.this.pullSavePath.setText(file.getAbsolutePath());
        }
      }
    });

    this.pushPathSelector.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int i = fileChooser.showOpenDialog(SyncForm.this.rootPanel);
        if (i == 0) {
          File[] files = fileChooser.getSelectedFiles();

          StringBuffer sb = new StringBuffer();
          if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
              sb.append(file.getAbsolutePath()).append(",");
            }

            SyncForm.this.pushPaths.setText(sb.substring(0, sb.length() - 1));
          }
        }
      }
    });
  }

  public JPanel getRootPanel() {
    return this.rootPanel;
  }

  public void setData(SyncSetting syncSetting) {
    SyncConfig pullSyncConfig = null == syncSetting.getPullSyncConfig() ? new SyncConfig() : syncSetting.getPullSyncConfig();
    SyncConfig pushSyncConfig = null == syncSetting.getPushSyncConfig() ? new SyncConfig() : syncSetting.getPushSyncConfig();

    SyncType syncType = syncSetting.getSyncType();
    if (SyncType.PULL.equals(syncType)) {
      this.pullRadioButton.setSelected(true);
      this.pushRadioButton.setSelected(false);
      this.tabbedPane1.setSelectedIndex(0);
    } else if (SyncType.PUSH.equals(syncType)) {
      this.pullRadioButton.setSelected(false);
      this.pushRadioButton.setSelected(true);
      this.tabbedPane1.setSelectedIndex(1);
    } else {
      this.pullRadioButton.setSelected(false);
      this.pushRadioButton.setSelected(false);
      this.tabbedPane1.setSelectedIndex(0);
    }

    this.pullUrl.setText(pullSyncConfig.getUrl());
    this.pullInterval.setText(pullSyncConfig.getInterval() + "");
    this.pullSavePath.setText(pullSyncConfig.getPath());
    this.author.setText(pullSyncConfig.getAuthor());
    this.email.setText(pullSyncConfig.getEmail());

    this.pushUrl.setText(pushSyncConfig.getUrl());
    this.pushInterval.setText(pushSyncConfig.getInterval() + "");
    this.pushPaths.setText(pushSyncConfig.getPath());
    this.pushAuthor.setText(pushSyncConfig.getAuthor());
    this.pushOrgnization.setText(pushSyncConfig.getOrganization());
    this.pushTemplate.setText(pushSyncConfig.getTemplate());
  }

  public void getData(SyncSetting syncSetting) {
    syncSetting.setSyncType(getSyncType());
    syncSetting.setPullSyncConfig(getPullSyncConfig());
    syncSetting.setPushSyncConfig(getPushSyncConfig());
  }

  private SyncConfig getPushSyncConfig() {
    int interval = NumberUtils.toInt(this.pushInterval.getText().trim(), 10);
    if (interval < 10) {
      interval = 10;
    }

    SyncConfig pushSyncConfig = new SyncConfig();
    pushSyncConfig.setUrl(this.pushUrl.getText().trim());
    pushSyncConfig.setInterval(interval);
    pushSyncConfig.setPath(this.pushPaths.getText().trim());
    pushSyncConfig.setAuthor(this.pushAuthor.getText().trim());
    pushSyncConfig.setOrganization(this.pushOrgnization.getText().trim());
    pushSyncConfig.setTemplate(this.pushTemplate.getText().trim());
    return pushSyncConfig;
  }

  private SyncConfig getPullSyncConfig() {
    int interval = NumberUtils.toInt(this.pushInterval.getText().trim(), 10);
    if (interval < 10) {
      interval = 10;
    }

    SyncConfig pullSyncConfig = new SyncConfig();
    pullSyncConfig.setUrl(this.pullUrl.getText().trim());
    pullSyncConfig.setInterval(interval);
    pullSyncConfig.setPath(this.pullSavePath.getText().trim());
    pullSyncConfig.setAuthor(this.author.getText().trim());
    pullSyncConfig.setEmail(this.email.getText().trim());
    return pullSyncConfig;
  }

  private SyncType getSyncType() {
    if (this.pullRadioButton.isSelected()) {
      return SyncType.PULL;
    } else if (this.pushRadioButton.isSelected()) {
      return SyncType.PUSH;
    } else {
      return SyncType.NONE;
    }
  }

  public boolean isModified(SyncSetting syncSetting) {
    SyncConfig pullSyncConfig = syncSetting.getPullSyncConfig();
    SyncConfig pushSyncConfig = syncSetting.getPushSyncConfig();
    SyncType syncType = syncSetting.getSyncType();


    SyncType currSyncType = getSyncType();
    SyncConfig currPullSyncConfig = getPullSyncConfig();
    SyncConfig currPushSyncConfig = getPushSyncConfig();

    return !currSyncType.equals(syncType) || !currPullSyncConfig.equals(pullSyncConfig) || !currPushSyncConfig.equals(pushSyncConfig);
  }
}
