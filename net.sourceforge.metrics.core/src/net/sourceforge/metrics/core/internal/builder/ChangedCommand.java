package net.sourceforge.metrics.core.internal.builder;

import org.eclipse.jdt.core.IJavaElement;

/**
 * ChangedCommand removes cached metricas and recalculates them 
 * @author Frank Sauer
 */
 class ChangedCommand extends Command {

	ChangedCommand(IJavaElement element) {
		super(element);
	}

	void execute() {
		removeMetricsFromCache();
		//setResult(Dispatcher.calculateAbstractMetricSource(element));
	}
	
}