/*
 * $Log: IJavaElementFilter.java,v $
 * Revision 1.1  2005/03/19 22:49:52  sauerf
 * added hierarchical behavior to IMetricsElement and its implementations
 * 
 */
package net.sourceforge.metrics.core.internal;

import org.eclipse.jdt.core.IJavaElement;

interface IJavaElementFilter {
	/**
	 * return true in order to accept this element
	 * @param element an IJavaElement
	 * @return true to accept, false to ignore it
	 */
	boolean accept(IJavaElement element);
}