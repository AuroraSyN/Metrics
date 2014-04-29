package net.sourceforge.metrics.core.extensions;

import org.eclipse.jdt.core.IJavaElement;

import net.sourceforge.metrics.core.ICalculator;

/**
 * This interface represents calculator contributions contributed to the
 * net.sourceforge.metrics.core.calculators extension-point
 * 
 * @author Frank Sauer
 */
public interface ICalculatorDescriptor {

	public static final String LVL_METHOD = "method";
	public static final String LVL_TYPE = "type";
	public static final String LVL_UNIT = "compilationUnit";
	public static final String LVL_PKG = "packageFragment";
	public static final String LVL_SRC = "packageFragmentRoot";
	public static final String LVL_PROJECT = "project";
	
	public static String[] LEVELS = new String[] {
		LVL_METHOD, LVL_TYPE, LVL_UNIT, LVL_PKG, LVL_SRC, LVL_PROJECT
	};
	
	public static int[] JAVA_ELEMENT_LEVELS = new int[] {
		IJavaElement.METHOD, IJavaElement.TYPE, IJavaElement.COMPILATION_UNIT, 
		IJavaElement.PACKAGE_FRAGMENT, IJavaElement.PACKAGE_FRAGMENT_ROOT,
		IJavaElement.JAVA_PROJECT
	};
	
	/**
	 * get the level as declared in plugin.xml
	 * @return
	 */
	public String getLevel();
	
	/**
	 * get the corresponding IJavaElement level (e.g. IJavaElement.TYPE)
	 * @return
	 */
	public int getElementType();
	
	/**
	 * get the name of this calculator
	 * @return
	 */
	public String getName();
	
	public String getId();
	
	/**
	 * get the actual executable extension
	 * @return
	 */
	public ICalculator getCalculator();
	
	public String getDescription();
	
	public IMetricDescriptor[] getMetrics();
}
