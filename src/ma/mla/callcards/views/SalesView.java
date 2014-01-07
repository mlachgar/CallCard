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
import ma.mla.callcards.model.Sale;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.Section;

public class SalesView extends DateFilteredView {

	public static final String ID = "ma.mla.view.sales";

	private Composite rootComposite;
	private TreeViewer salesTree;
	private Text salesText;
	private Text gainText;
	private PropertyChangeListener pcl;

	private Action removSaleAction;

	@Override
	public void createContent(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));

		createFinacialView(rootComposite);
		createSalesTree(rootComposite);
		contributeToolbar();

		updateUI();
		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("sales".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
	}

	private void contributeToolbar() {
		removSaleAction = new Action("Supprimer la vente",
				ResourceManager.getDescriptor("remove_sale_16.png")) {
			@Override
			public void run() {
				if (MessageDialog.openConfirm(getViewSite().getShell(),
						"Confirmation",
						"Voulez-vous vraiment supprimer les ventes ?")) {
					StorageManager.getStorage().removeSales(
							UIUtils.getSelectedObjects(
									salesTree.getSelection(), Sale.class));
				}
			}
		};
		removSaleAction.setEnabled(false);
		if (StorageManager.isEditable()) {
			getViewSite().getActionBars().getToolBarManager()
					.add(removSaleAction);
		}
	}

	private void createFinacialView(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, false));
		root.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		salesText = FormUtils.newInfoText(root, "Total ventes");
		gainText = FormUtils.newInfoText(root, "Total bénéfice");
	}

	private void createSalesTree(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("Liste des ventes");
		salesTree = new TreeViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		final Tree tree = salesTree.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		section.setClient(tree);
		salesTree.setContentProvider(new ITreeContentProvider() {

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
				if (o instanceof Sale) {
					Sale s = (Sale) o;
					return s.getSaleProducts().toArray();
				}
				return null;
			}
		});

		salesTree.setSorter(new ViewerSorter() {
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

		TreeViewerColumn tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		TreeColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale s = (Sale) element;
					return DataUtils.DATE_FORMAT.format(s.getDate());
				}
				return "";
			}

		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Client");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale s = (Sale) element;
					return s.getClient().getName();
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
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

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Quantité");
		col.setWidth(80);
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

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
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

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
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

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Total");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getTotal());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Payé");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getPayed());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Reste");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getTotal() - cs.getPayed());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Bénéfice");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getTotal() - cs.getCost());
				}
				return "";
			}

			@Override
			public Color getForeground(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					if (cs.getTotal() < cs.getCost()) {
						return tree.getDisplay().getSystemColor(SWT.COLOR_RED);
					}
				}
				return tree.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
			}
		});

		salesTree
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						List<Sale> selectedSales = UIUtils.getSelectedObjects(
								event.getSelection(), Sale.class);
						removSaleAction.setEnabled(!selectedSales.isEmpty());

					}
				});

	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	protected void updateUI() {
		List<Sale> sales = DataUtils.filter(StorageManager.getStorage()
				.getSales(), fromDate, toDate);
		salesTree.setInput(sales);
		double totalSales = 0.;
		double totalCost = 0.0;
		for (Sale s : sales) {
			totalSales += s.getTotal();
			totalCost += s.getCost();
		}
		salesText.setText(DataUtils.roundString(totalSales));
		gainText.setText(DataUtils.roundString(totalSales - totalCost));
		if (totalSales < totalCost) {
			gainText.setForeground(gainText.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		} else {
			gainText.setForeground(gainText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}
	}

	@Override
	public void setFocus() {

	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		return Arrays
				.asList(new PrintableControl(salesTree, "Liste des ventes"));
	}

	@Override
	public String getPrintTitle() {
		return "Liste des ventes";
	}

}
