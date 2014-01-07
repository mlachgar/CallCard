package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.PayDialog;
import ma.mla.callcards.model.Pay.PersonType;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

public class AddPayAction extends Action {

	private PersonType type;

	public AddPayAction(PersonType type, boolean large) {
		this.type = type;
		if (type == PersonType.PROVIDER) {
			setText("Ajouter une paie fournisseur");
			setImageDescriptor(ResourceManager
					.getDescriptor(large ? "remove_prov_pay_48.png"
							: "remove_prov_pay_16.png"));
		} else {
			setText("Ajouter une paie client");
			setImageDescriptor(ResourceManager
					.getDescriptor(large ? "add_pay_48.png" : "add_pay_16.png"));
		}

	}

	@Override
	public void run() {
		if (type == PersonType.PROVIDER) {
			PayDialog.addProviderPays(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell());
		} else {
			PayDialog.addClientPays(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell());
		}
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable();
	}

}
