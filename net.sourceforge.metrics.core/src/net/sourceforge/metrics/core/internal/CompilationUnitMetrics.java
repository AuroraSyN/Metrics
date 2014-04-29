package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.Log;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class CompilationUnitMetrics extends MetricsElement {

	private static final long serialVersionUID = 3256437010565837878L;

	public CompilationUnitMetrics(IJavaElement javaElement) {
		super(javaElement);
	}

	public ICompilationUnit getCompilationUnit() {
		return (ICompilationUnit) getJavaElement();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricsElement#getChildren()
	 */
	public IMetricsElement[] getChildren() {
		try {
			ICompilationUnit c = getCompilationUnit();
			IJavaElement[] types = c.getTypes();
			return adaptAll(types, new IJavaElementFilter() {

				public boolean accept(IJavaElement element) {
					try {
						IType type = (IType)element;
						return !type.isAnnotation();
					} catch (JavaModelException e) {
						Log.logError("error getting type", e);
						return false;
					}
				}
				
			});
		} catch (JavaModelException e) {
			return EMPTY;
		}
	}
	
	
}
