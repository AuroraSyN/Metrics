package biz.volantec.utils;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class UtilsPlugin extends AbstractUIPlugin {
	
	//The shared instance.
	private static UtilsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	public static String fPluginId = "biz.volantec.utils"; //$NON-NLS-1$

	private static final ILog logger = Platform.getLog(Platform.getBundle(fPluginId));

	public static final int OK = IStatus.OK; // 0
	public static final int INFO = IStatus.INFO; // 1
	public static final int WARNING = IStatus.WARNING; // 2
	public static final int ERROR = IStatus.ERROR; // 4

	public static final int OK_DEBUG = 200 + OK;
	public static final int INFO_DEBUG = 200 + INFO;
	public static final int WARNING_DEBUG = 200 + WARNING;
	public static final int ERROR_DEBUG = 200 + ERROR;
	
	/**
	 * The constructor.
	 */
	public UtilsPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static UtilsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getString(String key) {
		ResourceBundle bundle = UtilsPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("biz.volantec.utils.UtilsPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	private void _log(int level, String message, Throwable exception) {
		if (level == OK_DEBUG || level == INFO_DEBUG || level == WARNING_DEBUG || level == ERROR_DEBUG) {
			if (!isDebugging())
				return;
		}

		int severity = IStatus.OK;
		switch (level) {
			case INFO_DEBUG :
			case INFO :
				severity = IStatus.INFO;
				break;
			case WARNING_DEBUG :
			case WARNING :
				severity = IStatus.WARNING;
				break;
			case ERROR_DEBUG :
			case ERROR :
				severity = IStatus.ERROR;
		}
		message = (message != null) ? message : "null"; //$NON-NLS-1$
		Status statusObj = new Status(severity, fPluginId, severity, message, exception);
		logger.log(statusObj);
	}
	
	public static void log(int level, String message) {
		getDefault()._log(level, message, null);
	}

	public static void log(int level, String message, Throwable exception) {
		getDefault()._log(level, message, exception);
	}

	public static void logException(String message, Throwable exception) {
		getDefault()._log(ERROR, message, exception);
	}

	public static void logException(Throwable exception) {
		getDefault()._log(ERROR, exception.getMessage(), exception);
	}

}
