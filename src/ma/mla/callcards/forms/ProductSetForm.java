package ma.mla.callcards.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.model.ProductSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

public class ProductSetForm extends Composite {

	private List<ProductSetRow> rows = new ArrayList<ProductSetRow>();

	public ProductSetForm(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(5, false));

		newLabel("Produit", 120);
		newLabel("Quantité", 80);
		newLabel("%/Prix Unit", 80);
		newLabel("S.Total", 80);

		ToolBarManager m = new ToolBarManager();
		m.add(new Action("Ajouter", ResourceManager
				.getDescriptor("plus_16.png")) {
			@Override
			public void run() {
				addRow();
				getParent().layout(true, true);
			}
		});

		ToolBar toolbar = m.createControl(this);
		toolbar.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false, false));
	}

	private ProductSetRow addRow() {
		ProductSetRow row = new ProductSetRow(this) {
			@Override
			protected void selectionChanged() {
				productSelectionChanged();
			}

			@Override
			protected void rowRemoved() {
				rows.remove(this);
			}
		};
		rows.add(row);
		return row;
	}

	public void setProducts(Collection<ProductSet> products) {
		for (ProductSet set : products) {
			ProductSetRow row = addRow();
			row.setData(set.getProduct(), set.getUnitPrice(), set.getCount());
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
		for (ProductSetRow row : rows) {
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
