package biz.volantec.utils.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import biz.volantec.utils.UtilsPlugin;
import biz.volantec.utils.views.UsusedEntriesView;
import biz.volantec.utils.widgets.WSFileDialog;

/**
 * Page for the Search wizard to perform a search for unused 
 * properties in a properties file.
 * 
 * @author Frank Sauer
 */
public class SearchPage extends DialogPage implements ISearchPage {

	private ISearchPageContainer container;
	private Label fileName;

	/**
	 * Collect the search options and kick off the search as a background job
	 * @see org.eclipse.search.ui.ISearchPage#performAction()
	 */
	public boolean performAction() {
		IFile file = getPropertiesFile();
		if (file != null) {
			try {
				IWorkbenchWindow dw = UtilsPlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
			try {
				UsusedEntriesView view = (UsusedEntriesView) dw.getActivePage().showView("biz.volantec.utils.UsusedEntriesView", null, IWorkbenchPage.VIEW_ACTIVATE); //$NON-NLS-1$
				int scope = container.getSelectedScope();
				IWorkingSet[] workingSets = container.getSelectedWorkingSets();
				Job search = new SearchJob(file, view, scope, workingSets, getSelection());
				// schedule with the view
				IWorkbenchSiteProgressService siteService = (IWorkbenchSiteProgressService)view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
				siteService.schedule(search, 0 /* now */, true /* use the half-busy cursor in the part */);
				return true;
			} catch (PartInitException e) {
				UtilsPlugin.logException(e);
			}
			} catch (Throwable e) {
				UtilsPlugin.logException(e);
			} 
		}
		return false;
	}
	
	private ISelection getSelection() {
		return container.getSelection();
	}
	
	private IFile getPropertiesFile() {
		String name = fileName.getText();
		if (name != null && name.endsWith("properties")) { //$NON-NLS-1$
			return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(name));
		} else return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
	 */
	public void setContainer(ISearchPageContainer container) {
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Label spacer = new Label(parent, SWT.NONE);
		Group main = new Group(parent, SWT.NONE);
		main.setText(UtilsPlugin.getString("SearchPage.PROPERTIES_FILE")); //$NON-NLS-1$
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		// the following is 3.1 only
		// data.verticalIndent = 10;
		main.setLayoutData(data);
		main.setLayout(new GridLayout(2, false));
		fileName = new Label(main, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		fileName.setLayoutData(data);
		Button browse = new Button(main, SWT.PUSH);
		browse.setText(UtilsPlugin.getString("SearchPage.BROWSE")); //$NON-NLS-1$
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseForpropertiesFile();
			}
		});
		setControl(main);
	}

	/**
	 * Show our own wonderful WSFileDialog to let the user pick a properties file from the workspace
	 */
	protected void browseForpropertiesFile() {
		WSFileDialog dialog = new WSFileDialog(getShell(), SWT.SINGLE, UtilsPlugin.getString("SearchPage.FILEDIALOG_TITLE"),ResourcesPlugin.getWorkspace().getRoot(), true, new String[]{"properties"}, null); //$NON-NLS-1$ //$NON-NLS-2$
		if (dialog.open() == WSFileDialog.OK) {
			fileName.setText(dialog.getSingleResult().getFullPath().toString());
			container.setPerformActionEnabled(getPropertiesFile() != null);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		initializeFileAndScope();
	}
	
	/**
	 * If a single file is selected and it is a properties file, set it as the value
	 * in the fileName field and set the scope to be Workspace or Project depending
	 * on the default preference. If no properties file is selected disable the action
	 * until one is selected.
	 */
	private void initializeFileAndScope() {
		IFile pFile = getPropertiesFileFromSelection();
		if (pFile != null) {
			fileName.setText(pFile.getFullPath().toString());
			container.setPerformActionEnabled(true);
		} else {
			fileName.setText(UtilsPlugin.getString("SearchPage.NO_FILE_MESSAGE")); //$NON-NLS-1$
			container.setPerformActionEnabled(false);			
		}
	}

	private IFile getPropertiesFileFromSelection() {
		ISelection selection = getSelection();
		if (selection != null && !selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection)selection;
				if (sel.size() == 1) {
					Object first = sel.getFirstElement();
					IResource resource= null;			
					if (first instanceof IResource)
						resource= (IResource)first;
					else if (selection instanceof IAdaptable) {
						if (resource == null)
							resource= (IResource)((IAdaptable)first).getAdapter(IResource.class);
					}
					if (resource != null) {
						if (resource.getName().endsWith("properties")) { //$NON-NLS-1$
							return (IFile)((IAdaptable)first).getAdapter(IFile.class);
						}
					}
				}
			}
		}
		return null;
	}
}
