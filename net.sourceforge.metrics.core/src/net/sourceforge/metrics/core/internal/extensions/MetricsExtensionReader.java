package net.sourceforge.metrics.core.internal.extensions;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.metrics.core.Log;
import net.sourceforge.metrics.core.extensions.ICalculatorDescriptor;
import net.sourceforge.metrics.core.extensions.ICategoryDescriptor;
import net.sourceforge.metrics.core.extensions.IMetricDescriptor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;

/**
 * Discover and parse all the contributions to the net.sourceforge.metrics.core.calculators
 * extension point
 * 
 * @author Frank Sauer
 */
public class MetricsExtensionReader {

	private static final String XPOINT = "net.sourceforge.metrics.core.calculators";
	
	private static final String TAG_CALCULATOR = "calculator";
	private static final String TAG_METRIC = "metric";
	private static final String TAG_CATEGORY = "category";
	
	private static HashMap categories = null;
	private static HashMap calculators = null;

	private static HashMap metrics;
	
	/**
	 * Unitlity class, no instances. Hide constructor.
	 */
	private MetricsExtensionReader() { }
	
	/**
	 * Discover and parse all the contributions to the net.sourceforge.metrics.core.calculators
	 * extension point
	 */
	public static void readExtensions() {
		IExtensionPoint p = Platform.getExtensionRegistry().getExtensionPoint(XPOINT);
		IExtension[] extensions = p.getExtensions();
		for (int x = 0; x < extensions.length; x++) {
			IConfigurationElement[] elements = extensions[x].getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				IConfigurationElement next = elements[i];
				if (TAG_CATEGORY.equals(next.getName())) {
					readCategory(next);
				} else if (TAG_CALCULATOR.equals(next.getName())) {
					readCalculator(next);
				} else {
					Log.logMessage("unknown extension element <" + next.getName() + "> in " + next.getNamespace());
				}
			}
		}
		
	}

	/**
	 * Get all declared metrics categories in arbitrary order
	 * @return array of ICategoryDescriptor
	 */
	public static ICategoryDescriptor[] getCategories() {
		init();
		return (ICategoryDescriptor[]) categories.values().toArray(new CategoryDescriptor[]{});
	}
	
	public static ICategoryDescriptor getCategory(String id) {
		init();
		return (ICategoryDescriptor) categories.get(id);
	}
	
	public static ICalculatorDescriptor[] getCalculators() {
		init();
		return (ICalculatorDescriptor[]) calculators.values().toArray(new CalculatorDescriptor[]{});
	}
	
	public static ICalculatorDescriptor getCalculator(String id) {
		init();
		return (ICalculatorDescriptor) calculators.get(id);
	}
	
	/**
	 * get all applicable calculators for the given IJavaElement
	 * @param element an IJavaElement
	 * @return array of all calculator descriptors
	 */
	public static ICalculatorDescriptor[] getCalculators(IJavaElement element) {
		init();
		int lvl = element.getElementType();
		ArrayList result = new ArrayList();
		ICalculatorDescriptor[] all = getCalculators();
		for (int i = 0; i < all.length; i++) {
			if (all[i].getElementType() == lvl) {
				result.add(all[i]);
			}
		}
		return (ICalculatorDescriptor[]) result.toArray(new ICalculatorDescriptor[result.size()]);
	}
	
	/**
	 * Get all defined metrics for the given IJavaElement
	 * @param element
	 * @return
	 */
	public static IMetricDescriptor[] getMetricDescriptors(IJavaElement element) {
		init();
		ArrayList result = new ArrayList();
		ICalculatorDescriptor[] calcs = getCalculators(element);
		for (int c = 0; c < calcs.length; c++) {
			IMetricDescriptor[] metrics = calcs[c].getMetrics();
			for (int m = 0; m < metrics.length; m++) {
				result.add(metrics[m]);
			}
		}
		return (IMetricDescriptor[]) result.toArray(new IMetricDescriptor[]{});
	}
	
	/**
	 * Get all declared metrics in arbitrary order
	 * @return array of ICategoryDescriptor
	 */
	public static IMetricDescriptor[] getMetricDescriptors() {
		init();
		return (IMetricDescriptor[]) metrics.values().toArray(new MetricDescriptor[]{});
	}
	
	public static IMetricDescriptor getMetricDescriptor(String id) {
		init();
		return (IMetricDescriptor) metrics.get(id);
	}
	
	private static void init() {
		if (categories == null) {
			categories = new HashMap();
			calculators = new HashMap();
			metrics = new HashMap();
			readExtensions();
		}
	}
	
	/**
	 * @param next a &lt;category&gt; element
	 */
	private static void readCategory(IConfigurationElement next) {
		try {
			CategoryDescriptor c = new CategoryDescriptor(next);
			categories.put(c.getId(), c);
		} catch (IllegalArgumentException e) {
			Log.logError("skipping bad category", e);
		}
		
	}

	/**
	 * @param next a &lt;calculator&gt; element
	 */
	private static void readCalculator(IConfigurationElement next) {
		try {
			CalculatorDescriptor c = new CalculatorDescriptor(next);
			calculators.put(c.getId(), c);
			IConfigurationElement[] metrics = next.getChildren(TAG_METRIC);
			for (int i = 0; i < metrics.length; i++) {
				readMetric(c, metrics[i]);
			}
		} catch (IllegalArgumentException e) {
			Log.logError("skipping bad category", e);
		}
	}

	/**
	 * @param c
	 * @param element
	 */
	private static void readMetric(CalculatorDescriptor c, IConfigurationElement element) {
		try {
			IMetricDescriptor metric = new MetricDescriptor(element);
			c.addMetric(metric);
			metrics.put(metric.getId(), metric);
		} catch (IllegalArgumentException e) {
			Log.logError("skipping bad metric", e);
		}
	}
}
