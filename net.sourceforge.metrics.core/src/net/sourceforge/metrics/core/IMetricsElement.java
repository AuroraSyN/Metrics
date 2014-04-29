package net.sourceforge.metrics.core;

import java.util.Map;

import net.sourceforge.metrics.core.internal.math.Metric;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;

/**
 * Interface used to access the metrics. 
 * Implementations of this interface are internal. Instances of these
 * internal classes are obtained using the platforms adapter manager,
 * as follows:
 * <PRE>
 * (IMetricsElement) Platform.getAdapterManager().getAdapter(anIJavaElement, IMetricsElement.class)
 * </PRE>
 * @author Frank Sauer
 */
public interface IMetricsElement extends IAdaptable {

	/**
	 * @return corresponding IJavaElement
	 */
	IJavaElement getJavaElement();
	
	/**
	 * Find my parent IMetricsElement
	 * @return parent metrics element or null if this is a project
	 */
	IMetricsElement getParent();
	
	/**
	 * Find all the direct child metrics. returns empty array if none found
	 * @return array of IMetricsElements
	 */
	IMetricsElement[] getChildren();

	/**
	 * @param id
	 * @return metric with the given id or null if it does not exist
	 */
	Metric getMetric(String id);
	
	Map getMetrics();
	
	Map getAverages();
	
	Map getMaxima();
}
