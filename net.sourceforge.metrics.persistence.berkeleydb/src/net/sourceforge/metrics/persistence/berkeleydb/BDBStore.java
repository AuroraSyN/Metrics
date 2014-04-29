package net.sourceforge.metrics.persistence.berkeleydb;

import net.sourceforge.metrics.core.IMetricStore;
import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.Log;
import net.sourceforge.metrics.core.internal.MetricsElement;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;

import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * This IMetricStore fronts a BerkeleyDB (JE) store.
 * TODO use tuple bindings instead of SerialBinding (optimization)
 * 
 * @author Frank Sauer
 */
public class BDBStore implements IMetricStore {

	private static final String METRICSDB = "metricsdb";
	private static final String CLASS_CATALOG = "classdb";
	private static final String UTF8 = "UTF-8";
	
	private static BDBStore instance = null; 
	private static Environment dbEnv;
	private static Database db;
	private static StoredClassCatalog javaCatalog;
	private Transaction txn;
	
	/**
	 * Create/Initialize the database environment if it does not exist yet
	 */
	public BDBStore() {
		if (instance == null) {
			instance = this;
			initDBEnv();
		}
	}

	public static BDBStore getDefault() {
		return instance;
	}
	
	private synchronized void initDBEnv() {
		if (db == null) {
			IPath path = BerkeleydbPlugin.getDefault().getStateLocation();
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			try {
				dbEnv = new Environment(path.toFile(), envConfig);
				DatabaseConfig dbConfig = new DatabaseConfig();
				dbConfig.setAllowCreate(true);
				db = dbEnv.openDatabase(null, METRICSDB, dbConfig);
				Database catalog = dbEnv.openDatabase(null, CLASS_CATALOG, dbConfig);
				javaCatalog = new StoredClassCatalog(catalog);
			} catch (DatabaseException e) {
				throw new IllegalStateException("Could not create BerkeleyDB database.");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#get(org.eclipse.jdt.core.IJavaElement)
	 */
	public IMetricsElement get(final IJavaElement element) {
		initDBEnv();
		IMetricsElement result = null;
		if (db != null) {
			DatabaseEntry key = new DatabaseEntry(element.getHandleIdentifier().getBytes());
			DatabaseEntry value = new DatabaseEntry();
			try {
				OperationStatus status = db.get(txn,key, value, LockMode.DIRTY_READ);	
				if (status.equals(OperationStatus.SUCCESS)) {
					SerialBinding binding = new SerialBinding(javaCatalog, IMetricsElement.class) {

						public Object entryToObject(DatabaseEntry arg0) {
							// restore the transient javaElement
							MetricsElement result =  (MetricsElement)super.entryToObject(arg0);
							result.setJavaElement(element);
							return result;
						}
						
					};
					return (IMetricsElement)binding.entryToObject(value);
				} else {
					return null;
				}
			} catch (DatabaseException e) {
				Log.logError("Error retrieving metrics for " + element.getHandleIdentifier(), e);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#put(net.sourceforge.metrics.core.IMetricsElement)
	 */
	public void put(IMetricsElement element) {
		initDBEnv();
		if (db != null) {
			IJavaElement jElm = element.getJavaElement();
			try {
				DatabaseEntry key = new DatabaseEntry(jElm.getHandleIdentifier().getBytes(UTF8));
				DatabaseEntry value = new DatabaseEntry();
				SerialBinding binding = new SerialBinding(javaCatalog, IMetricsElement.class);
				binding.objectToEntry(element, value);
				db.put(txn,key, value);
			} catch (Exception e) {
				Log.logError("Could not store " + jElm.getHandleIdentifier(), e);
			}
		}		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#remove(org.eclipse.jdt.core.IJavaElement)
	 */
	public void remove(IJavaElement element) {
		initDBEnv();
		if (db != null) {
			Cursor cursor = null;
			try {
				DatabaseEntry key = new DatabaseEntry(element.getHandleIdentifier().getBytes(UTF8));
				DatabaseEntry data = new DatabaseEntry();
				cursor = db.openCursor(txn, null);				
				// find and delete all records where the key starts with the id of the given
				// IJavaElement
				OperationStatus status = cursor.getSearchKeyRange(key, data, LockMode.DEFAULT);
				while (status == OperationStatus.SUCCESS) {
					cursor.delete();
					status = cursor.getNext(key, data, LockMode.DEFAULT);
				}
			} catch (Exception e) {e.printStackTrace();
				Log.logError("Could not remove " + element.getHandleIdentifier(), e);
			} finally {
				if (cursor != null)
					try {
						cursor.close();
					} catch (DatabaseException e) {
						Log.logError("Could not close cursor", e);
					}
			}
		}		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#clear()
	 */
	public void clear() {
		initDBEnv();
		if (db != null) {
			try {
				db.close();
				javaCatalog.close();
				dbEnv.removeDatabase(null, METRICSDB);
				dbEnv.removeDatabase(null, CLASS_CATALOG);
				dbEnv.close();				
			} catch (DatabaseException e) {
				Log.logError("Could not remove databases", e);
			} finally {
				dbEnv = null;
				db = null;
				javaCatalog = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#close()
	 */
	public void close() {
		if (db != null) {
			try {
				db.close();
				javaCatalog.close();
				dbEnv.close();
			} catch (DatabaseException e) {
				Log.logError("Error closing berkeleyDB",e);
			} finally {
				dbEnv = null;
				db = null;
				javaCatalog = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#beginTransaction()
	 */
	public void beginTransaction() {
		initDBEnv();
		if (db != null) {
			if (txn == null) {
				try {					
					txn = dbEnv.beginTransaction(null, null);
				} catch (DatabaseException e) {
					Log.logError("Could not start transaction", e);
					throw new IllegalStateException("Could not start transaction");
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#commitTransaction()
	 */
	public void commitTransaction() {
		if (txn != null) {
			try {
				txn.commit();
			} catch (DatabaseException e) {
				Log.logError("Could not commit transaction", e);
				throw new IllegalStateException("Could not commit transaction");
			} finally {
				txn = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.IMetricStore#abortTransaction()
	 */
	public void abortTransaction() {
		if (txn != null) {
			try {
				txn.abort();
			} catch (DatabaseException e) {
				Log.logError("Could not commit transaction", e);
				throw new IllegalStateException("Could not commit transaction");
			} finally {
				txn = null;
			}
		}
	}
}
