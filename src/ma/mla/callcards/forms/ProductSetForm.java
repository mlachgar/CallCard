package ma.mla.callcards.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.editor.DoubleEditingSupoort;
import ma.mla.callcards.editor.TextEditingSupoort;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.ProductType;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;

public class ProductSetForm extends Composite {

	private List<ProductSet> products = new ArrayList<ProductSet>();
	private Set<Product> choosenProdcuts = new HashSet<Product>();
	private TableViewer table;

	public ProductSetForm(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		setUpToolbar();

		table = new TableViewer(this, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		Table t = table.getTable();
		t.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		t.setLinesVisible(true);
		t.setHeaderVisible(true);
		table.setContentProvider(new ArrayContentProvider());

		TableViewerColumn tvc = new TableViewerColumn(table, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Produit");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					if (ps.getProduct() != null) {
						return ps.getProduct().getName();
					}
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Quantité");
		col.setWidth(80);
		tvc.setEditingSupport(new TextEditingSupoort(table) {

			@Override
			protected void setValue(Object element, Object value) {

			}

			@Override
			protected Object getValue(Object element) {
				return null;
			}
		});
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

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("%/Prix Unit");
		col.setWidth(80);
		tvc.setEditingSupport(new DoubleEditingSupoort(table) {

			@Override
			public boolean isValid(Object element, Double value) {
				ProductSet ps = getProductSet(element);
				if (ps != null && ps.getProduct() != null) {
					if (ps.getProduct().getType() == ProductType.CARD) {
						return value >= 0.0 && value <= 100.0;
					}
					return value >= 0.0;
				}
				return false;
			}

			@Override
			protected boolean canEdit(Object element) {
				ProductSet ps = getProductSet(element);
				return ps != null && ps.getProduct() != null;
			}

			@Override
			protected Object getValue(Object element) {
				ProductSet ps = getProductSet(element);
				if (ps != null && ps.getProduct() != null) {
					return Double.valueOf(ps.getProduct().getValue(
							ps.getUnitPrice()));
				}
				return null;
			}

			@Override
			protected void setValue(Object element, Object value) {

			}

		});
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					if (ps.getProduct() != null) {
						return DataUtils.roundString(ps.getProduct().getValue(
								ps.getUnitPrice()));
					}
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("S.Total");
		col.setWidth(80);
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

		table.setInput(products);
	}

	private ProductSet getProductSet(Object e) {
		if (e instanceof ProductSet) {
			return (ProductSet) e;
		}
		return null;
	}

	private ProductSet newProductSet() {
		Product product = null;
		for (Product p : StorageManager.getStorage().getProducts()) {
			product = p;
			if (!choosenProdcuts.contains(p)) {
				choosenProdcuts.add(p);
				break;
			}
		}
		ProductSet set = new ProductSet();
		set.setProduct(product);
		if (product != null) {
			set.setUnitPrice(product.getUnitPrice(product
					.getDefaultPurchaseValue()));
		}
		return set;
	}

	private void setUpToolbar() {
		ToolBarManager m = new ToolBarManager();
		m.add(new Action("Ajouter", ResourceManager
				.getDescriptor("plus_16.png")) {
			@Override
			public void run() {
				products.add(newProductSet());
				table.refresh();
			}
		});

		m.add(new Action("Supprimer", ResourceManager
				.getDescriptor("minus_16.png")) {
			@Override
			public void run() {
				List<ProductSet> selectedProducts = UIUtils.getSelectedObjects(
						table.getSelection(), ProductSet.class);
				products.removeAll(selectedProducts);
				for (ProductSet set : selectedProducts) {
					choosenProdcuts.remove(set.getProduct());
				}
				table.refresh();
			}
		});

		ToolBar toolbar = m.createControl(this);
		toolbar.setLayoutData(new GridData(SWT.TRAIL, SWT.TOP, true, false));
	}

	public void setProducts(Collection<ProductSet> products) {
		this.products.clear();
		choosenProdcuts.clear();
		this.products.addAll(products);
		for (ProductSet set : products) {
			if (set.getProduct() != null) {
				choosenProdcuts.add(set.getProduct());
			}
		}
		table.refresh();
	}

	protected void productSelectionChanged() {

	}

	public List<ProductSet> getProducts() {
		return products;
	}

}
