package ma.mla.callcards.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import ma.mla.callcards.dao.StorageManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.FileDialog;

public class TaskUtils {

	public static boolean saveFolder(final String path) {
		ProgressMonitorDialog dlg = new ProgressMonitorDialog(
				UIUtils.getShell());
		final boolean[] result = { false };
		try {
			dlg.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						if (path != null) {
							StorageManager.saveFolderAs(new File(path));
						} else {
							StorageManager.saveFolder();
						}
						result[0] = true;
					} catch (Exception e) {
						e.printStackTrace();
						UIUtils.error(
								"Impossible de sauvegarder les modfications",
								e.getMessage());
						result[0] = false;
					}
				}
			});
		} catch (Exception e) {
			UIUtils.error("Impossible de sauvegarder les modfications",
					e.getMessage());
			return false;
		}
		return result[0];
	}

	public static boolean saveFolder(boolean newFile) {
		if (newFile) {
			FileDialog dlg = new FileDialog(UIUtils.getShell());
			dlg.setFilterExtensions(new String[] { "*.ccb" });
			dlg.setFilterNames(new String[] { "Dossiers Call Cards Business" });
			String path = dlg.open();
			if (path != null) {
				if (!path.endsWith(".ccb")) {
					path = path + ".ccb";
				}
				saveFolder(path);
			} else {
				return false;
			}
		} else {
			return saveFolder(null);
		}
		return true;
	}

	public static void openFolder(final String path) {
		ProgressMonitorDialog dlg = new ProgressMonitorDialog(
				UIUtils.getShell());
		try {
			dlg.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						String problems = StorageManager.openFolder(path);
						if (problems != null && !problems.isEmpty()) {
							UIUtils.warning("Dossier invalide",
									"L'ouverture du dossier a rencontré les problèmes suivants :\n"
											+ problems);
						}
					} catch (Exception e) {
						e.printStackTrace();
						UIUtils.error("Impossible d'ouvrir le dossier",
								e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			UIUtils.error("Impossible d'ouvrir le dossier", e.getMessage());
		}
	}
}
