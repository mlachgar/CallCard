package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.Purchase;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.Section;

public class PurchasesView extends DateFilteredView {

	public static final String ID = "ma.mla.view.purchases";

	private Composite rootComposite;
	private TreeViewer pursharesTree;
	private Text totalText;
	private Action removePurchaseAction;
	private PropertyChangeListener pcl;

	@Override
	public void createContent(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));

		createFinacialView(rootComposite);
		createPurchasesTree(rootComposite);
		contributeToolbar();
		updateUI();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("purchases".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);

	}

	private void contributeToolbar() {
		removePurchaseAction = new Action("Supprimer la vente",
				ResourceManager.getDescriptor("remove_purchase_16.png")) {
			@Override
			public void run() {
				try {
					if (MessageDialog.openConfirm(getViewSite().getShell(),
							"Confirmation",
							"Voulez-vous vraiment supprimer les achats ?")) {
						StorageManager.getStorage().removePurchases(
								UIUtils.getSelectedObjects(
										pursharesTree.getSelection(),
										Purchase.class));
					}
				} catch (Exception e) {
					MessageDialog.openError(getViewSite().getShell(), "Erreur",
							e.getMessage());
				}
			}
		};
		removePurchaseAction.setEnabled(false);
		if (StorageManager.isEditable()) {
			getViewSite().getActionBars().getToolBarManager()
					.add(removePurchaseAction);
		}
	}

	private void createPurchasesTree(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("Liste des achats");

		pursharesTree = new TreeViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		Tree tree = pursharesTree.getTree();
		section.setClient(tree);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		pursharesTree.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof ProductSet) {
					ProductSet ps1 = (ProductSet) e1;
					ProductSet ps2 = (ProductSet) e2;
					return (int) (ps2.getCost() - ps1.getCost());
				}
				return DataUtils.DATEABLE_SORTER.compare(viewer, e1, e2);
			}
		});

		pursharesTree.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean hasChildren(Object o) {
				return !(o instanceof ProductSet);
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
				if (o instanceof Purchase) {
					Purchase p = (Purchase) o;
					return p.getProducts().toArray();
				}
				return null;
			}
		});

		TreeViewerColumn tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		TreeColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Purchase) {
					Purchase p = (Purchase) element;
					return DataUtils.DATE_FORMAT.format(p.getDate());
				}
				return "";
			}

		});

		tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Fournisseur");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Purchase) {
					Purchase p = (Purchase) element;
					return p.getProvider().getName();
				}
				return "";
			}

		});

		tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Produit");
		col.setWidth(140);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return ps.getProduct().getName();
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Quantité");
		col.setWidth(60);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return String.valueOf(ps.getCount());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Prix Unit");
		col.setWidth(60);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return DataUtils.roundString(ps.getUnitPrice());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Prix");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return DataUtils.roundString(ps.getCost());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(pursharesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Total");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Purchase) {
					Purchase cs = (Purchase) element;
					return DataUtils.roundString(cs.getTotal());
				}
				return "";
			}
		});

		pursharesTree
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						List<Purchase> selectedPurchases = UIUtils
								.getSelectedObjects(event.getSelection(),
										Purchase.class);
						removePurchaseAction.setEnabled(!selectedPurchases
								.isEmpty());

					}
				});
	}

	private void createFinacialView(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		section.setText("Synthèse");
		final Composite cmp = new Composite(section, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		section.setClient(cmp);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		totalText = FormUtils.newInfoText(cmp, "Total Achats");
	}

	protected void updateUI() {
		List<Purchase> purchases = DataUtils.filter(StorageManager.getStorage()
				.getPurchases(), fromDate, toDate);
		pursharesTree.setInput(purchases);
		double totalOut = 0.;
		for (Purchase p : purchases) {
			totalOut += p.getTotal();
		}
		totalText.setText(DataUtils.roundString(totalOut));
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public void setFocus() {
		pursharesTree.getTree().setFocus();
	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		return Arrays.asList(new PrintableControl(pursharesTree,
				"Liste des achats"));
	}

	@Override
	public String getPrintTitle() {
		return "Liste des achats";
	}

}
