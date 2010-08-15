package hudson.plugins.rake;

import java.io.File;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A Ruby Installation Name.
 *
 * @author David Calavera
 * @author Darcy Laycock
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
  
  public File getPath() {
    return Util.getRvmWrapperDirFor(name);
  }
  
  public File getExecutable() {
    return Util.getExecutable(getPath());
  }
  
  public File getCanonicalExecutable() throws IOException {
    return Util.getExecutable(getPath()).getCanonicalFile();
  }  
}
