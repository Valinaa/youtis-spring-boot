package cn.valinaa.boot.autoconfigure;

/**
 * @author Valinaa
 */

public class Youtis {
    private boolean enabled;
    
    private boolean outputEnabled;
    
    private String outputPath;
    
    public Youtis(boolean enabled, boolean outputEnabled, String outputPath) {
        this.enabled = enabled;
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
}
