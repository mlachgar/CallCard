package ma.mla.callcards.forms;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductType;
import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;
import ma.mla.callcards.utils.ValidatorAcceptor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public abstract class ProductSetRow {

	private ComboViewer productCombo;
	private Text totalLabel;
	private Spinner countSpinner;
	private Text valueText;
	private Product product;
	private double value;
	private ToolBar toolbar;

	abstract protected void rowRemoved();

	public ProductSetRow(final Composite parent) {
		productCombo = UIUtils.newCombo(parent, null, StorageManager
				.getStorage().getProducts(), new Acceptor<Product>() {
			public void accept(Product p) {
				product = p;
				totalLabel.setText(String.valueOf(caclculateTotal(value)));
			};
		});

		productCombo.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, false, false));
		countSpinner = new Spinner(parent, SWT.BORDER);
		countSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		countSpinner.setMinimum(0);
		countSpinner.setMaximum(Integer.MAX_VALUE);
		countSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				valueText.setEnabled(countSpinner.getSelection() > 0);
				totalLabel.setText(DataUtils
						.roundString(caclculateTotal(value)));
				selectionChanged();
			}
		});

		valueText = UIUtils.newDoubleText(parent, new ValidatorAcceptor<Double>() {

			@Override
			public boolean isValid(Double d) {
				if (product.getType() == ProductType.CARD) {
					return d >= 0.0 && d <= 100.0;
				}
				return d >= 0.0;
			}

			@Override
			public void accept(Double v) {
				value = v != null ? v.doubleValue() : 0.0;
				totalLabel.setText(DataUtils
						.roundString(caclculateTotal(value)));
				selectionChanged();
			}
		});
		valueText.setLayoutData(new GridData(100, SWT.DEFAULT));

		totalLabel = new Text(parent, SWT.BORDER);
		totalLabel.setEditable(false);
		totalLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));

		ToolBarManager m = new ToolBarManager();
		m.add(new Action("Supprimer", ResourceManager
				.getDescriptor("minus_16.png")) {
			@Override
			public void run() {
				rowRemoved();
				dispose();
				parent.getParent().layout(true, true);
			}
		});

		toolbar = m.createControl(parent);
		toolbar.setLayoutData(gridData(30));

	}

	private GridData gridData(int width) {
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd.widthHint = width;
		return gd;
	}

	protected void selectionChanged() {

	}

	public Product getProduct() {
		return product;
	}

	public void setData(Product product, double unitPrice, int available) {
		this.product = product;
		if (product != null) {
			this.value = product.getValue(unitPrice);
			productCombo.setSelection(new StructuredSelection(product));
		}
		valueText.setText(DataUtils.roundString(value));
		countSpinner.setSelection(available);
		totalLabel.setText(DataUtils.roundString(caclculateTotal(value)));
	}

	private double caclculateTotal(double percent) {
		return getUnitPrice() * getCount();
	}

	public int getCount() {
		return countSpinner.getSelection();
	}

	public double getUnitPrice() {
		if (product != null) {
			if (product.getType() == ProductType.CARD) {
				return product.getPrice() * (1. - (value / 100.));
			} else {
				return value;
			}
		}
		return 0.0;
	}

	public void dispose() {
		productCombo.getControl().dispose();
		countSpinner.dispose();
		valueText.dispose();
		totalLabel.dispose();
		toolbar.dispose();
	}
}
