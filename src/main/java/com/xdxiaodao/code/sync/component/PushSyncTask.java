package com.xdxiaodao.code.sync.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.xdxiaodao.code.sync.bean.StatusCodeDTO;
import com.xdxiaodao.code.sync.bean.StatusCodeDTO.SimpleStatusCode;
import com.xdxiaodao.code.sync.bean.SyncConfig;
import com.xdxiaodao.code.sync.utils.HttpUtil;
import com.xdxiaodao.code.sync.utils.JsonUtil;
import com.xdxiaodao.code.sync.utils.SyncUtil;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author xdxiaodao
 * @email xdxiaodao@gmail.com
 * @date 2019-08-08 16:58
 * @desc
 */
public class PushSyncTask implements Runnable {

  private Logger logger = SyncUtil.getLogger(PushSyncTask.class);

  private Project project;
  private SyncConfig pushSyncConfig;
  private int count = 0;
  private String latestContent = "";

  public PushSyncTask(Project project, SyncConfig syncConfig) {
    this.project = project;
    this.pushSyncConfig = syncConfig;
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
    String pushFilePathes = pushSyncConfig.getPath();
    if (StringUtils.isBlank(pushFilePathes)
        || StringUtils.isBlank(pushSyncConfig.getUrl())) {
      return;
    }

    String[] pushFilePathArr = pushFilePathes.split(",");
    if (ArrayUtils.isEmpty(pushFilePathArr)) {
      return;
    }

    List<Map<String, Object>> enumDataMap = Lists.newArrayList();
    for (String pushFilePath : pushFilePathArr) {
      List<Map<String, Object>> tmpEnumDataMap = ApplicationManager.getApplication().runReadAction(new Computable<List<Map<String, Object>>>() {
        @Override
        public List<Map<String, Object>> compute() {
          VirtualFileManager manager = VirtualFileManager.getInstance();
          VirtualFile virtualFile = manager
              .refreshAndFindFileByUrl(VfsUtil.pathToUrl(pushFilePath));

          if (virtualFile == null || !virtualFile.exists()) {
            return Lists.newArrayList();
          }

          PsiFile javaFile = PsiManager.getInstance(project).findFile(virtualFile);
          List<PsiClass> psiClasses = SyncUtil.getClasses(javaFile);
          List<Map<String, Object>> tmpEnumDataMap = getEnumDataMap(psiClasses);
          return tmpEnumDataMap;
        }
      });

      if (CollectionUtils.isNotEmpty(tmpEnumDataMap)) {
        enumDataMap.addAll(tmpEnumDataMap);
      }
    }

//    PsiFile javaFile = (PsiJavaFile) PsiFileFactory
//        .getInstance(project).createFileFromText("file://" + pullProjectPath, StdFileTypes.JAVA, "");

    if (CollectionUtils.isEmpty(enumDataMap)) {
      return;
    }

    // 处理数据
    StatusCodeDTO statusCodeDTO = assembleStatusCodeDTO(enumDataMap);

    try {
      if (null == statusCodeDTO) {
        return;
      }

      String statusCodeJson = JsonUtil.objectToJson(statusCodeDTO);
      if (statusCodeJson.equals(pushSyncConfig.getData())) {
        return;
      }

      String result = HttpUtil.doPostJson(pushSyncConfig.getUrl(), JsonUtil.objectToJson(statusCodeDTO));

      // 存储数据
      SyncSetting syncSetting = ServiceManager.getService(project, SyncSetting.class);
      if (StringUtils.isNotBlank(result)
          && null != syncSetting
          && null != syncSetting.getPushSyncConfig()) {
        syncSetting.getPushSyncConfig().setData(JsonUtil.objectToJson(statusCodeDTO));
      }
    } catch (Exception e) {
      logger.warn("[push sync task] push data has some exception!", e);
    } catch (Throwable t) {
      logger.warn("[push sync task] push data has some throwable!", t);
    }

//    System.out.println("push sync task:" + count + ", interval:" + pushSyncConfig.getInterval() + "enumdatamap:" + new Gson().toJson(enumDataMap));
  }

