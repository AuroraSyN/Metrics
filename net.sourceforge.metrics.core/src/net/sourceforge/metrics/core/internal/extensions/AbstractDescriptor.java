package net.sourceforge.metrics.core.internal.extensions;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Base class for all descriptors since they all have an id, name and description
 * 
 * @author Frank Sauer
 */
public abstract class AbstractDescriptor {

	public static final String ATT_ID = "id";
	public static final String ATT_NAME = "name";
	public static final String ATT_DESCRIPTION = "description";
	
	protected String id;
	protected String name;
	protected String description;

	protected AbstractDescriptor(IConfigurationElement element) {
		this.id = getAbsoluteValue(ATT_ID, element);
		this.name = element.getAttribute(ATT_NAME);
		this.description = element.getAttribute(ATT_DESCRIPTION);
		if (id == null || name == null) 
			throw new IllegalArgumentException(element.getName() + " must specify id and name");		 
	}
	
	/**
	 * Get the value of the given attribute prefixed with the namespace of the
	 * declaring plugin. For example, if element was declared in a plugin with
	 * id = test.foo.bar and the value of name is "value", then the absolute value
	 * would be "test.foo.bar.value"
	 * 
	 * @param attName		attribute name
	 * @param element		extension to get it from
	 * @return				absolute value
	 */
	protected String getAbsoluteValue(String attName, IConfigurationElement element) {
		String relValue = element.getAttribute(attName);
		if (relValue != null) {
			String namespace = element.getDeclaringExtension().getNamespace();
			StringBuffer result = new StringBuffer();
			if (!relValue.startsWith(namespace)) {
				result.append(namespace).append(".");
			}
			result.append(relValue);
			return result.toString();
		} else return null;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name as defined in &lt;category description="..."&gt; in the plugin.xml
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * workaround for Eclipse ignoring default values in the extension schema
	 * @param element
	 * @param name
	 * @param defaultValue
	 * @return boolean
	 */
	protected boolean getBooleanAttribute(IConfigurationElement element, String name, boolean defaultValue) {
		String val = element.getAttribute(name);
		if (val == null) return defaultValue;
		return "true".equals(val);
	}
}
