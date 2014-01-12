package ma.mla.callcards.actions;

import java.util.Collections;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.views.HistoryView;

import org.eclipse.ui.internal.actions.CommandAction;
import org.eclipse.ui.services.IServiceLocator;

@SuppressWarnings("restriction")
public class ShowHistoryAction extends CommandAction {

	public ShowHistoryAction(IServiceLocator serviceLocator, String iconId) {
		super(serviceLocator, "org.eclipse.ui.views.showView", Collections
				.singletonMap("org.eclipse.ui.views.showView.viewId",
						HistoryView.ID));
		setImageDescriptor(ResourceManager.getDescriptor(iconId));
		setText("Voire l'historique");
	}

}