  private StatusCodeDTO assembleStatusCodeDTO(List<Map<String, Object>> enumDataList) {
    String template = pushSyncConfig.getTemplate();
    try {
      StringReader stringReader = new StringReader(template);
      Properties properties = new Properties();
      properties.load(stringReader);
      if (properties.isEmpty()) {
        properties.put("code", "code");
        properties.put("description", "description");
        properties.put("suggestion", "suggestion");
      }

      List<SimpleStatusCode> simpleStatusCodes = Lists.newArrayList();
      for (Map<String, Object> enumDataMap : enumDataList) {

        String enumName = Objects.toString(enumDataMap.get("enumName"));
        if (StringUtils.isBlank(enumName)) {
          continue;
        }

        int code = NumberUtils.toInt(Objects.toString(enumDataMap.get(properties.getProperty("code"))));
        String desc = Objects.toString(enumDataMap.get(properties.getProperty("description")));
        String suggestion = Objects.toString(enumDataMap.get(properties.getProperty("suggestion")));

        SimpleStatusCode simpleStatusCode = new SimpleStatusCode();
        simpleStatusCode.setName(enumName);
        simpleStatusCode.setCode(code);
        simpleStatusCode.setDescription(desc);
        simpleStatusCode.setSuggestion(suggestion);
        simpleStatusCodes.add(simpleStatusCode);
      }

      StatusCodeDTO statusCodeDTO = new StatusCodeDTO();
      statusCodeDTO.setSysName(project.getName());
      statusCodeDTO.setOrganization(pushSyncConfig.getOrganization());
      statusCodeDTO.setUserName(pushSyncConfig.getAuthor());
      statusCodeDTO.setSimpleStatusCodeList(simpleStatusCodes);
      return statusCodeDTO;
    } catch (Exception e) {
      logger.warn("[push sync task] assemble statuscodedto has some exception!", e);
    }
    return null;
  }

  @Nullable
  private List<Map<String, Object>> getEnumDataMap(List<PsiClass> psiClasses) {
    List<Map<String, Object>> enumDataMap = Lists.newArrayList();
    try {
//      PsiFile javaFile = PsiManager.getInstance(project).findFile(virtualFile);
//      List<PsiClass> psiClasses = SyncUtil.getClasses(javaFile);
      if (CollectionUtils.isEmpty(psiClasses)) {
        return null;
      }

      List<StatusCodeDTO> statusCodes = Lists.newArrayList();

//    Map<PsiClass, List<>>
      for (PsiClass psiClass : psiClasses) {
        if (null == psiClass || !psiClass.isEnum()) {
          continue;
        }

        // 查找枚举构造函数
        List<Pair<String, String>> nameTypePairList = Lists.newArrayList();
        PsiMethod[] constructors = psiClass.getConstructors();
        if (ArrayUtils.isEmpty(constructors)) {
          continue;
        }

        for (PsiMethod psiMethod : constructors) {
          JvmParameter[] jvmParameters = psiMethod.getParameters();
          if (ArrayUtils.isEmpty(jvmParameters)) {
            continue;
          }

          // 只处理包含code，desc或者code，desc，suggest的
          if (jvmParameters.length < 2) {
            continue;
          }

          for (JvmParameter jvmParameter : jvmParameters) {
            String name = jvmParameter.getName();
            String typeName = jvmParameter.getType().toString();
            nameTypePairList.add(new Pair<>(name, typeName));
          }
        }

        // 查找枚举值
//    ((PsiExpressionList) ((PsiEnumConstant)psiClasses.get(0).getFields()[0]).getArgumentList()).getExpressions()[1]
        PsiField[] psiFields = psiClass.getFields();
        for (PsiField psiField : psiFields) {
          if (!(psiField instanceof PsiEnumConstant)) {
            continue;
          }

          PsiEnumConstant enumConstant = (PsiEnumConstant) psiField;
          PsiExpressionList psiExpressionList = enumConstant.getArgumentList();
          if (null == psiExpressionList || psiExpressionList.isEmpty()) {
            continue;
          }

          // 表达式数量不一致，则跳过
          if (nameTypePairList.size() != psiExpressionList.getExpressionCount()) {
            continue;
          }
          int count = -1;
          Map<String, Object> objectMap = Maps.newHashMap();
          for (PsiExpression psiExpression : psiExpressionList.getExpressions()) {
            count++;
            if (null == psiExpression.getType()) {
              continue;
            }

            String type = psiExpression.getType().getCanonicalText();
            if ("int".equalsIgnoreCase(type)
                || "long".equalsIgnoreCase(type)
                || "short".equalsIgnoreCase(type)) {
              objectMap.put(nameTypePairList.get(count).getKey(), NumberUtils.toInt(psiExpression.getText()));
            } else if ("java.lang.String".equalsIgnoreCase(type)) {
              String content = StringUtils.isBlank(psiExpression.getText()) ? "" : psiExpression.getText();
              content = content.replaceAll("\"", "");
              objectMap.put(nameTypePairList.get(count).getKey(), content);
            }
          }
          objectMap.put("enumName", enumConstant.getName());
          enumDataMap.add(objectMap);

        }

      }
    } catch (Exception e) {
      logger.warn("[push sync task] get enum data has some exception!", e);
    } catch (Throwable t) {
      logger.warn("[push sync task] get enum data has some throwable!", t);
    }
    return enumDataMap;
  }

  /**
   * 设置同步配置
   *
   * @param syncConfig 同步配置
   */
  public void setSyncConfig(SyncConfig syncConfig) {
    this.pushSyncConfig = syncConfig.snapshot();
  }
}
