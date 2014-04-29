package net.sourceforge.metrics.core.internal.builder;

import net.sourceforge.metrics.core.internal.MetricStore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;

/**
 * Base class for the commands
 * @author Frank Sauer
 */
 abstract class Command {

	protected IJavaElement element = null;
	protected Object result = null;
	
	public Command(IJavaElement element) {

		this.element = element;
	}
	
	public String getHandleIdentifier() {
		return element.getHandleIdentifier();
	}
	
	public IJavaElement getElement() {
		return element;
	}
	
	public int hashCode() {
		return element.getHandleIdentifier().hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof String) return element.getHandleIdentifier().equals(o);
		if (o instanceof Command) return element.equals(((Command)o).element);
		return false;
	}
	
	abstract void execute();
	
	public Object getResult() {
		return result;
	}
	
	protected void setResult(Object o) {
		result = o;
	}
	
	public IPath getMovedFrom() {
		return null;
	}
	
	public void removeMetricsFromCache() {
		MetricStore.get().remove(element);
	}
	
	public String toString() {
		return element.getElementName();
	}
}