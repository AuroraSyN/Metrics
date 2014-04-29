package net.sourceforge.metrics.tests;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;

import net.sourceforge.metrics.core.IMetricStore;
import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.internal.MetricStore;
import net.sourceforge.metrics.persistence.berkeleydb.BDBStore;
import net.sourceforge.metrics.persistence.berkeleydb.BerkeleydbPlugin;


public class TestMetricStore extends MetricsTestCase {

	public void testGetPut() {
		IMetricStore store = MetricStore.get();
		assertEquals("There should be two stores created", 2, MockMetricStore.instances.size());
		IMetricsElement element = getMetricsElement(PATH_PROJECT);
		store.put(element);
		IJavaElement key = element.getJavaElement();
		IMetricsElement value = store.get(key);
		assertNotNull("stored value not found", value);
		// test both mock stores got it
		MockMetricStore store1 = (MockMetricStore) MockMetricStore.instances.get(0);
		MockMetricStore store2 = (MockMetricStore) MockMetricStore.instances.get(1);
		assertNotNull("not found in store 1", store1.get(key));
		assertNotNull("not found in store 2", store2.get(key));
	}
	
	public void testJEFilesExist() {
		IPath path = BerkeleydbPlugin.getDefault().getStateLocation();
		File dir = path.toFile();
		assertTrue("State directory " + dir + " was not created.", dir.exists());
		File[] files = dir.listFiles();
		int count = 0;
		for (int i = 0; i < files.length;i++) {
			if (files[i].getName().endsWith("jdb")) count++;
		}
		assertTrue("No database files created.", count > 0);
	}
	
	public void testJEDataExists() {
		// the above tests should have stored this on disk
		IMetricsElement element = getMetricsElement(PATH_PROJECT);
		IJavaElement jElm = element.getJavaElement();
		BDBStore store = new BDBStore();
		IMetricsElement found = store.get(jElm);
		assertNotNull("element not found in JE", found);
		assertEquals("retrieved element should be equal to stored one", element, found);
	}
		
	public void testRemove() {
		IMetricStore store = MetricStore.get();
		IMetricsElement element = getMetricsElement(PATH_PROJECT);
		store.put(element);
		MockMetricStore store1 = (MockMetricStore) MockMetricStore.instances.get(0);
		MockMetricStore store2 = (MockMetricStore) MockMetricStore.instances.get(1);
		IJavaElement key = element.getJavaElement();
		assertNotNull("not found in store 1", store1.get(key));
		assertNotNull("not found in store 2", store2.get(key));
		store.remove(key);
		assertNull("should not be found in store 1", store1.get(key));
		assertNull("should not be found in store 2", store2.get(key));
	}
		
	public void testClear() {
		IMetricStore store = MetricStore.get();
		IMetricsElement element = getMetricsElement(PATH_PROJECT);
		store.put(element);
		MockMetricStore store1 = (MockMetricStore) MockMetricStore.instances.get(0);
		MockMetricStore store2 = (MockMetricStore) MockMetricStore.instances.get(1);
		IJavaElement key = element.getJavaElement();
		assertNotNull("not found in store 1", store1.get(key));
		assertNotNull("not found in store 2", store2.get(key));
		store.clear();
		assertTrue("store 1 not empty", store1.db.size()==0);
		assertTrue("store 2 not empty", store2.db.size()==0);
	}
	
	public void testJEClear() {
		BDBStore store = new BDBStore();
		IMetricsElement element = getMetricsElement(PATH_PROJECT);
		store.put(element);
		store.clear();
		IJavaElement jElm = element.getJavaElement();
		IMetricsElement found = store.get(jElm);
		assertNull("DB should have been empty", found);
	}
	
	public void testJERemove() {
		BDBStore store = new BDBStore();
		for (int i = 0; i < ALL.length; i++) {
			IMetricsElement element = getMetricsElement(ALL[i]);
			store.put(element);
		}
		IJavaElement cl = getMetricsElement(PATH_CLASS).getJavaElement();
		assertNotNull("Class should have stored metrics", store.get(cl));
		// now remove the src folder
		IJavaElement src = getMetricsElement(PATH_SRC).getJavaElement();
		store.remove(src);
		assertNull("src should have been removed", store.get(src));
		assertNull("class should have been removed too!", store.get(cl));
		IJavaElement project = getMetricsElement(PATH_PROJECT).getJavaElement();
		assertNotNull("project should still be there", store.get(project));
	}
}
