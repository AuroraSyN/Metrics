package net.sourceforge.metrics.core.internal.builder;

import java.util.Stack;


import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;


/**
 * Contains the result of the resource to IJavaElement translation/filtering
 * and knows how to process it in the context of a full build as well as an 
 * incremental build
 *  
 * @author Frank Sauer
 */
class FilterResult {
	
	private IPackageFragmentRoot defaultSourceFolder;
	private IJavaElement element;
	private boolean processChildren;
	private IPackageFragment defaultPackage;
	
	boolean process(Stack stack) {
		if (element != null) {
			pushChangedCommands(stack);
			return true;
		} else return processChildren;
	}
	
	/**
	 * Push one, two or three ChangedCommand objects onto the stack.
	 * One is the normal case. Two if this result holds a source folder
	 * with a default package. Three if this result holds a project with
	 * no source folders and a default package (BUG #766261) 
	 * @param stack
	 */
	private void pushChangedCommands(Stack stack) {
		stack.push(new ChangedCommand(element));
		if (defaultSourceFolder != null) {
			stack.push(new ChangedCommand(defaultSourceFolder));
		}
		if (defaultPackage != null) {
			stack.push(new ChangedCommand(defaultPackage));
		}
	}	
	
	boolean process(Stack stack, IResourceDelta delta) {
		if (element == null) return processChildren;
		switch (delta.getKind()) {
			case IResourceDelta.ADDED : 
				AddedCommand added = new AddedCommand(element);
				IPath from = delta.getMovedFromPath();
				if ( from != null) {
					added.setMovedFromPath(from);
				}
				stack.push(added);
				break;
			case IResourceDelta.REMOVED:
				stack.push(new RemovedCommand(element));
				break;
			case IResourceDelta.CHANGED:
				pushChangedCommands(stack);
				break;
		}
		return true;
	}

	public IPackageFragment getDefaultPackage() {
		return defaultPackage;
	}

	public void setDefaultPackage(IPackageFragment defaultPackage) {
		this.defaultPackage = defaultPackage;
	}

	public IPackageFragmentRoot getDefaultSourceFolder() {
		return defaultSourceFolder;
	}

	public void setDefaultSourceFolder(IPackageFragmentRoot defaultSourceFolder) {
		this.defaultSourceFolder = defaultSourceFolder;
	}

	public IJavaElement getElement() {
		return element;
	}

	public void setElement(IJavaElement element) {
		this.element = element;
	}

	public boolean isProcessChildren() {
		return processChildren;
	}

	public void setProcessChildren(boolean processChildren) {
		this.processChildren = processChildren;
	}			
}