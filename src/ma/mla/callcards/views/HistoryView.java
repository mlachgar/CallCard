package ma.mla.callcards.views;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ma.mla.callcards.dao.HistoryItem;
import ma.mla.callcards.dao.StorageHistory;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.SummaryComposite;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.TaskUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class HistoryView extends ViewPart {

	public static final String ID = "ma.mla.view.history";

	private TreeViewer historyTree;
	private SummaryComposite summaryComposite;

	@Override
	public void createPartControl(Composite parent) {
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		createistoryTree(sash);
		summaryComposite = new SummaryComposite(sash, SWT.BORDER);
		summaryComposite.setVisible(false);
		historyTree
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						HistoryItem item = UIUtils.getFirstSelected(
								historyTree.getSelection(), HistoryItem.class);
						if (item != null) {
							summaryComposite.setSummary(item.getContent());
							summaryComposite.setVisible(true);
						} else {
							summaryComposite.setVisible(false);
						}
					}
				});
		historyTree.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				HistoryItem item = UIUtils.getFirstSelected(
						event.getSelection(), HistoryItem.class);
				if (item != null) {
					if (StorageManager.getStorage() != null
							&& StorageManager.getStorage().isDirty()
							&& MessageDialog.openQuestion(UIUtils.getShell(),
									"Modifications",
									"Le dossier courant a été modifié, voulez l'enregistrer ?")) {
						if (!TaskUtils.saveFolder(StorageManager.isNewFolder())) {
							return;
						}
					}
					TaskUtils.restoreFolder(item.getFile());
				}
			}
		});
		sash.setWeights(new int[] { 30, 70 });
		summaryComposite.setVisible(false);
		reload();
	}

	private void createistoryTree(Composite parent) {
		historyTree = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);

		historyTree.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof StorageHistory) {
					StorageHistory h = (StorageHistory) element;
					return h.getName();
				} else if (element instanceof HistoryItem) {
					HistoryItem item = (HistoryItem) element;
					return DataUtils.FULL_DATE_FORMAT.format(new Date(item
							.getFile().lastModified()));
				}
				return super.getText(element);
			}
		});

		historyTree.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean hasChildren(Object o) {
				return o instanceof StorageHistory;
			}

			@Override
			public Object getParent(Object arg0) {
				return null;
			}

			@Override
			public Object[] getElements(Object o) {
				if (o instanceof Collection<?>) {
					Collection<?> c = (Collection<?>) o;
					return c.toArray();
				}
				return null;
			}

			@Override
			public Object[] getChildren(Object o) {
				if (o instanceof StorageHistory) {
					StorageHistory h = (StorageHistory) o;
					return h.getItems().toArray();
				}
				return null;
			}
		});

	}

	private void reload() {
		ProgressMonitorDialog dlg = new ProgressMonitorDialog(
				UIUtils.getShell());
		try {
			dlg.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						final List<StorageHistory> history = StorageManager
								.getHistory();
						UIUtils.async(new Runnable() {

							@Override
							public void run() {
								historyTree.setInput(history);
								historyTree.expandAll();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						UIUtils.error("Impossible de charger l'historique",
								e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			UIUtils.error("Impossible de charger l'historique", e.getMessage());
		}
	}

	@Override
	public void setFocus() {
		summaryComposite.setFocus();
	}

}
