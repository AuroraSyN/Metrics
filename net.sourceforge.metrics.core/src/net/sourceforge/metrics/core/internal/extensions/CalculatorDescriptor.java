package net.sourceforge.metrics.core.internal.extensions;

import java.util.HashMap;

import net.sourceforge.metrics.core.ICalculator;
import net.sourceforge.metrics.core.Log;
import net.sourceforge.metrics.core.extensions.ICalculatorDescriptor;
import net.sourceforge.metrics.core.extensions.IMetricDescriptor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;


/**
 * Contains the specification of a calculator as contained in a plugin manifest
 *
 * @author Frank Sauer
 */
public class CalculatorDescriptor extends AbstractDescriptor implements ICalculatorDescriptor {

	public static final String ATT_LEVEL = "level";

	private String level;
	private ICalculator calculator;
	private int elementType = -1;
	
	private HashMap metrics;
	
	/**
	 * Constructor for CalculatorDescriptor.
	 * @throws CoreException if calculator class can not be found \
	 * or no instance can be created or if id, name or level are missing
	 */
	CalculatorDescriptor(IConfigurationElement element) throws IllegalArgumentException {
		super(element);
		this.level = element.getAttribute(ATT_LEVEL);
		if (level == null) {
			throw new IllegalArgumentException("calculator must have level");
		}
		for (int i = 0; i < LEVELS.length; i++) {
			if (level.equals(LEVELS[i])) {
				elementType = JAVA_ELEMENT_LEVELS[i];
			}
		}
		if (elementType == -1) {
			throw new IllegalArgumentException("calculator has unknown level: " + level);
		}
		try {
			calculator = (ICalculator)element.createExecutableExtension("calculatorClass");
			calculator.setName(name);
		} catch (CoreException e) {
			Log.logError("CalculatorDescriptor::<init>", e);
			throw new IllegalArgumentException("Error creating calculator");
		}
	}

	/**
	 * Returns the level.
	 * @return String
	 */
	public String getLevel() {
		return level;
	}
	
	/**
	 * get the type of the IJavaElement
	 * @return
	 */
	public int getElementType() {
		return elementType;
	}
	
	/**
	 * get the next higher level from level or null if level is project
	 * @param level
	 * @return String
	 */
	public static String getNextLevel(String level) {
		for (int i = 0; i < LEVELS.length-1;i++) {
			if (LEVELS[i].equals(level)) return LEVELS[i+1];
		}
		return null;
	}
	
	/**
	 * get the next lower level from level or null if level is method
	 * @param level
	 * @return String
	 */
	public static String getPreviousLevel(String level) {
		for (int i = 1; i < LEVELS.length;i++) {
			if (LEVELS[i].equals(level)) return LEVELS[i-1];
		}
		return null;
	}	
	/**
	 * Get the next higher level from lvl, e.g. method returns type, type
	 * returns compilationUnit, etc. project returns null.
	 * @return String
	 */
	public String getParentLevel() {
		return getNextLevel(level);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.extensions.ICalculatorDescriptor#getCalculator()
	 */
	public ICalculator getCalculator() {
		return calculator;
	}
	
	void addMetric(IMetricDescriptor metric) {
		if (metrics == null) {
			metrics = new HashMap();
		}
		metrics.put(metric.getId(), metric);
	}
	
	public String toString() {
		return "<calculator name=\"" + getName() + "\" id=\"" + id + "\">";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.extensions.ICalculatorDescriptor#getMetrics()
	 */
	public IMetricDescriptor[] getMetrics() {
		return (IMetricDescriptor[]) metrics.values().toArray(new IMetricDescriptor[]{});
	}
}
