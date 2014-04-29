/*
 * $Log: TestAdapterFactory.java,v $
 * Revision 1.3  2005/03/19 21:05:51  sauerf
 * properly initialize a java project and pull up common code
 *
 * Revision 1.2  2005/03/19 19:25:33  sauerf
 * setUp must create a Java project. FAQ 351 taught us how
 *
 * Revision 1.1  2005/03/19 18:54:52  sauerf
 * initial unit tests
 *
 */
package net.sourceforge.metrics.tests;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.internal.CompilationUnitMetrics;
import net.sourceforge.metrics.core.internal.MethodMetrics;
import net.sourceforge.metrics.core.internal.PackageMetrics;
import net.sourceforge.metrics.core.internal.PackageRootMetrics;
import net.sourceforge.metrics.core.internal.ProjectMetrics;
import net.sourceforge.metrics.core.internal.TypeMetrics;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

/**
 * Test the behavior of the MetricsAdapterFactory
 * 
 * @author Frank Sauer
 */
public class TestAdapterFactory extends MetricsTestCase {

	private void doTestAdapter(String path, Class expectedAdapter) throws Exception {
		IResource p = find(path);
		assertNotNull("Could not find resource: " + path, p);
		IJavaElement element = JavaCore.create(p);
		assertNotNull("Not a java element", element);
		Object adapter = Platform.getAdapterManager().getAdapter(element,
				IMetricsElement.class);
		assertNotNull("Could not create adapter", adapter);
		assertEquals("Wrong adapterclass", expectedAdapter, adapter.getClass());
		
	}
	
	public void testMetricsFromProject() throws Exception {
		doTestAdapter(PATH_PROJECT, ProjectMetrics.class);
	}

	public void testMetricsFromPackageRoot() throws Exception {
		doTestAdapter(PATH_SRC, PackageRootMetrics.class);
	}
	
	public void testMetricsFromPackage() throws Exception {
		doTestAdapter(PATH_PACKAGE, PackageMetrics.class);
	}
	
	public void testMetricsFromCompilationUnit() throws Exception {
		doTestAdapter(PATH_CLASS, CompilationUnitMetrics.class);
	}
	
	public void testMetricsFromType() throws Exception {
		ICompilationUnit file = (ICompilationUnit) JavaCore.create(find(PATH_CLASS));
		IType type = file.getAllTypes()[0];
		Object adapter = Platform.getAdapterManager().getAdapter(type, IMetricsElement.class);
		assertNotNull("Could not create adapter", adapter);
		assertEquals("Wrong adapterclass", TypeMetrics.class, adapter.getClass());
	}
	
	public void testMetricsFromMethod() throws Exception {
		ICompilationUnit file = (ICompilationUnit) JavaCore.create(find(PATH_CLASS));
		IType type = file.getAllTypes()[0];
		IMethod method = type.getMethods()[0];
		Object adapter = Platform.getAdapterManager().getAdapter(method, IMetricsElement.class);
		assertNotNull("Could not create adapter", adapter);
		assertEquals("Wrong adapterclass", MethodMetrics.class, adapter.getClass());
	}
}
