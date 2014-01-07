package ma.mla.callcards.actions;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.Storage;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.utils.TaskUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

public class ContinueFolderAction extends Action {

	public ContinueFolderAction() {
		setText("Continuer dans un nouveau dossier");
		setImageDescriptor(ResourceManager.getDescriptor("next_date_16.png"));
	}

	@Override
	public void run() {
		Storage storage = StorageManager.getStorage();
		if (storage != null
				&& storage.isDirty()
				&& MessageDialog
						.openQuestion(
								UIUtils.getShell(),
								"Modifications",
								"Le dossier courant a été modifié, vous devez l'enregistrer avant de continuser")) {
			if (!TaskUtils.saveFolder(StorageManager.isNewFolder())) {
				return;
			}
		}
		InputDialog dlg = new InputDialog(UIUtils.getShell(),
				"Nouveau dossier", "Entrez le nom du nouveau dossier",
				storage.getName() + "_suite", new IInputValidator() {

					@Override
					public String isValid(String newText) {
						return newText.isEmpty() ? "Le nom ne doit pas être vide"
								: null;
					}
				});
		if (dlg.open() == Window.OK) {
			StorageManager.continueInNewFolder(dlg.getValue());
		}
	}

	@Override
	public boolean isEnabled() {
		return StorageManager.getStorage() != null
				&& StorageManager.isEditable() && !StorageManager.isNewFolder();
	}

}
