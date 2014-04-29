package net.sourceforge.metrics.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.metrics.core.IMetricStore;
import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.Log;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;

/**
 * This class discovers all contributed databases by other plug-ins and delegates
 * store requests to them. Only one primary database will be used to retrieve data from.
 * 
 * @author Frank Sauer
 */
public class MetricStore implements IMetricStore {
	
	private static final String XPOINT = "net.sourceforge.metrics.core.database";
	private static MetricStore singleton = null;
	
	/**
	 * Get the metrics store front singleton
	 * @return
	 */
	public static MetricStore get() {
		if (singleton == null) {
			singleton = new MetricStore();
		}
		return singleton;
	}
	
	private List stores = null;
	private MetricStoreDescriptor primary = null;
	
	/**
	 * Discover all stores from the extension registry and initialize the primary
	 * store and secondary store list
	 */
	private MetricStore () {
		IExtensionPoint p = Platform.getExtensionRegistry().getExtensionPoint(XPOINT);
		IExtension[] extensions = p.getExtensions();
		for (int x = 0; x < extensions.length; x++) {
			IConfigurationElement[] elements = extensions[x].getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				IConfigurationElement next = elements[i];
				if (next.getName().equals("store")) {
					try {
						MetricStoreDescriptor db = new MetricStoreDescriptor();
						db.name = next.getAttribute("name");
						db.db = (IMetricStore) next.createExecutableExtension("class");
						// remember the first primary as the primary db
						if (primary == null) {
							String strPrimary = next.getAttribute("primary");
							if (strPrimary != null && !strPrimary.equals("false")) {
								primary = db;
							}
						}
						if (stores == null) {
							stores = new ArrayList();
						}
						stores.add(db);
					} catch (CoreException e) {
						Log.logError("Invalid database extension", e);
					}
				}
			}
		}
	}
	
	/**
	 * Get the IMetricsElement from the primary store or null if none exist
	 * @return stored IMetricsElement or null
	 */
	public IMetricsElement get(IJavaElement element) {
		if (primary != null) {
			return primary.db.get(element);
		} else {
			return null;
		}
	}

	public void put(final IMetricsElement element) {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.put(element);
			}			
		});
	}

	public void remove(final IJavaElement element) {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.remove(element);
			}			
		});
	}

	public void clear() {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.clear();
			}			
		});
	}

	public void close() {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.close();
			}			
		});
	}
	
	public void beginTransaction() {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.beginTransaction();
			}			
		});
	}

	public void commitTransaction() {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.commitTransaction();
			}			
		});
	}

	public void abortTransaction() {
		forEachDB(new RunnableWithDB() {

			public void runWith(IMetricStore db) {
				db.abortTransaction();
			}			
		});
	}

	/**
	 * Invoke the given runnable on all contributed databases
	 * @param runnable operation to invoke
	 */
	private void forEachDB(RunnableWithDB runnable) {
		if (stores != null) {
			for (Iterator i = stores.iterator(); i.hasNext();) {
				MetricStoreDescriptor next = (MetricStoreDescriptor)i.next();
				try { // let exceptions with one not interfere with the others
					runnable.runWith(next.db);
				} catch (Throwable t) {
					Log.logError("Error updating metric store: " + next.name, t);
				}
			}
		}
	}
	
	/**
	 * Simple interface to pass a IMetricStore to a runnable. Used to invoke the
	 * same operation on all registered metrics stores.
	 * 
	 * @author Frank Sauer
	 */
	interface RunnableWithDB {
		void runWith(IMetricStore db);
	}
	
	/**
	 * Simple encapsulation of a store's metadata, given to a RunnableWithDB
	 * 
	 * @author Frank Sauer
	 */
	class MetricStoreDescriptor {
		String name;
		IMetricStore db;
	}
}
