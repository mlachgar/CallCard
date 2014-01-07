package ma.mla.callcards;

import ma.mla.callcards.dao.Storage;
import ma.mla.callcards.dao.StorageListener;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1000, 800));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		configurer.setTitle("Call Cards Business");
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
		StorageManager.addStorageListener(new StorageListener() {

			@Override
			public void folderChanged(final Storage storage, ChangeType type) {
				if (type == ChangeType.OPEN) {
					UIUtils.async(new Runnable() {

						@Override
						public void run() {
							getWindowConfigurer().setTitle(storage.getName());
							getWindowConfigurer().getWindow().getActivePage()
									.resetPerspective();
						}
					});

				}
			}
		});

		StorageManager.restoreLastFolder();
		new Job("Clean Restore Job") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				StorageManager.cleanRestore();
				return Status.OK_STATUS;
			}
		}.schedule();
	}
}
