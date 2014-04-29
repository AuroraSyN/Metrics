package net.sourceforge.metrics.core;

import org.eclipse.jdt.core.IJavaElement;

/**
 * Public interface to metrics databases providing the persistent storage for calculated metrics
 * 
 * @author Frank Sauer
 */
public interface IMetricStore {

	/**
	 * transactional databases can use this to start a transaction if so desired
	 */
	public void beginTransaction();
	
	/**
	 * transactional databases can use this to commit the current transaction
	 */
	public void commitTransaction();
	
	/**
	 * transactional databases can use this to abort the current transaction
	 */
	public void abortTransaction();
	
	/**
	 * Get the metrics for the given java element
	 * @param element	element to retrieve metrics for
	 * @return ImetricsElement or null if not found
	 */
	public IMetricsElement get(IJavaElement element);
	
	/**
	 * Store the given metrics in the persistent store
	 * @param element
	 */
	public void put(IMetricsElement element);
	
	/**
	 * Remove all the metrics for the given element <em>and all its children</em>
	 * @param element java element to remove from the database
	 */
	public void remove(IJavaElement element);
	
	/**
	 * clear the entire metrics database
	 */
	public void clear();
	
	/**
	 * Close the database (connection), commit all pending transactions, etc.
	 */
	public void close();
}
