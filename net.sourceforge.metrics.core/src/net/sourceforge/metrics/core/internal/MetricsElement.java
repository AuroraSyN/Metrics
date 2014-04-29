package net.sourceforge.metrics.core.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.internal.math.Metric;

public abstract class MetricsElement implements IMetricsElement, Serializable {

	public static final IMetricsElement[] EMPTY = new IMetricsElement[]{};

	/**
	 * Any database using serialization must ensure that this gets restored after reading
	 * a metrics element from persistent storage
	 */
	transient private IJavaElement javaElement;
	
	private Map values = new HashMap();
	private Map averages = new HashMap();
	private Map maxima = new HashMap();
	
	protected MetricsElement(IJavaElement javaElement) {
		this.javaElement = javaElement;
	}
	
	/**
	 * This method is needed to set the java element after a read from 
	 * persistent storage. Not for public use.
	 * @param javaElement
	 */
	public void setJavaElement(IJavaElement javaElement) {
		this.javaElement = javaElement;
	}
	
	/**
	 * allow for future extensions
	 * @return any applicable adapters or null if none exist
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == null) return null;
		if (adapter == this.getClass()) return this;
		if (adapter == IJavaElement.class) return javaElement;
		// no direct way, ask Platform for any registered adapters
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	
	public IJavaElement getJavaElement() {
		return javaElement;
	}

	/**
	 * @return my parent IMetricsElement (using the adapter factory on the parent IJavaElement)
	 */
	public IMetricsElement getParent() {
		IJavaElement parent = javaElement.getParent();
		if (parent != null) {
			return adapt(parent);
		}
		return null;
	}

	/**
	 * Use the Adapter Manager to adapt the given element to an IMetricsElement
	 * @param element a IjavaElement
	 * @return the corresponding IMetricsElement
	 */
	protected IMetricsElement adapt(IJavaElement element) {
		return (IMetricsElement) Platform.getAdapterManager()
			.getAdapter(element, IMetricsElement.class);
	}

	/**
	 * Get IMetricsElement adapters for all java elements.
	 * If filter is not null, it has to return true for the elements to be considered.
	 * This is used to filter out binary packages, annotation types, etc., everything to
	 * which metrics don't apply
	 * 
	 * @param elements array of java elements
	 * @param filter optional filter to apply to the elements
	 * @return array of metrics elements
	 */
	protected IMetricsElement[] adaptAll(IJavaElement[] elements, IJavaElementFilter filter) {
		if (elements == null) return null;
		ArrayList result = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			if (filter == null || filter.accept(elements[i])) {
				result.add(adapt(elements[i]));
			}
		}
		return (IMetricsElement[]) result.toArray(EMPTY);
	}
	
	/**
	 * Two metrics elements are equal when their java elements are
	 */
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (o instanceof IMetricsElement) {
			IJavaElement other = ((IMetricsElement)o).getJavaElement();
			return javaElement.equals(other);
		} else return false;
	}
	
	/**
	 * my hashCode is my java element's hashCode
	 */
	public int hashCode() {
		return javaElement.hashCode();
	}

	public Metric getMetric(String id) {
		return (Metric)values.get(id);
	}

	public Map getAverages() {
		return averages;
	}

	public Map getMaxima() {
		return maxima;
	}

	public Map getMetrics() {
		return values;
	}
	
	
}
