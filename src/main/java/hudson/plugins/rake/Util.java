package hudson.plugins.rake;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Ruby utility class. It's used to detect ruby local installations among other features.
 * 
 * @author David Calavera
 */
public class Util {

  public static File getRVMPath() {
    String[] pathsToCheck = new String[]{
      System.getenv("rvm_path"),
      System.getProperty("user.home") + "/.rvm",
      "/usr/local/rvm"
    };
    for(String envPath : pathsToCheck) {
      if(envPath == null) continue;
      File currentFile = new File(envPath);
      if(envPath.isDirectory()) return currentFile();
    }
    return null;
  }

  public static Collection<String> getRVMRubies() {
    return null;
  }

  public static boolean isValidRubyString(String rvmRubyString) {
    File wrapperDir = getRVMWrapperDirFor(rvmRubyString);
    return wrapperDir != null && wrapperDir.isDirectory() && new File(wrapperDir, "rake").exists();
  }

  public static File getRvmWrapperDirFor(String rvmRubyString) {
    return new File(getRVMPath(), "wrappers/" + rvmRubyString);
  }

  public static Collection<File> getRubyInstallations() throws IOException {
    String rvmPath = System.getenv("rvm_path");
    
    Collection<File> rubyVersions = new LinkedHashSet<File>();
    
    if (systemPath != null) {
      Set<String> candidates = new LinkedHashSet<String>(Arrays.asList(systemPath.split(File.pathSeparator)));
      for (String path : candidates) {
        for (String ruby : RUBY_EXECUTABLES) {
          File rubyExec = getExecutableWithExceptions(path, ruby);
          if (rubyExec.isFile() && 
              !rubyVersions.contains(rubyExec.getCanonicalFile().getParentFile())) {
            File parent = rubyExec.getCanonicalFile().getParentFile();
            File[] gemsDir = getGemsDir(parent.getCanonicalPath());
            
            if (!isRakeInstalled(gemsDir) && (isMac() || isJruby(parent.getParent()))) {
              parent = parent.getParentFile();
              gemsDir = getGemsDir(parent.getAbsolutePath());
            }
            
            if (gemsDir != null && isRakeInstalled(gemsDir)) {
              rubyVersions.add(parent);
            }
          }
        }
      }
    }
    
    return rubyVersions;
  }
  
  public static RubyInstallation[] getCanonicalRubies(RubyInstallation[] currentInstallations) {
    try {
      Collection<File> rubies = getRubyInstallations();
      Collection<RubyInstallation> currentList = new LinkedHashSet<RubyInstallation>(Arrays.asList(currentInstallations));
      
      out: for (File ruby : rubies) {
        for (RubyInstallation current : currentList) {
          if (current.getCanonicalExecutable().equals(getExecutable(ruby.getCanonicalPath()).getCanonicalFile())) {
            continue out;
          }
        }
        currentList.add(new RubyInstallation(ruby.getName(), ruby.getCanonicalPath()));
      }
          
      return currentList.toArray(new RubyInstallation[currentList.size()]);
    } catch (IOException e) {
      hudson.Util.displayIOException(e, null);
    }
    return new RubyInstallation[0];
  }
}
