package tech.valinaa.boot.autoconfigure;

import cn.hutool.core.text.CharSequenceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    
    private static final Logger log = LogManager.getLogger(BeanManagement.class);
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
            log.info("Attention: Youtis has Disabled.");
            return;
        }
        log.info("Youtis has Enabled.Start checking the properties.");
        checkYoutisProperties();
        var classBeans = applicationContext.getBeansWithAnnotation(YoutisTable.class);
        var dataSource = applicationContext.getBean(DataSource.class);
        classBeans.forEach((k, v) -> {
            k = k.split("\\.")[k.split("\\.").length - 1];
            var ddl = "";
            try {
                ddl = getFieldsNeedGenerated(Class.forName(v.getClass().getName()));
            } catch (ClassNotFoundException e) {
                log.error("Unexpected error: A expected class is not found.", e);
            }
            log.debug("Class `{}`: Below is the DDL.\n{}", k, ddl);
            // Output
            if (youtis.isOutputEnabled()) {
                OperationUtil.outputDDL(youtis.getOutputPath(), k, ddl);
            }
            // Execute
            if (youtis.isExecute()) {
                OperationUtil.createTable(k, dataSource, ddl);
            }
        });
    }
    
    private void checkYoutisProperties() {
        if (youtis.isOutputEnabled()) {
            log.info("Output Enabled, the DDL file will be output.");
            if (Objects.isNull(youtis.getOutputPath())
                    || youtis.getOutputPath().isBlank()
                    || "youtis".equals(youtis.getOutputPath())) {
                log.info("Output path is blank, use default path instead.");
            }
            log.debug("Output path is `{}`.", youtis::getOutputPath);
        } else {
            log.info("Output Disabled, the DDL file will not be output.");
        }
        if (youtis.isExecute()) {
            log.info("Execute Enabled, the DDL will be executed directly.");
        } else {
            log.info("Execute Disabled, the DDL will not be executed, " +
                    "recommend to use output mode instead.");
        }
        if (!youtis.isOutputEnabled() && !youtis.isExecute()) {
            log.warn("Output and Execute are both disabled, if you want to check the DDL," +
                    "try to enable output or use `debug`");
        }
    }
    
    private String getFieldsNeedGenerated(Class<?> clazz) {
        var fields = clazz.getDeclaredFields();
        var hasYoutisColumn = Arrays.stream(fields)
                .anyMatch(field -> field.isAnnotationPresent(YoutisColumn.class));
        var youtisTable = clazz.getAnnotation(YoutisTable.class);
        var tableName = CharSequenceUtil.toUnderlineCase(youtisTable.value().isBlank()
                ? clazz.getSimpleName() : youtisTable.value());
        
        var ddl = new StringBuilder(CharSequenceUtil.format(
                "CREATE TABLE IF NOT EXISTS `{}`(\n", tableName));
        int count = 0;
        List<String> lengthList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> primaries = new ArrayList<>();
        for(var field : fields){
            var shouldProcess = !hasYoutisColumn || (field.isAnnotationPresent(YoutisColumn.class) || field.isAnnotationPresent(YoutisPrimary.class));
            if(shouldProcess&&getDDL(lengthList, typeList, primaries,ddl,field,count,hasYoutisColumn)){
                count++;
            }
        }
        var lengthLogContent = "You have not set the length of field {} in Class " + clazz.getSimpleName() + ", " +
                "it will be set to default value.";
        var typeLogContent = "The types of field {} you set may not be recommended, " +
                "they will follow your setting , but ensure they are right.";
        if (!lengthList.isEmpty()) {
            log.info(() -> CharSequenceUtil.format(lengthLogContent, lengthList.toString()));
        }
        if (!typeList.isEmpty()) {
            log.warn(() -> CharSequenceUtil.format(typeLogContent, typeList.toString()));
        }
        if (primaries.isEmpty()) {
            ddl.delete(ddl.length() - 2, ddl.length());
            log.warn("Class `{}`: You have not set primary key in this table !", clazz.getSimpleName());
        } else {
            String primaryStr = Arrays.stream(primaries.toString()
                            .replace("[", "").replace("]", "")
                            .split(",")).map(s -> "`" + s.trim() + "`").toList().toString()
                    .replace("[", "").replace("]", "");
            ddl.append(CharSequenceUtil.format("\tPRIMARY KEY({})", primaryStr));
        }
        
        var tableComment = youtisTable.comment();
        ddl.append(CharSequenceUtil.format("\n){};",
                tableComment.isBlank() ? "" : CharSequenceUtil.format(" COMMENT '{}'", tableComment)));
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
        return ddlHeader+ ddl;
    }
    
    private boolean getDDL(List<String> lengthInfo, List<String> typeWarn, List<String> primaries,
                            StringBuilder ddl, Field field, int count, boolean optional) {
        var res = ColumnValidation.validate(field, count, optional);
        if (res.isEmpty()) {
            return false;
        }
        String columnDDL = res.get("result").get(0).trim();
        ddl.append("\t").append(columnDDL).append(",\n");
        lengthInfo.addAll(res.get("lengthInfo"));
        typeWarn.addAll(res.get("typeWarning"));
        primaries.addAll(res.get("primary"));
        return res.get("autoIncrement").isEmpty();
    }
}
