package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

public class PackageMetrics extends MetricsElement  {

	private static final long serialVersionUID = 3906650782479888951L;

	/**
	 * @param javaElement
	 */
	protected PackageMetrics(IJavaElement javaElement) {
		super(javaElement);
	}

	/**
	 * @return my corresponding IPackageFragment
	 */
	public IPackageFragment getPackageFragment() {
		return (IPackageFragment) getJavaElement();
	}
	
	/**
	 * @return metrics for all compilation iunits in my package
	 */
	public IMetricsElement[] getChildren() {
		try {
			IPackageFragment pf = getPackageFragment();
			IJavaElement[] units = pf.getCompilationUnits();
			return adaptAll(units, null);
		} catch (JavaModelException e) {
			return EMPTY;
		}
	}


	
}
