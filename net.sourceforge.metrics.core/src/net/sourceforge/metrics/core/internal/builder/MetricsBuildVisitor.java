package net.sourceforge.metrics.core.internal.builder;

import java.util.Stack;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * implements both the ResourceVisitor for a full build and the DeltaVisitor for
 * an incremental build. pushes commands on a stack so that they get executed in
 * a depth first order.
 * 
 * @author Frank Sauer
 */
class MetricsBuildVisitor implements IResourceVisitor, IResourceDeltaVisitor {
	
 	private final MetricsBuilder builder;
	private Stack stack = new Stack();
	private IProgressMonitor monitor;

    MetricsBuildVisitor(MetricsBuilder builder, IProgressMonitor monitor) {
   		this.builder = builder;
		this.monitor = monitor;
    }

   /**
    * pushes ChangedCommand for all resources in a project (full build)
    */
   public boolean visit(IResource res) {
		this.builder.checkCancel(monitor);
		FilterResult result = filter(res);
		return result.process(stack);
   }

   /**
    * filters and translates the resource thrown at it from the builder
    * @param res
    * @return FilterResult
    */
   private FilterResult filter(IResource resource) {
		FilterResult result = new FilterResult();
   		result.setProcessChildren(false);
   		if (resource == null) return result;
		IJavaElement element = JavaCore.create(resource);
		if (element == null) {
			// deal with high-level intermediate non-sourcefolders
			// that occur when linking in source folders from outside
			// the workspace or other bizar projects, skip the folder
			// but process its children...
			if (resource.getType() == IResource.FOLDER) {
				result.setProcessChildren(true);
			}
			return result;
		}
		
		// same thing shows up once in classes, once in src...
		if (stack.contains(element)) return result;
		
		// skip jars/zips
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
			IPackageFragmentRoot candidate = (IPackageFragmentRoot)element;
			if (candidate.isArchive()) return result;
		}
		
		// check for default package in a source folder
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
			IPackageFragmentRoot candidate = (IPackageFragmentRoot)element;
			IPackageFragment defPackage = candidate.getPackageFragment("");
			try {
				if ((defPackage != null)&&(defPackage.hasChildren()))
					result.setDefaultPackage(defPackage);
			} catch (JavaModelException e) {
			}
		}
		
		// check for a default package in a project (BUG #766261)
	    if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
			IJavaProject p = (IJavaProject)element;
			IPackageFragmentRoot r = p.getPackageFragmentRoot(resource);
			if (r.exists()) { // BUG #931018 
				// PackageFragmentRoot == JavaProject (same resource)!!!					
				result.setDefaultSourceFolder(r);
				IPackageFragment dp = r.getPackageFragment("");
				if (dp != null) {
					result.setDefaultPackage(dp);
				}
			}
	    }
	    
		// skip java files in regular (non-source) folders
		if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
				try {
					element.getUnderlyingResource();
				} catch (JavaModelException e) {
					return result;
				}
		}
		
		// skip class files
		if (element.getElementType() == IJavaElement.CLASS_FILE) return result;
		
		// skip empty parent packages and binary packages
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
			IPackageFragment pack = (IPackageFragment)element;
			try {
				result.setProcessChildren(pack.getKind()==IPackageFragmentRoot.K_SOURCE);
				if ((!result.isProcessChildren())||(pack.getCompilationUnits().length == 0)) {
					return result;
				}
			} catch (JavaModelException e) {
				return result;
			}
		}
		result.setElement(element);
		return result;
   }

   /**
    * determines the kind of command needed and pushes it on the stack.
    * Used by incremental builds
    */
   public boolean visit(IResourceDelta delta) {
		this.builder.checkCancel(monitor);
   		FilterResult result = filter(delta.getResource());
   		return result.process(stack, delta);	   		
   }

   /**
    * Queue commands in UI mode or execute them immediately in headless mode
    *
    */
   public void execute() {
	   	if (MetricsBuilder.isHeadless()) {
	   		executeHeadless();
	   	} else {
	   		executeUI();
	   	}
   }

    /**
	 * execute commands in the foreground in headless mode so Ant task waits for completion
	 */
	private void executeHeadless() {}

	/**
    * Executes all commands on the stack by popping them off until empty
    * fires progress events (pending and completed to listeners)
    */
   private void executeUI() {}

}