package net.sourceforge.metrics.tests;

import java.util.ArrayList;

import net.sourceforge.metrics.core.internal.math.Avg;
import net.sourceforge.metrics.core.internal.math.Metric;

import junit.framework.TestCase;

public class TestAvg extends TestCase {

	private static String name1 = "NAME1";

	private static String name2 = "NAME2";

	private ArrayList metrics1;

	private ArrayList metrics2;

	protected void setUp() throws Exception {
		metrics1 = new ArrayList();
		metrics2 = new ArrayList();
		metrics1.add(new Metric(name1, 1));
		metrics1.add(new Metric(name1, 3));
		metrics1.add(new Metric(name1, 2));
		metrics1.add(new Metric(name1, 6));
		metrics2.add(new Metric(name1, 2));
		metrics2.add(new Metric(name1, 4));
		metrics2.add(new Metric(name1, 6));
		metrics2.add(new Metric(name1, 4));
	}

	public void testAvgFromMetrics() {
		Avg avg1 = Avg.createFromMetrics(name1, "per", metrics1);
		assertEquals("Wrong number of points", 4, avg1.getPoints());
		assertEquals("Wrong average", 3, avg1.intValue());
		assertEquals("Wrong variance", 3.5, avg1.getVariance(), 0);
		Avg avg2 = Avg.createFromMetrics(name1, "per", metrics2);
		assertEquals("Wrong number of points", 4, avg2.getPoints());
		assertEquals("Wrong average", 4, avg2.intValue());
		assertEquals("Wrong variance", 2.0, avg2.getVariance(), 0);
	}

	public void testAvgFromAverages() {
		Avg avg1 = Avg.createFromMetrics(name1, "per", metrics1);
		Avg avg2 = Avg.createFromMetrics(name1, "per", metrics2);
		ArrayList averages = new ArrayList();
		averages.add(avg1);
		averages.add(avg2);
		Avg result = Avg.createFromAverages(name1, "per", averages);
		assertEquals("Wrong number of points", 8, result.getPoints());
		assertEquals("Wrong average", 3.5, result.doubleValue(), 0);
		assertEquals("Wrong variance", 3.0, result.getVariance(), 0);
	}
}
