package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.actions.PdfExportAction;
import ma.mla.callcards.actions.PrintAction;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.dialogs.ProductDialog;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.PrintableView;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;
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
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class ProductsView extends ViewPart implements PrintableView {

	public static final String ID = "ma.mla.view.products";

	private Composite rootComposite;
	private TableViewer productsTable;

	private PropertyChangeListener pcl;

	private Action createProductAction;
	private Action createWalletAction;
	private Action editAction;
	private Action deleteProdustAction;
	private Action printAction;
	private Action exportAction;

	@Override
	public void createPartControl(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));
		productsTable = new TableViewer(rootComposite, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = productsTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);

		productsTable.setContentProvider(new ArrayContentProvider());
		productsTable.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Product) {
					Product p = (Product) element;
					return p.getName();
				}
				return super.getText(element);
			}
		});

		TableViewerColumn tvc = new TableViewerColumn(productsTable, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Nom");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Product) {
					Product p = (Product) element;
					return p.getName();
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(productsTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Opérateur");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Product) {
					Product p = (Product) element;
					return p.getOperator().getName();
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(productsTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Prix");
		col.setWidth(60);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Product) {
					Product p = (Product) element;
					return DataUtils.roundString(p.getPrice());
				}
				return "";
			}
		});

		productsTable.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				Product p1 = (Product) e1;
				Product p2 = (Product) e2;
				return DataUtils.compare(p1, p2);
			}
		});

		productsTable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				ProductDialog.edit(getViewSite().getShell(), UIUtils
						.getFirstSelected(productsTable.getSelection(),
								Product.class));
			}
		});

		productsTable
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent e) {
						boolean enabled = !e.getSelection().isEmpty();
						deleteProdustAction.setEnabled(enabled);
						editAction.setEnabled(enabled);
					}
				});
		productsTable.setInput(StorageManager.getStorage().getProducts());
		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("products".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					productsTable.setInput(StorageManager.getStorage()
							.getProducts());
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
		makeActions();
		fillToolbar();
	}

	private void fillToolbar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		if (StorageManager.isEditable()) {
			tbm.add(createProductAction);
			tbm.add(createWalletAction);
			tbm.add(editAction);
			tbm.add(deleteProdustAction);
			tbm.add(new Separator());
		}
		tbm.add(printAction);
		tbm.add(exportAction);
	}

	private void makeActions() {
		createProductAction = new Action("Nouveau carte",
				ResourceManager.getDescriptor("add_card_16.png")) {
			@Override
			public void run() {
				ProductDialog.create(getViewSite().getShell(), true);
			}
		};

		createWalletAction = new Action("Nouvelle pochette",
				ResourceManager.getDescriptor("add_bag_product.png")) {
			@Override
			public void run() {
				ProductDialog.create(getViewSite().getShell(), false);
			}
		};

		editAction = new Action("Modifier",
				ResourceManager.getDescriptor("edit_16.png")) {
			@Override
			public void run() {
				ProductDialog.edit(getViewSite().getShell(), UIUtils
						.getFirstSelected(productsTable.getSelection(),
								Product.class));
			}
		};
		editAction.setEnabled(false);

		deleteProdustAction = new Action("Supprimer le produit") {
			@Override
			public void run() {
				if (MessageDialog.openConfirm(rootComposite.getShell(),
						"Confirmation",
						"Voulez vous vraiment supprimer les produits ?")) {
					StorageManager.getStorage()
							.removeProducts(
									UIUtils.getSelectedObjects(
											productsTable.getSelection(),
											Product.class));
				}
			}
		};
		deleteProdustAction.setImageDescriptor(ResourceManager
				.getDescriptor("delete_card_16.png"));
		deleteProdustAction.setEnabled(false);

		printAction = new PrintAction(this);
		exportAction = new PdfExportAction(this);

	}

	@Override
	public void setFocus() {
		productsTable.getTable().setFocus();
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		return Arrays.asList(new PrintableControl(productsTable,
				"Liste des produits"));
	}

	@Override
	public String getPrintTitle() {
		return "Liste des produits";
	}

}
