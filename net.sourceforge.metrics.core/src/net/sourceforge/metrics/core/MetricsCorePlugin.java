package net.sourceforge.metrics.core;

import net.sourceforge.metrics.core.extensions.IMetricDescriptor;
import net.sourceforge.metrics.core.extensions.ICategoryDescriptor;
import net.sourceforge.metrics.core.internal.extensions.MetricsExtensionReader;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.core.IJavaElement;

import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class MetricsCorePlugin extends Plugin {
	
	public static final String ID = "net.sourceforge.metrics.core";
	
	//The shared instance.
	private static MetricsCorePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public MetricsCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MetricsCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MetricsCorePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("net.sourceforge.metrics.core.MetricsCorePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Find and return all metrics contributed to the net.sourceforge.metrics.core.calculators
	 * extension point
	 * @return array of all contributed metrics descriptors
	 */
	public static IMetricDescriptor[] getMetricDescriptors() {
		return MetricsExtensionReader.getMetricDescriptors();
	}
	
	public static IMetricDescriptor[] getMetricDescriptors(IJavaElement element) {
		return MetricsExtensionReader.getMetricDescriptors(element);
	}
	
	public static IMetricDescriptor getMetricDescriptor(String id) {
		return MetricsExtensionReader.getMetricDescriptor(id);	
	}
	
	/**
	 * 
	 * @param categoryId id of a metrics category
	 * @return all metrics declared for the given category
	 */
	public static IMetricDescriptor[] getMetricDescriptors(String categoryId) {
		ICategoryDescriptor cat = MetricsExtensionReader.getCategory(categoryId);
		return (cat!=null)?cat.getMetricDescriptors():(new IMetricDescriptor[0]);
	}
	
	public static ICategoryDescriptor[] getMetricsCategories() {
		return MetricsExtensionReader.getCategories();
	}
	
	public static ICategoryDescriptor getMetricsCategory(String id) {
		return MetricsExtensionReader.getCategory(id);
	}
}
