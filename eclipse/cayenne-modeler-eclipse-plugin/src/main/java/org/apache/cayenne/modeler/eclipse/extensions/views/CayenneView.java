package org.apache.cayenne.modeler.eclipse.extensions.views;

import java.io.File;

import org.apache.cayenne.modeler.eclipse.extensions.modeler.PluginMain;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class CayenneView extends ViewPart {
	
	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new CayenneContentProvider());
		viewer.setLabelProvider(new CayenneLabelProvider());
		viewer.setInput(new CayenneViewModel());
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent e) {
				IStructuredSelection selection = (IStructuredSelection)e.getSelection();
				Object element = selection.getFirstElement();
				if (element instanceof CayenneProjectFile) {
					runModeler((CayenneProjectFile) element);
					
					viewer.setExpandedState(element, !viewer.getExpandedState(element));
				}
				else if (element instanceof CayenneDatamapFile) {
					runModeler(((CayenneDatamapFile) element).getParentProject());
				}
				else {
					viewer.setExpandedState(element, !viewer.getExpandedState(element));
				}
			}
		});
		
	}
	
	protected void runModeler(CayenneProjectFile project) {
		File projFile = project.getPath();
		if (projFile != null) {
			String[] args = new String[] { projFile.toString() };
			PluginMain plugin = new PluginMain(args);
			plugin.start();
		}
	}
	
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
