package net.sourceforge.metrics.core.extensions;

/**
 * This interface represents category contributions contributed to the
 * net.sourceforge.metrics.core.calculators extension-point
 * 
 * @author Frank Sauer
 */
public interface ICategoryDescriptor {

	/**
	 * @return id of this category
	 */
	String getId();
	
	/**
	 * @return name of the category
	 */
	String getName();
	
	/**
	 * @return a somewhat longer description of this category
	 */
	String getDescription();
	
	/**
	 * Get all metrics defined in thsi category
	 * @return array of MetricsDescriptor with all metrics in this category
	 */
	IMetricDescriptor[] getMetricDescriptors();
	
	/**
	 * Get the MetricDescriptor for the metric with the given id
	 * @param id metric id
	 * @return MetricDescriptor or null
	 */
	IMetricDescriptor getMetricDescriptor(String id);
	
}
