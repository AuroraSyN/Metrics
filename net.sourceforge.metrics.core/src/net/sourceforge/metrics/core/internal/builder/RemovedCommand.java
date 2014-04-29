package net.sourceforge.metrics.core.internal.builder;

import org.eclipse.jdt.core.IJavaElement;

/**
 * RemovedCommand removes the cached metrics 
 * @author Frank Sauer
 */
 class RemovedCommand extends Command {

	RemovedCommand(IJavaElement element) {
		super(element);
	}

	void execute() {
		removeMetricsFromCache();
	}
	
}