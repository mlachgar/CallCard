package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.InitDataDialog;
import ma.mla.callcards.model.InitData;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;

public class EditInitDataAction extends Action {

	public EditInitDataAction() {
		setText("Données initiales");
		setImageDescriptor(ResourceManager
				.getDescriptor("previous_date_16.png"));
	}

	@Override
	public void run() {
		InitData data = StorageManager.getStorage().getInitData();
		if (InitDataDialog.open(UIUtils.getShell(), data) == InitDataDialog.OK) {
			StorageManager.getStorage().setInitData(data);
		}
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable();
	}

}
