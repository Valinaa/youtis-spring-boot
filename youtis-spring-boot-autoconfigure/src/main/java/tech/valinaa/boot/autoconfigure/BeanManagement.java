package tech.valinaa.boot.autoconfigure;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import tech.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import tech.valinaa.boot.autoconfigure.annotation.YoutisTable;
import tech.valinaa.boot.autoconfigure.utils.ColumnValidation;
import tech.valinaa.boot.autoconfigure.utils.OperationUtil;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;

@Component
@AutoConfigureAfter(YoutisAutoConfiguration.class)
public class BeanManagement {
    
    private static final Logger logger = LoggerFactory.getLogger(BeanManagement.class);
    private final ApplicationContext applicationContext;
    private final Youtis youtis;
    
    @Autowired
    public BeanManagement(ApplicationContext applicationContext,
                          Youtis youtis) {
        this.applicationContext = applicationContext;
        this.youtis = youtis;
    }
    
    @Bean
    public void init() {
        if (!youtis.isEnabled()) {
            logger.info("Attention: Youtis has Disabled.");
            return;
        }
        logger.info("Youtis has Enabled.Start checking the properties.");
        checkYoutisProperties();
        var classBeans = applicationContext.getBeansWithAnnotation(YoutisTable.class);
        var dataSource = applicationContext.getBean(DataSource.class);
        classBeans.forEach((k, v) -> {
            k = k.split("\\.")[k.split("\\.").length - 1];
            var DDL = "";
            try {
                DDL = getFieldsNeedGenerated(Class.forName(v.getClass().getName()));
            } catch (ClassNotFoundException e) {
                logger.error("Unexpected error: A expected class is not found.", e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Class `{}`: Below is the DDL.\n{}", k, DDL);
            }
            // Output
            if (youtis.isOutputEnabled()) {
                OperationUtil.outputDDL(youtis.getOutputPath(), k, DDL);
            }
            // Execute
            if (youtis.isExecute()) {
                OperationUtil.createTable(k, dataSource, DDL);
            }
        });
    }
    
    private void checkYoutisProperties() {
        if (youtis.isOutputEnabled()) {
            logger.info("Output Enabled, the DDL file will be output.");
            if (Objects.isNull(youtis.getOutputPath())
                    || youtis.getOutputPath().isBlank()
                    || "youtis".equals(youtis.getOutputPath())) {
                logger.info("Output path is blank, use default path instead.");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Output path is `{}`.", youtis.getOutputPath());
            }
        } else {
            logger.info("Output Disabled, the DDL file will not be output.");
        }
        if (youtis.isExecute()) {
            logger.info("Execute Enabled, the DDL will be executed directly.");
        } else {
            logger.info("Execute Disabled, the DDL will not be executed, " +
                    "recommend to use output mode instead.");
        }
        if (!youtis.isOutputEnabled() && !youtis.isExecute()) {
            logger.warn("Output and Execute are both disabled, if you want to check the DDL," +
                    "try to enable output or use `debug`");
        }
    }
    
    private String getFieldsNeedGenerated(Class<?> clazz) {
        var fields = clazz.getDeclaredFields();
        var hasYoutisColumn = false;
        for (Field field : fields) {
            if (field.isAnnotationPresent(YoutisColumn.class)) {
                hasYoutisColumn = true;
                break;
            }
        }
        var youtisTable = clazz.getAnnotation(YoutisTable.class);
        var tableName = StrUtil.toUnderlineCase(youtisTable.value().isBlank()
                ? clazz.getSimpleName() : youtisTable.value());
        
        var DDL = new StringBuilder(StrUtil.format(
                "CREATE TABLE IF NOT EXISTS `{}`(\n", tableName));
        int count = 0;
        var lengthList = new ArrayList<String>();
        var typeList = new ArrayList<String>();
        var primaries = new ArrayList<String>();
        if (hasYoutisColumn) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(YoutisColumn.class)
                        || field.isAnnotationPresent(YoutisPrimary.class)) {
                    if (getDDL(lengthList, typeList, primaries, DDL, field, count, true)) {
                        count++;
                    }
                }
            }
        } else {
            for (Field field : fields) {
                if (getDDL(lengthList, typeList, primaries, DDL, field, count, false)) {
                    count++;
                }
            }
        }
        
        var lengthLogContent = "You have not set the length of field {} in Class " + clazz.getSimpleName() + ", " +
                "it will be set to default value.";
        var typeLogContent = "The types of field {} you set may not be recommended, " +
                "they will follow your setting , but ensure they are right.";
        if (!lengthList.isEmpty()) {
            logger.info(StrUtil.format(lengthLogContent, lengthList.toString()));
        }
        if (!typeList.isEmpty()) {
            logger.warn(StrUtil.format(typeLogContent, typeList.toString()));
        }
        if (primaries.isEmpty()) {
            DDL.delete(DDL.length() - 2, DDL.length());
            logger.warn("Class `{}`: You have not set primary key in this table !", clazz.getSimpleName());
        } else {
            String primaryStr = Arrays.stream(primaries.toString()
                            .replace("[", "").replace("]", "")
                            .split(",")).map(s -> "`" + s.trim() + "`").toList().toString()
                    .replace("[", "").replace("]", "");
            DDL.append(StrUtil.format("\tPRIMARY KEY({})", primaryStr));
        }
        
        var tableComment = youtisTable.comment();
        DDL.append(StrUtil.format("\n){};",
                tableComment.isBlank() ? "" : StrUtil.format(" COMMENT '{}'", tableComment)));
        var ddlHeader = """
                /*
                 * Auto generated by Youtis.
                 * Youtis is a tool for generating DDL automatically.
                 * You can get more information from https://github.com/Valinaa/youtis-spring-boot.
                 *
                 * Server Type: MySQL
                 *
                 */
                
                """;
        return ddlHeader+ DDL;
    }
    
    private boolean getDDL(List<String> lengthInfo, List<String> typeWarn, List<String> primaries,
                            StringBuilder DDL, Field field, int count, boolean optional) {
        var res = Optional.ofNullable(ColumnValidation.validate(field, count, optional));
        if (res.isEmpty()) {
            return false;
        }
        String columnDDL = res.get().get("result").get(0).trim();
        DDL.append("\t").append(columnDDL).append(",\n");
        lengthInfo.addAll(res.get().get("lengthInfo"));
        typeWarn.addAll(res.get().get("typeWarning"));
        primaries.addAll(res.get().get("primary"));
        return res.get().get("autoIncrement").isEmpty();
    }
}
