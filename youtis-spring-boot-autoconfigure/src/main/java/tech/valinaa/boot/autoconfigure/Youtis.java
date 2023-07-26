package tech.valinaa.boot.autoconfigure;

/**
 * @author Valinaa
 */

public class Youtis {
    private boolean enabled;
    
    private boolean execute;
    
    private boolean outputEnabled;
    
    private String outputPath;
    
    public Youtis(boolean enabled, boolean execute, boolean outputEnabled, String outputPath) {
        this.enabled = enabled;
        this.execute = execute;
        this.outputEnabled = outputEnabled;
        this.outputPath = outputPath;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isOutputEnabled() {
        return outputEnabled;
    }
    
    public void setOutputEnabled(boolean outputEnabled) {
        this.outputEnabled = outputEnabled;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
    
    public boolean isExecute() {
        return execute;
    }
    
    public void setExecute(boolean execute) {
        this.execute = execute;
    }
}
