package hudson.plugins.rake;

import static hudson.plugins.rake.Util.getRakeCommandForRubyString;

import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Rake plugin main class.
 * 
 * @author David Calavera
 * @author Darcy Laycock
 */
@SuppressWarnings({"unchecked", "serial"})
public class Rake extends Builder {

  @Extension
  public static final RakeDescriptor DESCRIPTOR = new RakeDescriptor();
  private final String  rubyString;
  private final String  rakeFile;
  private final String  rakeLibDir;
  private final String  rakeWorkingDir;
  private final String  tasks;
  private final boolean silent;
  
  @DataBoundConstructor
  public Rake(String rubyString, String rakeFile, String tasks, String rakeLibDir, String rakeWorkingDir, boolean silent) {
    this.rubyString     = rubyString;
    this.rakeFile       = rakeFile;
    this.tasks          = tasks;
    this.rakeLibDir     = rakeLibDir;
    this.rakeWorkingDir = rakeWorkingDir;
    this.silent         = silent;
  }

  private Launcher getLastBuiltLauncher(AbstractBuild build, Launcher launcher, BuildListener listener) {
      AbstractProject project    = build.getProject();
      Node lastBuiltOn           = project.getLastBuiltOn();
      Launcher lastBuiltLauncher = launcher;
      if (lastBuiltOn != null)
        lastBuiltLauncher = lastBuiltOn.createLauncher(listener);
      return lastBuiltLauncher;
  }

  @Override
  public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException {
    ArgumentListBuilder args   = new ArgumentListBuilder();
    String normalizedTasks     = tasks.replaceAll("[\t\r\n]+"," ");        
    Launcher lastBuiltLauncher = getLastBuiltLauncher(build, launcher, listener);
    
    // Get the generated command for rvm exec.
    for(String command : getRakeCommandForRubyString(rubyString))
      args.add(command);
    
    if (rakeFile != null && rakeFile.length() > 0)
      args.add("--rakefile", rakeFile);
    if (rakeLibDir != null && rakeLibDir.length() > 0)
      args.add("--libdir", rakeLibDir);
    if (silent)
      args.add("--silent");

    FilePath workingDir = build.getModuleRoot();

    if (rakeWorkingDir != null && rakeWorkingDir.length() > 0)
      workingDir = new FilePath(build.getModuleRoot(), rakeWorkingDir);

    args.addTokenized(normalizedTasks);
    
    try {
      int r = lastBuiltLauncher.launch().cmds(args).envs(build.getEnvironment(listener)).stdout(listener).pwd(workingDir).join();
      return r == 0;
    } catch (IOException e) {
      Util.displayIOException(e,listener);
      e.printStackTrace(listener.fatalError("rake execution failed"));
      return false;
    }
  }  
  
  @Override
  public RakeDescriptor getDescriptor() {
    return DESCRIPTOR;
  }
    
  public String getRubyString() {
    return rubyString;
  }
    
  public String getRakeFile() {
    return rakeFile;
  }

  public String getRakeLibDir() {
    return rakeLibDir;
  }

  public String getTasks() {
    return tasks;
  }

  public boolean isSilent() {
    return silent;
  }  

  public String getRakeWorkingDir() {
    return rakeWorkingDir;
  }

  public static final class RakeDescriptor extends Descriptor<Builder> {  
    
    private RakeDescriptor() {
      super(Rake.class);
      load();
    }

    public String getDisplayName() {
      return "Invoke Rake (via RVM)";
    }
        
    @Override
    public Rake newInstance(StaplerRequest req, JSONObject formData) throws FormException {
      return (Rake)req.bindJSON(clazz,formData);
    }
        
    @Override
    public String getHelpFile() {
      return "/plugin/rake/help.html";
    }
  }  
}
