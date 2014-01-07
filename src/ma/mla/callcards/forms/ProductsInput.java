package ma.mla.callcards.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ProductsInput extends Composite {

	private List<ProductRowInput> rows = new ArrayList<ProductRowInput>();

	public ProductsInput(Composite parent, final boolean isSale) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(5, false));

		newLabel("Produit", 120);
		newLabel("Disponible", 80);
		newLabel("Quantité", 80);
		newLabel("%/Prix Unit", 80);
		newLabel("S.Total", 80);

		if (isSale) {
			initForSale();
		} else {
			initForPurchase();
		}

	}

	private void initForSale() {
		Map<Product, Integer> available = StorageManager.getStorage()
				.getAvailableProducts();
		for (Product p : StorageManager.getStorage().getProducts()) {
			ProductRowInput row = new ProductRowInput(this, true) {
				@Override
				protected void selectionChanged() {
					productSelectionChanged();
				}
			};
			row.setData(p, p.getDefaultSaleValue(), available.get(p).intValue());
			rows.add(row);
		}
	}

	private void initForPurchase() {
		Map<Product, Integer> available = StorageManager.getStorage()
				.getAvailableProducts();
		for (Product p : StorageManager.getStorage().getProducts()) {
			ProductRowInput row = new ProductRowInput(this, false) {
				@Override
				protected void selectionChanged() {
					productSelectionChanged();
				}
			};
			row.setData(p, p.getDefaultPurchaseValue(), available.get(p)
					.intValue());
			rows.add(row);
		}
	}

	protected void productSelectionChanged() {

	}

	private void newLabel(String text, int width) {
		Label label = new Label(this, SWT.CENTER);
		label.setAlignment(SWT.CENTER);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, false, false);
		gd.widthHint = width;
		label.setLayoutData(gd);
		label.setText(text);
	}

	public List<ProductSet> getSelectedProducts() {
		List<ProductSet> sets = new ArrayList<ProductSet>();
		for (ProductRowInput row : rows) {
			if (row.getCount() > 0) {
				ProductSet set = new ProductSet();
				set.setProduct(row.getProduct());
				set.setCount(row.getCount());
				set.setUnitPrice(row.getUnitPrice());
				sets.add(set);
			}
		}
		return sets;
	}

}
