package tech.valinaa.boot.autoconfigure.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Output properties
 *
 * @author Valinaa
 */
@ConfigurationProperties(prefix = "youtis.output")
public class OutputProperties {
    /**
     * Enable output SQL file.
     */
    private boolean enabled = true;
    /**
     * Path of the SQL output file.
     */
    private String path = "youtis";
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
