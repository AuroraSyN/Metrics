package net.sourceforge.metrics.core.extensions;

/**
 * This interface represents metric contributions contributed to the
 * net.sourceforge.metrics.core.calculators extension-point
 * 
 * @author Frank Sauer
 */
public interface IMetricDescriptor {

	/**
	 * @return id of this metric
	 */
	String getId();
	
	/**
	 * @return name of the metric
	 */
	String getName();
	
	/**
	 * @return a somewhat longer description of this metric
	 */
	String getDescription();
	
	public abstract String[] getRequiredMetricIds();

	public abstract boolean isAllowDisable();

	public abstract boolean isPropagateAvg();

	public abstract boolean isPropagateMax();

	public abstract boolean isPropagateSum();

	/**
	 * @return Double
	 */
	public abstract Double getMax();

	/**
	 * @return Double
	 */
	public abstract Double getMin();

	/**
	 * Check if the given value is within the safe range (boundaries inclusive) of this metric
	 * @param value
	 * @return true if value is within safe boundaries (inclusive)
	 */
	public abstract boolean isValueInRange(double value);

	public abstract void resetToDefaults();

}
