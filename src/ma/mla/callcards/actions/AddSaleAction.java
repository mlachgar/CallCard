package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.SaleDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

public class AddSaleAction extends Action {

	public AddSaleAction(boolean large) {
		setText("Ajouter une vente");
		setImageDescriptor(ResourceManager
				.getDescriptor(large ? "add_bag_48.png" : "add_bag_16.png"));
	}

	@Override
	public void run() {
		SaleDialog.create(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell());
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable();
	}

}
