package biz.volantec.utils.views;


import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import biz.volantec.utils.UtilsPlugin;

/**
 * Show the results of the unused property search. 
 * Retargets the global delete and select all actions
 * @author Frank Sauer
 */
public class UsusedEntriesView extends ViewPart {
	private TableViewer viewer;
	private Action deleteAction;
	private Action selectAllAction;
	private Action doubleClickAction;
	private Properties unused;
	
	private IFile file;
	private Table table;
	
	class ViewContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
		}
		
		/**
		 * return the properties as an array of string arrays ({{key, value}*}*)
		 */
		public Object[] getElements(Object parent) {
			if (unused == null) return new String[][]{};
			String[][] result = new String[unused.size()][2];
			int row = 0;
			for (Iterator i = unused.keySet().iterator(); i.hasNext();row++) {
				String key = (String)i.next();
				String value = unused.getProperty(key);
				result[row][0] = key;
				result[row][1] = value;
			}
			return result;
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * get the key for the first column, and the value for the second
		 */
		public String getColumnText(Object obj, int index) {
			String[] row = (String[])obj;
			return row[index];
		}
		
		public Image getColumnImage(Object obj, int index) {
			return null;
		}
		
		public Image getImage(Object obj) {
			return null;
		}
	}
	
	class NameSorter extends ViewerSorter {
		
		/**
		 * compare the property names
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			String key1 = ((String[])e1)[0];
			String key2 = ((String[])e2)[0];
			return key1.compareTo(key2);
		}
}

	/**
	 * The constructor.
	 */
	public UsusedEntriesView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		table = createTable(parent);
		viewer = new TableViewer(table);
        viewer.setUseHashlookup(true);
        createColumns(table);
		viewer.setColumnProperties(new String[]{UtilsPlugin.getString("UsusedEntriesView.NAME"), UtilsPlugin.getString("UsusedEntriesView.VALUE")}); //$NON-NLS-1$ //$NON-NLS-2$
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();		
		hookDoubleClickAction();
		retargetActions();
	}

	/**
	 * 
	 */
	private void retargetActions() {
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAllAction);
	}

	private Table createTable(Composite parent) {
		final Table t = new Table(parent,SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH | SWT.H_SCROLL | SWT.V_SCROLL);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		t.setLayoutData(data);
		t.setLinesVisible (true);
		t.setHeaderVisible (true);
		t.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				deleteAction.setEnabled(t.getSelectionCount()>0);
			}
		});
		return t;
	}

	/**
	 * create the columns for name and value
	 * @param t
	 */
	private void createColumns(final Table t) {
		final TableColumn col1 = new TableColumn(t, SWT.LEFT, 0);
		int tableWidth = t.getSize().x;
		col1.setText(UtilsPlugin.getString("UsusedEntriesView.NAME")); //$NON-NLS-1$
		col1.setWidth(500);
		col1.setResizable(true);
		final TableColumn col2 = new TableColumn(t, SWT.LEFT, 1);
		col2.setText(UtilsPlugin.getString("UsusedEntriesView.VALUE")); //$NON-NLS-1$
		col2.setWidth(500);
		col2.setResizable(true);
		// when table (or actually, its parent view) is resized, resize
		// the columns to split the width equally
		t.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int w = t.getClientArea().width;
				col1.setWidth(w/2);
				col2.setWidth(w/2);
				t.layout();
			}
		
		});
	}

	private void makeActions() {
		deleteAction = new Action() {
			public void run() {
				deleteSelected();
			}
		};
		deleteAction.setEnabled(false);
		
		selectAllAction = new Action() {
			public void run() {
				table.selectAll();
				deleteAction.setEnabled(table.getSelectionCount()>0);
			}
		};
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				String[] row = (String[]) ((IStructuredSelection)selection).getFirstElement();
				String key = row[0];
				revealAndSelect(key);
			}
		};
	}

	/**
	 * Remove all the selected properties from my current properties file
	 */
	protected void deleteSelected() {
		ISelection sel = viewer.getSelection();
		if (!sel.isEmpty()) {
			ITextEditor editor = (ITextEditor)openEditor();
			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());
			IStructuredSelection ssel = (IStructuredSelection)sel;
			for (Iterator i = ssel.iterator(); i.hasNext();) {
				String[] row = (String[])i.next();
				String key = row[0];
				String value = row[1];
				if (deleteFromWorkingCopy(doc, key, value)) {
					unused.remove(key);
				}
			}
		}
		viewer.refresh();
	}

	/**
	 * In the open editor on my current properties file, delete the lines on which the 
	 * selected propertynames occur. Does not currently support multiline property values
	 * @param key
	 */
	private boolean deleteFromWorkingCopy(IDocument doc, String key, String value) {
		try {
			FindReplaceDocumentAdapter fra = new FindReplaceDocumentAdapter(doc);
			IRegion region =  fra.find(0, key, true, true, true, false);
			while (region != null) {
				int line = doc.getLineOfOffset(region.getOffset());
				int start = doc.getLineOffset(line);
				int length = doc.getLineLength(line);
				// handle multiline property values
				String text = doc.get(start, length).trim();
				while (text.endsWith("\\")) { //$NON-NLS-1$
					length = length + doc.getLineLength(++line);
					text = doc.get(start, length).trim();
				}
				doc.replace(start, length, ""); //$NON-NLS-1$
				region =  fra.find(0, key, true, true, true, false);
			}
			return true;
		} catch (BadLocationException e) {
			UtilsPlugin.logException(e);
			return false;
		}
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * @param name
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/**
	 * @param unused
	 */
	public void setEntries(Properties unused) {
		this.unused = unused;
		table.getDisplay().asyncExec(new Runnable(){

			public void run() {
				viewer.refresh();

			}
			
		});
	}
	
	/**
	 * Find the given property name in the current properties file, open it in an editor and select it.
	 * @param key
	 */
	private void revealAndSelect(String key) {
		ITextEditor editor = (ITextEditor)openEditor();
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		FindReplaceDocumentAdapter fra = new FindReplaceDocumentAdapter(doc);		
		try {
			IRegion region =  fra.find(0, key, true, true, true, false);
			if (region != null) {
				editor.selectAndReveal(region.getOffset(), region.getLength());
			}
		} catch (BadLocationException e) {
			UtilsPlugin.logException(e);
		}
	}
	
	/**
	 * open an editor on my current properties file
	 * @return
	 */
	public IEditorPart openEditor() {
		// Open editor on new file.
		String editorId = null;
		IEditorDescriptor editor = UtilsPlugin.getDefault()
				.getWorkbench().getEditorRegistry().getDefaultEditor(
						file.getLocation().toOSString());
		if (editor != null) {
			editorId = editor.getId();
		}
		IWorkbenchWindow dw = UtilsPlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		try {
			if (dw != null) {
				IWorkbenchPage page = dw.getActivePage();
				if (page != null)
					return page.openEditor(new FileEditorInput(file), editorId, true);
			}
		} catch (PartInitException e) {
		}
		return null;
	}

}