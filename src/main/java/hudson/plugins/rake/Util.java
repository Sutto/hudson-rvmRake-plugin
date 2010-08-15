package hudson.plugins.rake;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import hudson.plugins.rake.RubyInstallation;

/**
 * Ruby utility class. It's used to detect ruby local installations among other features.
 * 
 * @author David Calavera
 * @author Darcy Laycock 
 */
public class Util {
  
  public static File getRvmPath() {
    String[] pathsToCheck = new String[]{
      Rake.DESCRIPTOR.getRvmPath(),
      System.getenv("rvm_path"),
      System.getProperty("user.home") + "/.rvm",
      "/usr/local/rvm"
    };
    for(String envPath : pathsToCheck) {
      if(envPath == null) continue;
      File currentFile = new File(envPath);
      if(currentFile.isDirectory()) return currentFile;
    }
    return null;
  }

  public static Collection<String> getRvmRubies() {
    Collection<String> rubyStrings = new LinkedHashSet<String>();
    File file = getRvmPath();
    for(File f : file.listFiles())
      rubyStrings.add(f.getName());
    return rubyStrings;
  }
  
  public static Collection<RubyInstallation> getRubyInstallations() {
    Collection<RubyInstallation> installations = new LinkedHashSet<RubyInstallation>();
    for(String rubyString : getRvmRubies())
      installations.add(new RubyInstallation(rubyString));
    return installations;
  }

  public static boolean isValidRubyString(String rvmRubyString) {
    File wrapperDir = getRvmWrapperDirFor(rvmRubyString);
    return wrapperDir != null && wrapperDir.isDirectory() && new File(wrapperDir, "rake").exists();
  }

  public static File getRvmWrapperDirFor(String rvmRubyString) {
    return new File(getRvmPath(), "wrappers/" + rvmRubyString);
  }
  
  public static File getExecutable(File f) {
    return f == null ? f : new File(f, "rake");
  }

  public static boolean isValidRvmPathValue(String value) {
    File f = new File(value);
    // Check null data first.
    if(value == null || value.equals("")) return true;
    // Otherwise, check if it is a valid path (namely, has a wrappers dir)
    return f.isDirectory() && new File(f, "wrappers").isDirectory();
  }
  
}
