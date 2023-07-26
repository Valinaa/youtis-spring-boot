package tech.valinaa.boot.autoconfigure.property;

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
     * Enable Youtis.
     */
    private boolean enabled = true;
    /**
     * Whether to execute DDL directly.
     */
    private boolean execute = false;
    /**
     * Output properties.
     */
    private OutputProperties outputProperties;
    
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isExecute() {
        return execute;
    }
    
    public void setExecute(boolean execute) {
        this.execute = execute;
    }
    
    public OutputProperties getOutputProperties() {
        return outputProperties;
    }
    
    public void setOutputProperties(OutputProperties outputProperties) {
        this.outputProperties = outputProperties;
    }
}
