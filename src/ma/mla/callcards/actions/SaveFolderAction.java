package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.utils.TaskUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

public class SaveFolderAction extends Action {

	public SaveFolderAction(boolean large) {
		setText("Entregistrer le dossier");
		setImageDescriptor(ResourceManager.getDescriptor(large ? "save_48.png"
				: "save_16.png"));
		setAccelerator(SWT.CTRL | 'S');
	}

	@Override
	public void run() {
		TaskUtils.saveFolder(StorageManager.isNewFolder());
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable()
				&& StorageManager.getStorage().isDirty();
	}

}
