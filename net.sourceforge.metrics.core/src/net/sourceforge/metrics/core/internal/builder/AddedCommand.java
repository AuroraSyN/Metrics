package net.sourceforge.metrics.core.internal.builder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;

/**
 * AddedCommand calculates the metrics for a new/moved resource 
 * @author Frank Sauer
 */
 class AddedCommand extends Command {

	IPath movedFrom;

	AddedCommand(IJavaElement element) {
		super(element);
	}

	/**
	 * @param from
	 */
	public void setMovedFromPath(IPath from) {
		this.movedFrom = from;
		
	}

	void execute() {
		removeMetricsFromCache();
		//setResult(Dispatcher.calculateAbstractMetricSource(element));
	}
	
	public IPath getMovedFrom() {
		return movedFrom;
	}
}