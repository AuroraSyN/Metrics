package net.sourceforge.metrics.tests;

import java.util.ArrayList;

import net.sourceforge.metrics.core.internal.math.Max;
import net.sourceforge.metrics.core.internal.math.Metric;

import junit.framework.TestCase;

public class TestMax extends TestCase {
	private static String name1 = "NAME1";

	private static String name2 = "NAME2";

	private ArrayList metrics;

	private ArrayList maxes;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		metrics = new ArrayList();
		metrics.add(new Metric(name1, 1));
		metrics.add(new Metric(name1, 3));
		metrics.add(new Metric(name1, 2));
		metrics.add(new Metric(name1, 5));
		maxes = new ArrayList();
		maxes.add(new Max(name1, "per", 1));
		maxes.add(new Max(name1, "per", 4));
		maxes.add(new Max(name1, "per", 10));
		maxes.add(new Max(name1, "per", 6));

	}

	public void testMaxFromMetrics() {
		Max max = Max.createFromMetrics(name1, "per", metrics);
		assertEquals("wrong expected max value", 5, max.intValue());
	}

	public void testMaxFromMaxes() {
		Max max = Max.createFromMaxes(maxes);
		assertEquals("wrong expected max value", maxes.get(2), max);
	}
}
