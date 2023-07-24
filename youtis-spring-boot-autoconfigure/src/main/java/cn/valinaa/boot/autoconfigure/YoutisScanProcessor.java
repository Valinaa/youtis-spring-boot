package cn.valinaa.boot.autoconfigure;

import cn.hutool.core.util.StrUtil;
import cn.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import cn.valinaa.boot.autoconfigure.annotation.YoutisScan;
import cn.valinaa.boot.autoconfigure.annotation.YoutisTable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Valinaa
 */
@Configuration
public class YoutisScanProcessor
        implements ImportBeanDefinitionRegistrar{

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
                            YoutisTable.class,false,true);
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
                            if(DDL==null){
                                logger.warn("DDL is null, Youtis will not work.");
                            }else{
                                logger.info(DDL);
                            }
                        }
                    }
            }else{
                logger.warn("No packages to scan, have you set package name in @YoutisScan ?");
            }
        }else{
            logger.warn("Not found @EntityScan, have you set it on Application ?");
        }
    }
    
    private String getFieldsNeedGenerated(Class<?> clazz){
        YoutisTable youtisTable =clazz.getAnnotation(YoutisTable.class);
        Field[] fields = clazz.getDeclaredFields();
        boolean hasYoutisColumn=false;
        String tableName= StrUtil.toUnderlineCase(youtisTable.value().isBlank()
                ?clazz.getSimpleName(): youtisTable.value());
        String tableComment= youtisTable.comment();
        for (Field field : fields) {
            if (field.isAnnotationPresent(YoutisColumn.class)) {
                hasYoutisColumn = true;
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
        if (hasYoutisColumn) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(YoutisColumn.class)) {
                    if(getDDL(lengthList, typeList,primaries, DDL, field,count)){
                        count++;
                    }
                }
            }
        }else {
            for (Field field : fields) {
                if(getDDL(lengthList, typeList,primaries, DDL, field,count)){
                    count++;
                }
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
