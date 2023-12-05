package tech.valinaa.boot.autoconfigure.utils;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.CharSequenceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.File;
import java.util.Objects;

/**
 * Util for operation(such as generate file and execute sql).
 *
 * @author Valinaa
 */
public final class OperationUtil {
    private static final Logger log = LogManager.getLogger(OperationUtil.class);
    
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
    
    public static void outputDDL(String path, String name, String ddl) {
        String targetDir = getResourcesDir() + CharSequenceUtil
                .replace(path, "/", File.separator)
                .replace("\\", File.separator) + File.separator;
        if (!FileUtil.exist(targetDir)) {
            FileUtil.mkdir(targetDir);
        }
        var tableName = CharSequenceUtil.toUnderlineCase(name);
        var fileName = CharSequenceUtil.format("{}_ddl.sql", tableName);
        path = CharSequenceUtil.format("{}/{}", path, fileName);
        log.info("Table `{}` :Output to `{}` started.", name, path);
        try {
            FileUtil.touch(targetDir + fileName);
            FileUtil.writeString(ddl, targetDir + fileName, "UTF-8");
        } catch (IORuntimeException e) {
            log.error("Error in output.", e);
        }
        log.info("Table `{}` :Output to `{}` completed.", name, path);
    }
    
    public static void createTable(String name, DataSource dataSource, String sqlSentence) {
        try (var statement = dataSource.getConnection().createStatement()) {
            statement.executeUpdate(sqlSentence);
            log.info("Class `{}` : Table `{}` has created completely.",
                    () -> name, () -> CharSequenceUtil.toUnderlineCase(name));
        } catch (Exception e) {
            log.error("Error in SQL Execute.", e);
        }
    }
}
