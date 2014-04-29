package net.sourceforge.metrics.core.internal.builder;

import java.util.Map;

import net.sourceforge.metrics.core.Log;
import net.sourceforge.metrics.core.internal.MetricStore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Calculates all the enabled metrics for the project being built
 * 
 * @author Frank Sauer
 */
public class MetricsBuilder extends IncrementalProjectBuilder {

	MetricStore store;
	private static Boolean headless;
	
	public MetricsBuilder() {
		store = MetricStore.get();
	}
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		if (project != null && project.isAccessible() && !hasErrors(project)) {
			checkCancel(monitor);
			IJavaProject currentProject = JavaCore.create(getProject());			
			if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			    fullBuild(currentProject, monitor);
			} else {
				// if we don't have a record of this project we've got to do a 
				// full build anyways, regardless of the fact that the user
				// requested an incremental build
				if (store.get(currentProject) == null) {
					fullBuild(currentProject, monitor);
				} else {
				    IResourceDelta delta = getDelta(getProject());
				    incrementalBuild(delta, monitor);
				}
			}
		}
		return new IProject[0];
	}
	
	/**
	 * @param delta
	 * @param monitor
	 * @throws CoreException 
	 */
	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		if (delta != null) {
			// this is a hack to avoid a build on enable only, not working
			if (!isHeadless() && monitor instanceof NullProgressMonitor) return;
			MetricsBuildVisitor v = new MetricsBuildVisitor(this, monitor);
		    delta.accept(v);
			checkCancel(monitor);
		    v.execute();
		}
	}

	/**
	 * @param currentProject
	 * @param monitor
	 * @throws CoreException 
	 */
	private void fullBuild(IJavaProject currentProject, IProgressMonitor monitor) throws CoreException {
  		MetricsBuildVisitor v = new MetricsBuildVisitor(this, monitor);
   		store.remove(currentProject);
	  	getProject().accept(v);
		checkCancel(monitor);
		v.execute();
	}

	/**
	 * Check whether the build has been canceled.
	 */
	public void checkCancel(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
	}	
	
	/**
	 * determine if project has compilation errors
	 * @param project
	 * @return true if project has compile errors
	 */
	private boolean hasErrors(IProject project) {
		try {
			IMarker[] markerList = project.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			if (markerList == null || markerList.length == 0)	return false;
			IMarker marker = null;
			int numErrors = 0;
			for (int i = 0; i < markerList.length; i++) {
				marker = markerList[i];
				int severity =	marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				if (severity == IMarker.SEVERITY_ERROR) {
					numErrors++;
				}
			}
			return numErrors>0;
		} catch (CoreException e) {
			Log.logError("CoreException getting build errors: " , e);
			return false;
		}
	}
	
	/**
	 * Answers true if the commandline that started eclipse contained "-noupdate"
	 * This is used to determine whether to calculate metrics in the background
	 * (normal operation in UI) or in the foreground (headless operation from Ant)
	 * @return true if running headless, false when in UI mode
	 */
	static boolean isHeadless() {
		if (headless == null) {
			headless = Boolean.FALSE;
			String[] args = Platform.getCommandLineArgs();
			for (int i = 0; i < args.length; i++) {
				if ("-noupdate".equals(args[i])) {
					headless = Boolean.TRUE;
					break;
				}
			} 
		}
		return headless.booleanValue();
	}		

}
