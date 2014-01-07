package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.utils.TaskUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;

public class OpenFolderAction extends Action {

	public OpenFolderAction(boolean large) {
		setText("Ouvrir un dossier");
		setImageDescriptor(ResourceManager
				.getDescriptor(large ? "open_folder_48.png"
						: "open_folder_16.png"));
	}

	@Override
	public void run() {
		if (StorageManager.getStorage() != null
				&& StorageManager.getStorage().isDirty()
				&& MessageDialog
						.openQuestion(UIUtils.getShell(), "Modifications",
								"Le dossier courant a été modifié, voulez l'enregistrer ?")) {
			if (!TaskUtils.saveFolder(StorageManager.isNewFolder())) {
				return;
			}
		}
		FileDialog dlg = new FileDialog(UIUtils.getShell());
		dlg.setFilterExtensions(new String[] { "*.ccb" });
		dlg.setFilterNames(new String[] { "Dossiers Call Cards Business" });
		String path = dlg.open();
		if (path != null) {
			TaskUtils.openFolder(path);
		}
	}

}
