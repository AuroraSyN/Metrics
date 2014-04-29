/*
 * $Log: IMetric.java,v $
 * Revision 1.1  2005/03/20 15:12:28  sauerf
 * ported some of the core classes from the old version into their new location
 * 
 */
package net.sourceforge.metrics.core;



/**
 * Public interface to the metrics
 * 
 * @author Frank Sauer
 */
public interface IMetric {

	/**
	 * Get the metric's name
	 * @return the name
	 */
	String getName();

	/**
	 * Get the scope (for averages and maxima, like per package or per type)
	 * @return
	 */
	String getPer();

	/**
	 * @see java.lang.Number#intValue()
	 */
	int intValue();

	/**
	 * @see java.lang.Number#longValue()
	 */
	long longValue();

	/**
	 * @see java.lang.Number#floatValue()
	 */
	float floatValue();

	/**
	 * @see java.lang.Number#doubleValue()
	 */
	double doubleValue();

	/**
	 * Returns the value.
	 * @return double
	 */
	double getValue();

	/**
	 * Only metrics calculated at the original level return false.
	 * All propagated metrics return true
	 * @return boolean
	 */
	boolean isPropagated();

}