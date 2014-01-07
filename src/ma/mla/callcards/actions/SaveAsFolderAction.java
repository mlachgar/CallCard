package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.utils.TaskUtils;

import org.eclipse.jface.action.Action;

public class SaveAsFolderAction extends Action {

	public SaveAsFolderAction(boolean large) {
		setText("Enregistrer le dossier sous");
		setImageDescriptor(ResourceManager
				.getDescriptor(large ? "save_as_48.png" : "save_as_16.png"));
	}

	@Override
	public void run() {
		TaskUtils.saveFolder(true);
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable();
	}

}
