package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.ExpenseDialog;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;

public class AddExpenseAction extends Action {

	public AddExpenseAction(boolean large) {
		setText("Ajouter une dépense");
		setImageDescriptor(ResourceManager
				.getDescriptor(large ? "remove_pay_48.png"
						: "remove_pay_16.png"));
	}

	@Override
	public void run() {
		new ExpenseDialog(UIUtils.getShell()).open();
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable();
	}

}
