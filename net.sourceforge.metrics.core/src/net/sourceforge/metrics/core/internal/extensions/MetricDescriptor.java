package net.sourceforge.metrics.core.internal.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.metrics.core.Log;
import net.sourceforge.metrics.core.MetricsCorePlugin;
import net.sourceforge.metrics.core.extensions.IMetricDescriptor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Preferences;

public class MetricDescriptor extends AbstractDescriptor implements IMetricDescriptor {

	private static final String ATT_PROP_SUM = "propagateSum";
	private static final String ATT_PROP_AVG = "propagateAvg";
	private static final String ATT_PROP_MAX = "propagateMax";
	
	private boolean propagateSum = true;
	private boolean propagateAvg = true;
	private boolean propagateMax = true;
	private boolean allowDisable = true;
	private String[] requires = null; 
	private String newAvgMaxAt = null;
	private String sumOf = null;
	private String defaultHint;
	private Double defaultMax;
	private Double defaultMin;
	private String hint;
	private Double max;
	private Double min;	

	MetricDescriptor(IConfigurationElement element) throws IllegalArgumentException {
		super(element);
		propagateSum = getBooleanAttribute(element, ATT_PROP_SUM, true);
		propagateAvg = getBooleanAttribute(element, ATT_PROP_AVG, true);
		propagateMax = getBooleanAttribute(element, ATT_PROP_MAX, true);
		this.sumOf = element.getAttribute("sumOf");;
		newAvgMaxAt = element.getAttribute("newAvgMaxAt");
		allowDisable = getBooleanAttribute(element,"allowDisable",true);
		IConfigurationElement[] ranges = element.getChildren("range");
		if (ranges.length>0) {
			IConfigurationElement range = ranges[0];
			String minStr = range.getAttribute("min");
			String maxStr = range.getAttribute("max");
			String hint   = range.getAttribute("hint");
			setRange(minStr, maxStr, hint);
		}
		String req = element.getAttribute("requires");	
		if (req != null && req.length()>0) {
			StringTokenizer t = new StringTokenizer(req, ",");
			List result = new ArrayList();
			while (t.hasMoreTokens()) {
				result.add(t.nextToken().trim());
			} 
			requires = (String[]) result.toArray(new String[]{});
		} 
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#getRequiredMetricIds()
	 */
	public String[] getRequiredMetricIds() {
		return requires;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#isAllowDisable()
	 */
	public boolean isAllowDisable() {
		return allowDisable;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#isPropagateAvg()
	 */
	public boolean isPropagateAvg() {
		return propagateAvg;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#isPropagateMax()
	 */
	public boolean isPropagateMax() {
		return propagateMax;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#isPropagateSum()
	 */
	public boolean isPropagateSum() {
		return propagateSum;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#getMax()
	 */
	public Double getMax() {
		double max = getPreferences().getDouble(getPrefName("MAX"));
		if (max == Preferences.DOUBLE_DEFAULT_DEFAULT) return null;
		return new Double(max);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#getMin()
	 */
	public Double getMin() {
		double min = getPreferences().getDouble(getPrefName("MIN"));
		if (min == Preferences.DOUBLE_DEFAULT_DEFAULT) return null;
		return new Double(min);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#isValueInRange(double)
	 */
	public boolean isValueInRange(double value) {
		if ((getMin() != null) && (value < getMin().doubleValue())) return false;
		if ((getMax() != null) && (value > getMax().doubleValue())) return false;
		return true;
	}
	/**
	 * @param minStr
	 * @param maxStr
	 * @param hint
	 */
	private void setRange(String minStr, String maxStr, String hint) {
		if (minStr != null) {
			try {
				min = new Double(minStr);
				defaultMin = min;
			} catch (NumberFormatException x) {
				Log.logError("Non-numeric minimum specified by a metrics extension", x);
			}
		}
		if (maxStr != null) {
			try {
				max = new Double(maxStr);
				defaultMax = max;
			} catch (NumberFormatException x) {
				Log.logError("Non-numeric maximum specified by a metrics extension", x);
			}
		}
		this.hint = hint;
		defaultHint = hint;
		initPreferences();
	}
	
	/**
	 * @param string
	 * @return
	 */
	private String getPrefName(String string) {
		StringBuffer b = new StringBuffer("METRICS.RANGE.");
		b.append(getId()).append(".").append(string);
		return b.toString();
	}

	private void initPreferences() {
		if (hint != null) getPreferences().setDefault(getPrefName("HINT"), hint);
		if (min != null) getPreferences().setDefault(getPrefName("MIN"), min.doubleValue());
		if (max != null) getPreferences().setDefault(getPrefName("MAX"), max.doubleValue());
	}
	
	public void copyToPreferences() {
		if (hint != null) getPreferences().setValue(getPrefName("HINT"), hint);
		if (min != null) getPreferences().setValue(getPrefName("MIN"), min.doubleValue());
		if (max != null) getPreferences().setValue(getPrefName("MAX"), max.doubleValue());
	}
	
	/**
	 * @return
	 */
	private Preferences getPreferences() {
		return MetricsCorePlugin.getDefault().getPluginPreferences();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.metrics.core.internal.extensions.IMetricDescriptor2#resetToDefaults()
	 */
	public void resetToDefaults() {
		hint = defaultHint;
		max = defaultMax;
		min = defaultMin;
		getPreferences().setToDefault(getPrefName("HINT"));
		getPreferences().setToDefault(getPrefName("MIN"));
		getPreferences().setToDefault(getPrefName("MAX"));
	}
}
