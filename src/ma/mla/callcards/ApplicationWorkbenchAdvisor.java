package ma.mla.callcards;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.utils.TaskUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "CallCard.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void preStartup() {
		getWorkbenchConfigurer().setSaveAndRestore(false);
	}

	@Override
	public boolean preShutdown() {
		if (MessageDialog.openConfirm(UIUtils.getShell(), "Confirmation",
				"Voulez vous vraiment quitter l'application ?")) {
			if (StorageManager.getStorage() != null
					&& StorageManager.getStorage().isDirty()
					&& MessageDialog
							.openQuestion(UIUtils.getShell(), "Modifications",
									"Le dossier courant a été modifié, voulez vous l'enregistrer")) {
				return TaskUtils.saveFolder(StorageManager.isNewFolder());
			}
			return true;
		}
		return false;
	}

	@Override
	public void postShutdown() {
		StorageManager.stop();
	}
}
