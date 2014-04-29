/*
 * Created on May 13, 2003
 */
package net.sourceforge.metrics.core;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Log message and errors to the Eclipse log file
 * 
 * @author Frank Sauer
 */
public class Log {
	
	public final static String pluginId = "net.sourceforge.metrics.core";
	
	public static void logError(String message, Throwable t) {
		MetricsCorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, pluginId,
				IStatus.ERROR, message, t));
	}
	
	public static void logMessage(String message) {
		MetricsCorePlugin.getDefault().getLog().log(
			new Status(IStatus.INFO, pluginId, IStatus.INFO, message, null));
	}

}
