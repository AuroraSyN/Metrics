package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.Log;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * IMetricsElement implementation for java projects
 * 
 * @author Frank Sauer
 */
public class ProjectMetrics extends MetricsElement {

	private static final long serialVersionUID = 3257854298233911352L;

	/**
	 * @param javaElement
	 */
	public ProjectMetrics(IJavaElement javaElement) {
		super(javaElement);
	}
	
	/**
	 * @return corresponding IJavaProject
	 */
	public IJavaProject getProject() {
		return (IJavaProject) getJavaElement();
	}

	/**
	 * get metrics for all PackageFragmentRoot elements with kind = K_SOURCE
	 * @see net.sourceforge.metrics.core.IMetricsElement#getChildren()
	 */
	public IMetricsElement[] getChildren() {
		try {
			IJavaProject project = getProject();
			IJavaElement[] roots = project.getPackageFragmentRoots();
			return adaptAll(roots, new IJavaElementFilter() {
				public boolean accept(IJavaElement element) {
					try {
						IPackageFragmentRoot r = (IPackageFragmentRoot)element;
						return r.getKind() == IPackageFragmentRoot.K_SOURCE;
					} catch (JavaModelException e) {
						Log.logError("Error getting pfr kind", e);
						return false;
					}
				}				
			});
		} catch (JavaModelException e) {
			return EMPTY;
		}
	}

	
}
