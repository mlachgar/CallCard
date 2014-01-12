package ma.mla.callcards.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.editor.ComboEditingSupport;
import ma.mla.callcards.editor.DoubleEditingSupport;
import ma.mla.callcards.editor.SpinnerEditingSupport;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.ProductType;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
		col.setWidth(140);
		tvc.setLabelProvider(new ProductSetLabelProbider(0));
		tvc.setEditingSupport(new ComboEditingSupport<Product>(table,
				StorageManager.getStorage().getProducts(), new LabelProvider() {
					public String getText(Object element) {
						if (element instanceof Product) {
							Product p = (Product) element;
							return p.getName();
						}
						return "";
					}
				}) {
			@Override
			protected Object getValue(Object element) {
				ProductSet ps = getProductSet(element);
				if (ps != null) {
					return ps.getProduct();
				}
				return null;
			}

			@Override
			protected void setValue(Object element, Object value) {
				ProductSet ps = getProductSet(element);
				if (ps != null) {
					Product p = (Product) value;
					ps.setProduct(p);
					ps.setUnitPrice(p.getUnitPrice(p.getDefaultPurchaseValue()));
					table.update(ps, null);
				}
			}

		});

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Quantité");
		col.setWidth(100);
		tvc.setLabelProvider(new ProductSetLabelProbider(1));
		tvc.setEditingSupport(new SpinnerEditingSupport(table, 0,
				Integer.MAX_VALUE) {

			@Override
			protected void setValue(Object element, Object value) {
				ProductSet ps = getProductSet(element);
				if (ps != null && value != null) {
					ps.setCount((Integer) value);
					table.update(ps, null);
				}
			}

			@Override
			protected Object getValue(Object element) {
				ProductSet ps = getProductSet(element);
				if (ps != null) {
					return Integer.valueOf(ps.getCount());
				}
				return null;
			}
		});

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("% / Prix Unit");
		col.setWidth(80);
		tvc.setLabelProvider(new ProductSetLabelProbider(2));
		tvc.setEditingSupport(new DoubleEditingSupport(table) {

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
				ProductSet ps = getProductSet(element);
				if (ps != null && ps.getProduct() != null) {
					ps.setUnitPrice(ps.getProduct()
							.getUnitPrice((Double) value));
					table.update(ps, null);
				}
			}

		});

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("S.Total");
		col.setWidth(100);
		tvc.setLabelProvider(new ProductSetLabelProbider(3));

		UIUtils.customizeTableEditing(table);
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
		toolbar.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, true, false));
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

	private static class ProductSetLabelProbider extends ColumnLabelProvider {
		int columnIndex;

		ProductSetLabelProbider(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof ProductSet) {
				ProductSet ps = (ProductSet) element;
				if (ps != null) {
					Product p = ps.getProduct();
					switch (columnIndex) {
					case 0:
						return p != null ? p.getName() : "";
					case 1:
						return String.valueOf(ps.getCount());
					case 2:
						return p != null ? DataUtils.roundString(ps
								.getProduct().getValue(ps.getUnitPrice())) : "";
					case 3:
						return DataUtils.roundString(ps.getCost());
					}
				}
			}
			return "";
		}
	}

}
