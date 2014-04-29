package biz.volantec.utils.search;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.internal.core.text.ITextSearchResultCollector;
import org.eclipse.search.internal.core.text.MatchLocator;
import org.eclipse.search.internal.core.text.TextSearchEngine;
import org.eclipse.search.internal.core.text.TextSearchScope;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;

import biz.volantec.utils.UtilsPlugin;
import biz.volantec.utils.views.UsusedEntriesView;

/**
 * Background job to find unused properties
 * 
 * @author Frank Sauer
 */
public class SearchJob extends Job {

	private IFile file;
	private UsusedEntriesView view;
	private int scope;
	private IWorkingSet[] workingSets;
	private ISelection selection;
	
	public SearchJob(IFile file, UsusedEntriesView view, int scope, IWorkingSet[] workingSets, ISelection selection) {
		super(UtilsPlugin.getString("SearchJob.JOB_TITLE")); //$NON-NLS-1$
		setUser(true);
		this.file = file;
		this.view = view;
		this.scope = scope;
		this.workingSets = workingSets;
		this.selection = selection;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		try {
			InputStream in = file.getContents(true);
			final Properties props = new Properties();
			final Properties unused = new Properties();
			props.load(in);
			in.close();
			findUnused(props, unused, monitor);
			if (!monitor.isCanceled()) {
				view.setFile(file);
				view.setEntries(unused);
			}
			return Status.OK_STATUS;
		} catch (Exception e) {
			return new Status(Status.ERROR, UtilsPlugin.fPluginId, -1, UtilsPlugin.getString("SearchJob.ERROR"), e); //$NON-NLS-1$
		} 
	}
	
	private void findUnused(Properties props, Properties unused, IProgressMonitor monitor) {
		monitor.beginTask(UtilsPlugin.getString("SearchJob.MESSAGE"), props.size()); //$NON-NLS-1$
		TextSearchScope textSearchScope = getSearchScope();
		for (Iterator i = props.keySet().iterator(); i.hasNext();) {
			if (monitor.isCanceled()) break;
			String next = (String)i.next();
			monitor.subTask(next);
			if (isUnused(textSearchScope, next, monitor)) {
				unused.put(next, props.getProperty(next));
			}
			monitor.worked(1);
		}
	}

	/**
	 * @param next
	 * @return
	 */
	private boolean isUnused(TextSearchScope scope, String name, final IProgressMonitor monitor) {
		IProgressMonitor reporter = new NullProgressMonitor();
		PropertySearchCollector collector = new PropertySearchCollector(file, reporter, monitor);
		new TextSearchEngine().search(scope, true, collector, new MatchLocator(name, true, false), true);
		return !collector.found;
	}
	
	private TextSearchScope getSearchScope() {
			
		// Setup search scope
		TextSearchScope scope= null;
		switch (this.scope) {
			case ISearchPageContainer.WORKSPACE_SCOPE:
				scope= TextSearchScope.newWorkspaceScope();
				break;
			case ISearchPageContainer.SELECTION_SCOPE:
				scope= getSelectedResourcesScope(false);
				break;
			case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
				scope= getSelectedResourcesScope(true);
				break;
			case ISearchPageContainer.WORKING_SET_SCOPE:
				String desc= "Working Set - {0}"; //$NON-NLS-1$
				scope= new TextSearchScope(desc, workingSets);
		}		
		return scope;
	}
	
	private TextSearchScope getSelectedResourcesScope(boolean isProjectScope) {
		TextSearchScope scope= new TextSearchScope("Selection"); //$NON-NLS-1$
		int elementCount= 0;
		IProject firstProject= null;
		if (getSelection() instanceof IStructuredSelection && !getSelection().isEmpty()) {
			Iterator iter= ((IStructuredSelection)getSelection()).iterator();
			while (iter.hasNext()) {
				Object selection= iter.next();

				IResource resource= null;			
				if (selection instanceof IResource)
					resource= (IResource)selection;
				else if (selection instanceof IAdaptable) {
					if (isProjectScope)
						resource= (IProject)((IAdaptable)selection).getAdapter(IProject.class);
					if (resource == null)
						resource= (IResource)((IAdaptable)selection).getAdapter(IResource.class);
				}
				if (resource != null) {
					if (isProjectScope) {
						resource= resource.getProject();
						if (resource == null || isProjectScope && scope.encloses(resource))
							continue;
						if (firstProject == null)
							firstProject= (IProject)resource;
					}
					elementCount++;
					scope.add(resource);
				}
			}
		} else if (isProjectScope) {
			IProject editorProject= getEditorProject();
			if (editorProject != null)scope.add(editorProject);
		}
		if (isProjectScope) {
			if (elementCount > 1)
				scope.setDescription("Projects {0}, ..."); //$NON-NLS-1$
			else if (elementCount == 1)
				scope.setDescription("Project {0}"); //$NON-NLS-1$
			else 
				scope.setDescription("Project {0}"); //$NON-NLS-1$ //$NON-NLS-2$
		} 
		return scope;
	}
	
	/**
	 * @return
	 */
	private ISelection getSelection() {
		return selection;
	}

	private IProject getEditorProject() {
		IWorkbenchPart activePart= SearchPlugin.getActivePage().getActivePart();
		if (activePart instanceof IEditorPart) {
			IEditorPart editor= (IEditorPart) activePart;
			IEditorInput input= editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				return ((IFileEditorInput)input).getFile().getProject();
			}
		}
		return null;
	}
	
	private static class PropertySearchCollector implements ITextSearchResultCollector {
		String sourceName = null;
		IProgressMonitor monitor;
		private boolean found;
		private IProgressMonitor cancelable;
		
		PropertySearchCollector(IFile source, IProgressMonitor reporter, IProgressMonitor cancelable) {
			this.sourceName = source.getName();
			this.monitor = reporter;
			this.cancelable = cancelable;
		}
		
		public IProgressMonitor getProgressMonitor() {
			return monitor;
		}

		public void aboutToStart() throws CoreException {
		}

		/**
		 * If we find one result other than the properties file itself, we're done
		 */
		public void accept(IResourceProxy proxy, int start, int length) throws CoreException {
			if ((cancelable.isCanceled())||(!sourceName.equals(proxy.getName()))) {
				found = true;
				throw new CoreException(Status.OK_STATUS);
			}
		}

		public void done() throws CoreException {
		}
		
	}

}
