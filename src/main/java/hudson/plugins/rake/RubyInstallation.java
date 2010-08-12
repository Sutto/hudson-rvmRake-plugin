package hudson.plugins.rake;

import java.io.File;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Ruby installation paths, managed by rvm.
 * @author David Calavera
 *
 */
public final class RubyInstallation {
  private final String name;
  
  @DataBoundConstructor
  public RubyInstallation(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public String getPath() {
    return "/dev/null";
  }
  
  public File getExecutable() {
    return Util.getExecutable(getPath());
  }
  
  public File getCanonicalExecutable() throws IOException {
    return Util.getExecutable(getPath()).getCanonicalFile();
  }  
}
