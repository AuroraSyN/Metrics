package biz.volantec.utils.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import biz.volantec.utils.UtilsPlugin;

/**
 * Identify all entries in the selected properties file that are not
 * referenced anywhere else in the workspace.
 * 
 * @author Frank Sauer
 */
public class FindUnusedPropsAction implements IObjectActionDelegate {

	public static final String SEARCHPAGE_ID = "biz.volantec.utils.unused.searchpage"; //$NON-NLS-1$
	/**
	 * Constructor for FindUnusedPropsAction.
	 */
	public FindUnusedPropsAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IWorkbenchWindow dw = UtilsPlugin.getDefault()
		.getWorkbench().getActiveWorkbenchWindow();
		NewSearchUI.openSearchDialog(dw, SEARCHPAGE_ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
}
