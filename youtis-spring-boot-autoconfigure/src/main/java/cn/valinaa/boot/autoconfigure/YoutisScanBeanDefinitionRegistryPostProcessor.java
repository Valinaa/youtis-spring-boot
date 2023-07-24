package cn.valinaa.boot.autoconfigure;

import cn.hutool.core.util.StrUtil;
import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;
import cn.valinaa.boot.autoconfigure.annotation.YoutisScan;
import cn.valinaa.boot.autoconfigure.utils.ColumnValidation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Valinaa
 */
@Configuration
public class YoutisScanBeanDefinitionRegistryPostProcessor
        implements ImportBeanDefinitionRegistrar {
    
    private static final Log logger = LogFactory.getLog(
            YoutisScan.class);
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        
        var annotations=importingClassMetadata.getAnnotations();
        if(annotations.isPresent(YoutisScan.class)){
            var basePackages=annotations.get(YoutisScan.class)
                    .getStringArray("value");
            if (basePackages.length > 0) {
                    var scanner = new ClassPathBeanDefinitionScanner(registry, false);
                    var tableClassAnnotationFilter = new AnnotationTypeFilter(
                            TableClass.class,false,true);
                    scanner.addIncludeFilter(tableClassAnnotationFilter);
                    for (String basePackage : basePackages) {
                        var beanDefinitions = scanner.findCandidateComponents(basePackage);
                        for (BeanDefinition beanDefinition : beanDefinitions) {
                            var className = beanDefinition.getBeanClassName();
                            String DDL = null;
                            try {
                                DDL=getFieldsNeedGenerated(Class.forName(className));
                            } catch (ClassNotFoundException e) {
                                logger.error(e.getMessage(),e);
                            }
                            logger.info(DDL);
                            // TODO !important
                        }
                    }
            }else{
                logger.warn("No package to scan, have you set package name in @YoutisScan ?");
            }
        }else{
            logger.warn("Not found @EntityScan, have you set it on Application ?");
        }
    }
    
    private String getFieldsNeedGenerated(Class<?> clazz){
        TableClass tableClass=clazz.getAnnotation(TableClass.class);
        Field[] fields = clazz.getDeclaredFields();
        boolean hasColumnUsed=false;
        String tableName= StrUtil.toUnderlineCase(tableClass.value().isBlank()
                ?clazz.getSimpleName():tableClass.value());
        String tableComment=tableClass.comment();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ColumnUsed.class)) {
                hasColumnUsed = true;
                break;
            }
        }
        List<String> lengthList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> primaries=new ArrayList<>();
        String lengthLogContent="You have not set the length of field {} in Class "+clazz.getSimpleName() +", " +
                "it will be set to default value.";
        String typeLogContent="The types of field {} you set may not be recommended, " +
                "they will follow your setting , but ensure they are right.";
        StringBuilder DDL=new StringBuilder(StrUtil.format(
                "CREATE TABLE IF NOT EXISTS `{}`(\n", tableName));
        int count=0;
        if (hasColumnUsed) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(ColumnUsed.class)) {
                    if(getDDL(lengthList, typeList,primaries, DDL, field,count)){
                        count++;
                    };
                }
            }
        }else {
            for (Field field : fields) {
                if(getDDL(lengthList, typeList,primaries, DDL, field,count)){
                    count++;
                };
            }
        }
        if(!lengthList.isEmpty()){
            logger.info(StrUtil.format(lengthLogContent,lengthList.toString()));
        }
        if(!typeList.isEmpty()){
            logger.warn(StrUtil.format(typeLogContent,typeList.toString()));
        }
        if(primaries.isEmpty()){
            DDL.delete(DDL.length()-2,DDL.length());
            logger.warn("Class `"+clazz.getSimpleName()+"`: You have not set primary key in this table !");
        }else{
            DDL.append(StrUtil.format("PRIMARY KEY({})", primaries.toString()
                    .replace("[","")
                    .replace("]","")));
        }
        DDL.append(StrUtil.format("\n){};",
                tableComment.isBlank()?"":StrUtil.format(" COMMENT '{}'",tableComment)));
        return DDL.toString();
    }
    
    private boolean getDDL(List<String> lengthInfo, List<String> typeWarn,List<String> primaries,
                           StringBuilder DDL, Field field,int count) {
        Map<String,List<String>> res= ColumnValidation.validate(field,count);
        String columnDDL=res.get("result").get(0).trim();
        DDL.append(columnDDL).append(",\n");
        lengthInfo.addAll(res.get("lengthInfo"));
        typeWarn.addAll(res.get("typeWarning"));
        primaries.addAll(res.get("primary"));
        return res.get("autoIncrement").isEmpty();
    }
}
