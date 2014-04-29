package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.Log;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Creates IMetricsElement objects given an IJavaElement.
 * This factory is registered with the org.eclipse.core.runtime.adapters
 * extension point in our plugin.xml
 * 
 * @author Frank Sauer
 */
public class MetricsAdapterFactory implements IAdapterFactory {

	/**
	 * If adaptableObject is an IJavaElement, create the corresponding IMetricsElement
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IMetricsElement.class.equals(adapterType) &&
				IJavaElement.class.isInstance(adaptableObject)) {
			try {
				return createMetricsElement((IJavaElement)adaptableObject);
			} catch (JavaModelException e) {
				Log.logError("Error creating metrics adapter", e);
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Create the appropriate metrics element for the type of java element
	 * @param element the java element
	 * @return an IMetricsElement implementation
	 * @throws JavaModelException 
	 */
	private IMetricsElement createMetricsElement(IJavaElement element) throws JavaModelException {
		switch(element.getElementType()) {
		case IJavaElement.JAVA_PROJECT:
				return new ProjectMetrics(element);
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				return new PackageRootMetrics(element);
		case IJavaElement.PACKAGE_FRAGMENT:
				return new PackageMetrics(element);
		case IJavaElement.COMPILATION_UNIT:
				return new CompilationUnitMetrics(element);
		case IJavaElement.TYPE:
				return new TypeMetrics(element);
		case IJavaElement.METHOD:
				return new MethodMetrics(element);
		default:
				return null;
		}
	}

	/**
	 * @return Class[] {IMetricsElement.class}
	 */
	public Class[] getAdapterList() {
		return new Class[] {IMetricsElement.class};
	}

}
