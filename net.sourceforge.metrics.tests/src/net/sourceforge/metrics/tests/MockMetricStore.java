package net.sourceforge.metrics.tests;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.IJavaElement;

import net.sourceforge.metrics.core.IMetricStore;
import net.sourceforge.metrics.core.IMetricsElement;

public class MockMetricStore implements IMetricStore {

	public HashMap db = new HashMap();
	
	public static ArrayList instances = new ArrayList();
	
	public MockMetricStore() {
		instances.add(this);
	}
	
	public IMetricsElement get(IJavaElement element) {
		return (IMetricsElement) db.get(element.getHandleIdentifier());
	}

	public void put(IMetricsElement element) {
		db.put(element.getJavaElement().getHandleIdentifier(), element);
	}

	public void remove(IJavaElement element) {
		db.remove(element.getHandleIdentifier());
	}

	public void clear() {
		db.clear();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#close()
	 */
	public void close() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#beginTransaction()
	 */
	public void beginTransaction() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#commitTransaction()
	 */
	public void commitTransaction() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#abortTransaction()
	 */
	public void abortTransaction() {
		// TODO Auto-generated method stub
		
	}

}
