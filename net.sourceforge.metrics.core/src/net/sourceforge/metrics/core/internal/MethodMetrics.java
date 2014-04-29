package net.sourceforge.metrics.core.internal;

import net.sourceforge.metrics.core.IMetricsElement;

import org.eclipse.jdt.core.IJavaElement;

public class MethodMetrics extends MetricsElement {

	private static final long serialVersionUID = 3257844381238048825L;

	public MethodMetrics(IJavaElement javaElement) {
		super(javaElement);
	}

	public IMetricsElement[] getChildren() {
		return EMPTY;
	}

}
