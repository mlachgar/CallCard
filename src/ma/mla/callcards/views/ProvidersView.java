package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.ProviderDialog;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.utils.SafePropertyListener;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class ProvidersView extends ViewPart {

	public static final String ID = "ma.mla.view.providers";

	private Composite rootComposite;
	private TableViewer providersTable;
	private Action createProviderAction;
	private Action deleteProviderAction;
	private PropertyChangeListener pcl;

	@Override
	public void createPartControl(Composite parent) {
		makeActions();
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));
		providersTable = new TableViewer(rootComposite, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = providersTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		providersTable.setContentProvider(new ArrayContentProvider());

		TableViewerColumn tvc = new TableViewerColumn(providersTable, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Nom");
		col.setWidth(180);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Provider) {
					Provider c = (Provider) element;
					return c.getName();
				}
				return super.getText(element);
			}

		});

		tvc = new TableViewerColumn(providersTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("N°.Tél");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Provider) {
					Provider c = (Provider) element;
					return c.getPhoneNumber();
				}
				return super.getText(element);
			}

		});

		providersTable
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent e) {
						deleteProviderAction.setEnabled(!e.getSelection()
								.isEmpty());
					}
				});

		providersTable.setInput(StorageManager.getStorage().getClients());
		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("clients".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					providersTable.setInput(StorageManager.getStorage()
							.getProviders());
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);

		fillToolbar();

		providersTable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection ss = (StructuredSelection) e.getSelection();
				Provider p = (Provider) ss.getFirstElement();
				try {
					getSite().getPage().showView(ProviderSheetView.ID,
							p.getName() + "@" + p.getId(),
							IWorkbenchPage.VIEW_ACTIVATE);
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void makeActions() {
		createProviderAction = new Action("Nouveau fournisseur") {
			@Override
			public void run() {
				ProviderDialog.create(getViewSite().getShell());
			}
		};
		createProviderAction.setImageDescriptor(ResourceManager
				.getDescriptor("add_provider_16.png"));

		deleteProviderAction = new Action("Supprimer le fournisseur") {
			@Override
			public void run() {
				if (MessageDialog.openConfirm(rootComposite.getShell(),
						"Confirmation",
						"Voulez vous vraiment supprimer les fournisseurs ?")) {
					StorageManager.getStorage().removeProviders(
							UIUtils.getSelectedObjects(
									providersTable.getSelection(),
									Provider.class));
				}
			}
		};
		deleteProviderAction.setImageDescriptor(ResourceManager
				.getDescriptor("delete_provider_16.png"));
		deleteProviderAction.setEnabled(false);
	}

	private void fillToolbar() {
		if (StorageManager.isEditable()) {
			IToolBarManager tbm = getViewSite().getActionBars()
					.getToolBarManager();
			tbm.add(createProviderAction);
			tbm.add(deleteProviderAction);
		}
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public void setFocus() {
		providersTable.getTable().setFocus();
	}

}
