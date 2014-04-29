package net.sourceforge.metrics.core.internal.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import net.sourceforge.metrics.core.extensions.IMetricDescriptor;
import net.sourceforge.metrics.core.extensions.ICategoryDescriptor;

/**
 * This class represents the &ltcategory&gt of the extension-point contributions
 * 
 * @author Frank Sauer
 */
public class CategoryDescriptor extends AbstractDescriptor implements ICategoryDescriptor {

	private List descriptors = new ArrayList();
	private Map index = new HashMap();
	
	/**
	 * Only the MetricsExtensionReader creates these
	 * @param element configuration element representing the &lt;category&gt;
	 */
	CategoryDescriptor(IConfigurationElement element) {
		super(element);
	}
		
	/**
	 * @return all metric descriptors within this category
	 */
	public IMetricDescriptor[] getMetricDescriptors() {
		return (IMetricDescriptor[]) descriptors.toArray(new IMetricDescriptor[]{});
	}

	public IMetricDescriptor getMetricDescriptor(String id) {
		return (IMetricDescriptor) index.get(id);
	}

	void addMetric(IMetricDescriptor metric) {
		index.put(metric.getId(), metric);
	}
}
