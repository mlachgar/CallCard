package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.Expense;
import ma.mla.callcards.model.Pay;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.ProviderPay;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;
import ma.mla.callcards.utils.SectionRelayouter;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

public class FundView extends DateFilteredView {

	public static final String ID = "ma.mla.view.fund";

	private Composite rootComposite;
	private TableViewer clientPaysTable;
	private TableViewer providerPaysTable;
	private TableViewer expensesTable;
	private Text totalClientPaysText;
	private Text totalProviderPaysText;
	private Text totalExpensesText;
	private Action removePayAction;

	private PropertyChangeListener pcl;

	private Section clientPaysSection;

	private Section expensesSection;

	private Section providerPaysSection;

	@Override
	public void createContent(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, true));
		createSummaryView(rootComposite);
		clientPaysTable = createClientPaysViewer(rootComposite);
		providerPaysTable = creatProviderPaysViewer(rootComposite);
		expensesTable = createExpensesViewer(rootComposite);

		clientPaysTable
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (!event.getSelection().isEmpty()) {
							providerPaysTable.setSelection(null);
							expensesTable.setSelection(null);
						}
						removePayAction.setEnabled(hasSelection());
					}
				});

		providerPaysTable
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (!event.getSelection().isEmpty()) {
							clientPaysTable.setSelection(null);
							expensesTable.setSelection(null);
						}
						removePayAction.setEnabled(hasSelection());
					}
				});

		expensesTable
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (!event.getSelection().isEmpty()) {
							clientPaysTable.setSelection(null);
							providerPaysTable.setSelection(null);
						}
						removePayAction.setEnabled(hasSelection());
					}
				});

		updateUI();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("clientPays".equals(e.getPropertyName())) {
					updateClientsUI();
				} else if ("providerPays".equals(e.getPropertyName())) {
					updateProvidersUI();
				} else if ("expenses".equals(e.getPropertyName())) {
					updateExpensesUI();
				} else if ("*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);

		makeActions();
		if (StorageManager.isEditable()) {
			getViewSite().getActionBars().getToolBarManager()
					.add(removePayAction);
		}
	}

	private boolean hasSelection() {
		return !clientPaysTable.getSelection().isEmpty()
				|| !providerPaysTable.getSelection().isEmpty()
				|| !expensesTable.getSelection().isEmpty();
	}

	private void makeActions() {
		removePayAction = new Action("Supprimer",
				ResourceManager.getDescriptor("remove_pay_16.png")) {
			@Override
			public void run() {
				List<Pay> pays = UIUtils.getSelectedObjects(
						clientPaysTable.getSelection(), Pay.class);
				pays.addAll(UIUtils.getSelectedObjects(
						providerPaysTable.getSelection(), Pay.class));
				List<Expense> expenses = UIUtils.getSelectedObjects(
						expensesTable.getSelection(), Expense.class);
				if (!pays.isEmpty() || !expenses.isEmpty()) {
					if (MessageDialog
							.openConfirm(rootComposite.getShell(),
									"Confirmation",
									"Voulez vous vraiment supprimer les éléments séléctionnés ?")) {
						if (!pays.isEmpty()) {
							StorageManager.getStorage().removePays(pays);
						}
						if (!expenses.isEmpty()) {
							StorageManager.getStorage()
									.removeExpenses(expenses);
						}
					}
				}

			}
		};
		removePayAction.setEnabled(false);
	}

	private void createSummaryView(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		section.setText("Synthèse");
		section.setExpanded(true);
		final Composite cmp = new Composite(section, SWT.NONE);
		cmp.setLayout(new GridLayout(6, false));
		section.setClient(cmp);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		totalClientPaysText = FormUtils.newInfoText(cmp, "Total recette",
				SWT.COLOR_DARK_GREEN);

		totalProviderPaysText = FormUtils.newInfoText(cmp, "Total versements",
				SWT.COLOR_RED);

		totalExpensesText = FormUtils.newInfoText(cmp, "Total dépenses",
				SWT.COLOR_RED);
	}

	protected void updateUI() {
		updateClientsUI();
		updateProvidersUI();
		updateExpensesUI();
	}

	private void updateClientsUI() {
		List<ClientPay> clientPays = DataUtils.filter(StorageManager
				.getStorage().getClientPays(), fromDate, toDate);

		clientPaysTable.setInput(clientPays);
		double totalClients = 0.0;
		for (ClientPay cp : clientPays) {
			totalClients += cp.getAmount();
		}
		totalClientPaysText.setText(DataUtils.roundString(totalClients));
	}

	private void updateProvidersUI() {
		List<ProviderPay> providerPays = DataUtils.filter(StorageManager
				.getStorage().getProviderPays(), fromDate, toDate);
		providerPaysTable.setInput(providerPays);
		double totalProviders = 0.0;
		for (ProviderPay pp : providerPays) {
			totalProviders += pp.getAmount();
		}
		totalProviderPaysText.setText(DataUtils.roundString(totalProviders));
	}

	private void updateExpensesUI() {
		List<Expense> expenses = DataUtils.filter(StorageManager.getStorage()
				.getExpenses(), fromDate, toDate);
		expensesTable.setInput(expenses);
		double totalExpenses = 0.0;
		for (Expense ex : expenses) {
			totalExpenses += ex.getAmount();
		}
		this.totalExpensesText.setText(DataUtils.roundString(totalExpenses));
	}

	private TableViewer createClientPaysViewer(Composite parent) {
		clientPaysSection = new Section(parent, Section.TITLE_BAR
				| Section.TWISTIE);
		clientPaysSection.addExpansionListener(new SectionRelayouter());
		clientPaysSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		clientPaysSection.setText("Paiements clients");
		clientPaysSection.setExpanded(true);
		TableViewer viewer = new TableViewer(clientPaysSection, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = viewer.getTable();
		clientPaysSection.setClient(table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(DataUtils.DATEABLE_SORTER);

		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientPay) {
					ClientPay p = (ClientPay) element;
					return DataUtils.DATE_FORMAT.format(p.getDate());
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(viewer, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Client");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientPay) {
					ClientPay p = (ClientPay) element;
					return p.getClient().getName();
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(viewer, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Montant");
		col.setWidth(140);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientPay) {
					ClientPay cp = (ClientPay) element;
					return DataUtils.roundString(cp.getAmount());
				}
				return "";
			}
		});

		return viewer;
	}

	private TableViewer createExpensesViewer(Composite parent) {
		expensesSection = new Section(parent, Section.TITLE_BAR
				| Section.TWISTIE);
		expensesSection.addExpansionListener(new SectionRelayouter());
		expensesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		expensesSection.setText("Dépenses");
		expensesSection.setExpanded(true);
		TableViewer viewer = new TableViewer(expensesSection, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = viewer.getTable();
		expensesSection.setClient(table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(DataUtils.DATEABLE_SORTER);

		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Expense) {
					Expense ex = (Expense) element;
					return DataUtils.DATE_FORMAT.format(ex.getDate());
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(viewer, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Montant");
		col.setWidth(140);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Expense) {
					Expense ex = (Expense) element;
					return DataUtils.roundString(ex.getAmount());
				}
				return "";
			}
		});

		return viewer;
	}

	private TableViewer creatProviderPaysViewer(Composite parent) {
		providerPaysSection = new Section(parent, Section.TITLE_BAR
				| Section.TWISTIE);
		providerPaysSection.addExpansionListener(new SectionRelayouter());
		providerPaysSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));
		providerPaysSection.setText("Paiements fournisseurs");
		TableViewer viewer = new TableViewer(providerPaysSection, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = viewer.getTable();
		providerPaysSection.setClient(table);
		providerPaysSection.setExpanded(true);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(DataUtils.DATEABLE_SORTER);

		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProviderPay) {
					ProviderPay p = (ProviderPay) element;
					return DataUtils.DATE_FORMAT.format(p.getDate());
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(viewer, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Fournisseur");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProviderPay) {
					ProviderPay p = (ProviderPay) element;
					return p.getProvider().getName();
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(viewer, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Montant");
		col.setWidth(140);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProviderPay) {
					ProviderPay cp = (ProviderPay) element;
					return DataUtils.roundString(cp.getAmount());
				}
				return "";
			}
		});

		return viewer;
	}

	@Override
	public void setFocus() {
		clientPaysTable.getTable().setFocus();
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		List<PrintableControl> list = new ArrayList<PrintableControl>();
		if (clientPaysSection.isExpanded()) {
			list.add(new PrintableControl(clientPaysTable,
					"Liste des paiements des clients"));
		}
		if (providerPaysSection.isExpanded()) {
			list.add(new PrintableControl(providerPaysTable,
					"Liste des versements founisseurs"));
		}
		if (expensesSection.isExpanded()) {
			list.add(new PrintableControl(expensesTable, "Liste des dépenses"));
		}
		return list;
	}

	@Override
	public String getPrintTitle() {
		return "Opérations de la caisse";
	}

}
