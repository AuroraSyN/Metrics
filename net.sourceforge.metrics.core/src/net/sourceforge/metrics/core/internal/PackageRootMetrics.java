package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.Log;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class PackageRootMetrics extends MetricsElement {

	private static final long serialVersionUID = 3617014169644249655L;

	/**
	 * @param javaElement
	 */
	public PackageRootMetrics(IJavaElement javaElement) {
		super(javaElement);
	}

	public IPackageFragmentRoot getRoot() {
		return (IPackageFragmentRoot) getJavaElement();
	}
	
	/**
	 * @return metrics for all packages in my corresponding IPackageFragmentRoot that actually have java resources
	 */
	public IMetricsElement[] getChildren() {
		try {
			IPackageFragmentRoot root = getRoot();
			IJavaElement[] packages = root.getChildren();
			return adaptAll(packages, new IJavaElementFilter() {
				public boolean accept(IJavaElement element) {
					try {
						if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
							IPackageFragment pf = (IPackageFragment)element;
							return pf.containsJavaResources();
						} else return false;
					} catch (JavaModelException e) {
						Log.logError("error getting IPackageFragmentRoot children", e);
						return false;
					}
				}				
			});
		} catch (JavaModelException e) {
			return EMPTY;
		}
	}


}
