package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.actions.PdfExportAction;
import ma.mla.callcards.actions.PrintAction;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.ClientDialog;
import ma.mla.callcards.dialogs.ProviderDialog;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.PrintableView;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.utils.SafePropertyListener;
import ma.mla.callcards.utils.SectionRelayouter;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

public class BookView extends ViewPart implements PrintableView {

	public static final String ID = "ma.mla.view.book";

	private Composite rootComposite;
	private TableViewer clientsTable;
	private TableViewer providersTable;
	private Action createClientAction;
	private Action deleteClientAction;
	private Action createProviderAction;
	private Action deleteProviderAction;
	private PropertyChangeListener pcl;

	private Section providerSection;

	private Section clientSection;

	@Override
	public void createPartControl(Composite parent) {
		makeActions();
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));

		makeActions();

		createClientsTable();
		createProvidersTable();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("clients".equals(e.getPropertyName())) {
					clientsTable.setInput(StorageManager.getStorage()
							.getClients());
				} else if ("providers".equals(e.getPropertyName())) {
					providersTable.setInput(StorageManager.getStorage()
							.getProviders());
				} else if ("*".equals(e.getPropertyName())) {
					clientsTable.setInput(StorageManager.getStorage()
							.getClients());
					providersTable.setInput(StorageManager.getStorage()
							.getProviders());
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);

		fillToolbar();

	}

	private void createProvidersTable() {
		providerSection = new Section(rootComposite, Section.TITLE_BAR
				| Section.TWISTIE);
		providerSection.addExpansionListener(new SectionRelayouter());
		providerSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		providerSection.setText("Fournisseurs");
		providerSection.setExpanded(true);
		providersTable = new TableViewer(providerSection, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = providersTable.getTable();
		providerSection.setClient(table);
		providerSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
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

		tvc = new TableViewerColumn(providersTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Adresse");
		col.setWidth(200);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Provider) {
					Provider c = (Provider) element;
					return c.getAddress();
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

		providersTable.setInput(StorageManager.getStorage().getProviders());

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

	private void createClientsTable() {
		clientSection = new Section(rootComposite, Section.TITLE_BAR
				| Section.TWISTIE);
		clientSection.addExpansionListener(new SectionRelayouter());
		clientSection
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		clientSection.setText("Clients");
		clientSection.setExpanded(true);

		clientsTable = new TableViewer(clientSection, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = clientsTable.getTable();
		clientSection.setClient(table);
		clientSection
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		clientsTable.setContentProvider(new ArrayContentProvider());

		TableViewerColumn tvc = new TableViewerColumn(clientsTable, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Nom");
		col.setWidth(180);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Client) {
					Client c = (Client) element;
					return c.getName();
				}
				return super.getText(element);
			}

		});

		tvc = new TableViewerColumn(clientsTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("N°.Tél");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Client) {
					Client c = (Client) element;
					return c.getPhoneNumber();
				}
				return super.getText(element);
			}

		});

		tvc = new TableViewerColumn(clientsTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Adresse");
		col.setWidth(200);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Client) {
					Client c = (Client) element;
					return c.getAddress();
				}
				return super.getText(element);
			}

		});

		clientsTable
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent e) {
						deleteClientAction.setEnabled(!e.getSelection()
								.isEmpty());
					}
				});

		clientsTable.setInput(StorageManager.getStorage().getClients());
		clientsTable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection ss = (StructuredSelection) e.getSelection();
				Client c = (Client) ss.getFirstElement();
				try {
					getSite().getPage().showView(ClientSheetView.ID,
							c.getName() + "@" + c.getId(),
							IWorkbenchPage.VIEW_ACTIVATE);
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void makeActions() {
		createClientAction = new Action("Nouveau client") {
			@Override
			public void run() {
				ClientDialog.create(getViewSite().getShell());
			}
		};
		createClientAction.setImageDescriptor(ResourceManager
				.getDescriptor("add_client_16.png"));

		deleteClientAction = new Action("Supprimer le client") {
			@Override
			public void run() {
				if (MessageDialog.openConfirm(rootComposite.getShell(),
						"Confirmation",
						"Voulez vous vraiment supprimer les clients ?")) {
					StorageManager.getStorage().removeClients(
							UIUtils.getSelectedObjects(
									clientsTable.getSelection(), Client.class));
				}
			}
		};
		deleteClientAction.setImageDescriptor(ResourceManager
				.getDescriptor("delete_client_16.png"));
		deleteClientAction.setEnabled(false);

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
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		if (StorageManager.isEditable()) {
			tbm.add(createClientAction);
			tbm.add(deleteClientAction);
			tbm.add(new Separator());
			tbm.add(createProviderAction);
			tbm.add(deleteProviderAction);
			tbm.add(new Separator());
		}
		tbm.add(new PrintAction(this));
		tbm.add(new PdfExportAction(this));
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public void setFocus() {
		clientsTable.getTable().setFocus();
	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		List<PrintableControl> list = new ArrayList<PrintableControl>();
		if (clientSection.isExpanded()) {
			list.add(new PrintableControl(clientsTable, "Liste des clients"));
		}
		if (providerSection.isExpanded()) {
			list.add(new PrintableControl(providersTable,
					"Liste des founisseurs"));
		}
		return list;
	}

	@Override
	public String getPrintTitle() {
		return "Carnet d'adresses";
	}

}
