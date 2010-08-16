package hudson.plugins.rvmRake;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Ruby utility class. It's used to detect ruby local installations among other features.
 * 
 * @author David Calavera
 * @author Darcy Laycock 
 */
public class Util {
  
  public static String expandPath(String s) {
    s = hudson.Util.fixNull(s).trim();
    if(s.equals("")) return s;
    return s.replaceFirst("^~\\/", System.getProperty("user.home") + "/");
  }
  
  public static String[] getRakeCommandForRubyString(String rubyString, String... rakeArgs) {
    String[] args = new String[5 + rakeArgs.length];
    args[0] = "rvm";
    args[1] = "--with-rubies";
    args[2] = rubyString;
    args[3] = "exec";
    args[4] = "rake";
    for(int i = 0; i < rakeArgs.length; i++) args[i + 4] = rakeArgs[i];
    return args;
  }
  
  public static String normalizeRubyString(String s) {
    String normalized = hudson.Util.fixNull(s).trim();
    return normalized.equals("") ? "default" : normalized;
  }
  
}
