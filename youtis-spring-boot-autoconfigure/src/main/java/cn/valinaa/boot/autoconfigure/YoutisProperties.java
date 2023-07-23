package cn.valinaa.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Youtis.
 *
 * @author Valinaa
 */

@ConfigurationProperties(prefix = YoutisProperties.YOUTIS_PREFIX)
public class YoutisProperties {
    public static final String YOUTIS_PREFIX = "youtis";
    
    /**
     * The location of the SQL output file.
     */
    private String outputLocation= "youtis";
    
    public String getOutputLocation() {
        return outputLocation;
    }
    public void setOutputLocation(String outputLocation) {
        this.outputLocation = outputLocation;
    }
}
