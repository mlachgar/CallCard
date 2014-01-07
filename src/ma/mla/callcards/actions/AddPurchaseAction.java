package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.PurchaseDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

public class AddPurchaseAction extends Action {

	public AddPurchaseAction(boolean large) {
		setText("Ajouter un achat");
		setImageDescriptor(ResourceManager
				.getDescriptor(large ? "add_purchase_48.png"
						: "add_purchase_16.png"));
	}

	@Override
	public void run() {
		PurchaseDialog.create(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell());
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable();
	}

}
