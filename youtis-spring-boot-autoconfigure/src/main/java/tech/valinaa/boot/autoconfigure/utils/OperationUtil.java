package tech.valinaa.boot.autoconfigure.utils;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.util.Objects;

/**
 * Util for operation(such as generate file and execute sql).
 *
 * @author Valinaa
 */
public final class OperationUtil {
    private static final Logger logger = LoggerFactory.getLogger(OperationUtil.class);
    
    private OperationUtil() {
    }
    
    public static String getResourcesDir() {
        String file = Objects.requireNonNull(OperationUtil.class.getClassLoader()
                .getResource("")).getPath();
        while (!file.substring(file.length() - 7).equals(File.separator + "target")) {
            file = FileUtil.getParent(file, 1);
        }
        file = FileUtil.getParent(file, 1) +
                File.separator + "src" + File.separator + "main" +
                File.separator + "resources" + File.separator;
        return file;
    }
    
    public static void outputDDL(String path, String name, String DDL) {
        String targetDir = getResourcesDir() + StrUtil
                .replace(path, "/", File.separator)
                .replace("\\", File.separator) + File.separator;
        if (!FileUtil.exist(targetDir)) {
            FileUtil.mkdir(targetDir);
        }
        var tableName = StrUtil.toUnderlineCase(name);
        var fileName = StrUtil.format("{}_ddl.sql", tableName);
        path = StrUtil.format("{}/{}", path, fileName);
        logger.info("Table `{}` :Output to `{}` started.", name, path);
        try {
            FileUtil.touch(targetDir + fileName);
            FileUtil.writeString(DDL, targetDir + fileName, "UTF-8");
        } catch (IORuntimeException e) {
            logger.error("Error in output.", e);
        }
        logger.info("Table `{}` :Output to `{}` completed.", name, path);
    }
    
    public static void createTable(String name, DataSource dataSource, String SQLSentence) {
        try (var statement = dataSource.getConnection().createStatement()) {
            statement.executeUpdate(SQLSentence);
            logger.info("Class `{}` : Table `{}` has created completely.",
                    name, StrUtil.toUnderlineCase(name));
        } catch (Exception e) {
            logger.error("Error in SQL Execute.", e);
        }
    }
}
