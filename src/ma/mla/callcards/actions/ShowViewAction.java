package ma.mla.callcards.actions;

import java.util.Collections;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.actions.CommandAction;
import org.eclipse.ui.services.IServiceLocator;

@SuppressWarnings("restriction")
public class ShowViewAction extends CommandAction {

	public ShowViewAction(IServiceLocator serviceLocator, String viewId) {
		super(serviceLocator, "org.eclipse.ui.views.showView", Collections
				.singletonMap("org.eclipse.ui.views.showView.viewId", viewId));
		setText(PlatformUI.getWorkbench().getViewRegistry().find(viewId)
				.getLabel());
	}

	public ShowViewAction(IServiceLocator serviceLocator, String viewId,
			String iconId) {
		super(serviceLocator, "org.eclipse.ui.views.showView", Collections
				.singletonMap("org.eclipse.ui.views.showView.viewId", viewId));
		setImageDescriptor(ResourceManager.getDescriptor(iconId));
		setText(PlatformUI.getWorkbench().getViewRegistry().find(viewId)
				.getLabel());
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null;
	}

}
