package cn.valinaa.boot.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Description
 *
 * @author Valinaa
 */

@Service
public class DataSourceService {
    private static final Log logger = LogFactory.getLog(DataSourceService.class);
    private final DataSource dataSource;
    
    private final Youtis youtis;

    DataSourceService(DataSource dataSource, Youtis youtis) {
        this.dataSource = dataSource;
        this.youtis = youtis;
    }
    
    public void createTable(String SQLSentence) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQLSentence);
            System.out.println("Table created successfully!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    @Bean
    public void init(){
        logger.info(youtis.getOutputPath());
    }
    
}
