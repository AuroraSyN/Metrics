package net.sourceforge.metrics.tests;

import net.sourceforge.metrics.core.ICalculator;
import net.sourceforge.metrics.core.MetricsCorePlugin;
import net.sourceforge.metrics.core.extensions.ICalculatorDescriptor;
import net.sourceforge.metrics.core.extensions.ICategoryDescriptor;
import net.sourceforge.metrics.core.extensions.IMetricDescriptor;
import net.sourceforge.metrics.core.internal.extensions.MetricsExtensionReader;
import junit.framework.TestCase;

public class TestMetricsExtensionReader extends TestCase {

	public void testCategories() {
		ICategoryDescriptor[] categories = MetricsExtensionReader.getCategories();
		assertEquals("Should have two categories", 2, categories.length);
		ICategoryDescriptor basic = MetricsExtensionReader.getCategory("net.sourceforge.metrics.tests.category.basic");
		assertNotNull("Could not find net.sourceforge.metrics.tests.category.basic", basic);
		assertEquals("Bad id", "net.sourceforge.metrics.tests.category.basic", basic.getId());
		assertEquals("Bad name", "Basic Metrics for Unit Test", basic.getName());
		assertEquals("Bad description", "This is a category containing test metrics only, for use in the Unit Tests for the metrics plugin", basic.getDescription());
	}
	
	public void testCalculators() {
		ICalculatorDescriptor[] calculators = MetricsExtensionReader.getCalculators();
		assertEquals("Should have one calculator", 1, calculators.length);
		assertEquals("Bad id", "net.sourceforge.metrics.tests.testcalculator1", calculators[0].getId());
		assertEquals("Bad name", "Test Calculator", calculators[0].getName());
		assertEquals("Bad level", "method", calculators[0].getLevel());
		ICalculator calculator = calculators[0].getCalculator();
		assertNotNull("No Calculator!", calculator);
		assertEquals("Should be MockCalculator", MockCalculator.class.getName(), calculator.getClass().getName());
	}
	
	public void testNonExistingCategory() throws Exception {
		IMetricDescriptor[] metrics = MetricsCorePlugin.getMetricDescriptors("doesnotexist");
		assertNotNull("should have returned empty array", metrics);
		assertEquals("Should have returned empty array", 0, metrics.length);
	}
	
	public void testMetrics() {
		IMetricDescriptor[] metrics = MetricsCorePlugin.getMetricDescriptors();
		assertEquals("Should have 2 metrics", 2, metrics.length);
		IMetricDescriptor m = MetricsCorePlugin.getMetricDescriptor("net.sourceforge.metrics.tests.mloc");
		assertNotNull("Could not find declared mloc metric", m);
		assertFalse("PropagateSum should be false", m.isPropagateSum());
	}
	
	public void testRange() {
		IMetricDescriptor m = MetricsCorePlugin.getMetricDescriptor("net.sourceforge.metrics.tests.mloc");
		assertEquals("max should be 50.0", 50.0, m.getMax().doubleValue(), 0.0);
		assertEquals("preference should have been set", 50.0, MetricsCorePlugin.getDefault().getPluginPreferences().getDouble("METRICS.RANGE.net.sourceforge.metrics.tests.mloc.MAX"),0.0);
	}
}
