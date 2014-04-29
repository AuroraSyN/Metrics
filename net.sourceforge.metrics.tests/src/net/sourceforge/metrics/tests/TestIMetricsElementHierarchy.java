package net.sourceforge.metrics.tests;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

import net.sourceforge.metrics.core.IMetricsElement;
import net.sourceforge.metrics.core.internal.CompilationUnitMetrics;
import net.sourceforge.metrics.core.internal.PackageMetrics;
import net.sourceforge.metrics.core.internal.PackageRootMetrics;
import net.sourceforge.metrics.core.internal.TypeMetrics;

/**
 * Test the hierarchical relationship between metrics elements
 * 
 * @author Frank Sauer
 */
public class TestIMetricsElementHierarchy extends MetricsTestCase {

	/**
	 * Test that a project has no parent and its children are of the
	 * correct type (PackageRootMetrics)
	 * @throws Exception
	 */
	public void testProjectParentAndChildren() throws Exception {
		IMetricsElement adapter = getMetricsElement(PATH_PROJECT);
		assertNull("Projects don't have parents", adapter.getParent());
		IMetricsElement[] children = adapter.getChildren();
		assertEquals("Test project should have 1 child", 1, children.length);
		assertEquals("Child should be a PackageFragmentRootMetrics", PackageRootMetrics.class, children[0].getClass());
	}
	
	public void testPackageRootParentAndChildren() throws Exception {
		IMetricsElement srcM = getMetricsElement(PATH_SRC);
		IMetricsElement prjM = getMetricsElement(PATH_PROJECT);
		assertEquals("parent should be project", prjM, srcM.getParent());
		IMetricsElement[] children = srcM.getChildren();
		assertEquals("src should have 1 package (test)", 1, children.length);
		assertEquals("Child should be a PackageFragmentMetrics", PackageMetrics.class, children[0].getClass());
	}
	
	public void testPackageParentAndChildren() {
		IMetricsElement pM = getMetricsElement(PATH_PACKAGE);
		IMetricsElement srcM = getMetricsElement(PATH_SRC);
		assertEquals("parent should be PackageFragmentRoot", srcM, pM.getParent());
		IMetricsElement[] children = pM.getChildren();
		assertEquals("src should have 1 compilation unit (testClass.java)", 1, children.length);
		assertEquals("Child should be a CompilationUnitMetrics", CompilationUnitMetrics.class, children[0].getClass());
	}
	
	public void testCompilationUnitParentAndChildren() throws Exception {
		IMetricsElement pM = getMetricsElement(PATH_PACKAGE);
		IMetricsElement uM = getMetricsElement(PATH_CLASS);
		assertEquals("parent should be Package", pM, uM.getParent());
		IMetricsElement[] children = uM.getChildren();
		ICompilationUnit unit = (ICompilationUnit) uM.getJavaElement();
		IType[] types = unit.getTypes();
		assertEquals("each type should have metrics", types.length, children.length);
		assertEquals("Child should be a TypeMetrics", TypeMetrics.class, children[0].getClass());
	}
}
