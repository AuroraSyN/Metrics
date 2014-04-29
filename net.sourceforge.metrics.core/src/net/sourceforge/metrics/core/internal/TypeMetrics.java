package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class TypeMetrics extends MetricsElement {

	private static final long serialVersionUID = 3763101847119738425L;

	public TypeMetrics(IJavaElement javaElement) {
		super(javaElement);
	}

	public IType getType() {
		return (IType) getJavaElement();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricsElement#getChildren()
	 */
	public IMetricsElement[] getChildren() {
		try {
			IType t = getType();
			IMethod[] methods = t.getMethods();
			return adaptAll(methods, null);
		} catch (JavaModelException e) {
			return EMPTY;
		}
	}
	
	
}
