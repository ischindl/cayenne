package org.apache.cayenne.modeler.eclipse.extensions.views;

import org.apache.cayenne.modeler.eclipse.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CayenneLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		if (element instanceof WorkspaceProject) {
			return ((WorkspaceProject) element).getName();
		}
		else if (element instanceof CayenneProjectFile) {
			return ((CayenneProjectFile) element).getName();
		}
		else {
			return ((CayenneDatamapFile) element).getName();
		}
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof WorkspaceProject) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
		}
		else {
			ImageDescriptor image = Activator.getImageDescriptor("icons/cayenne.png");
			return image.createImage();
		}
	}

}
