package ma.mla.callcards.forms;

import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductType;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;
import ma.mla.callcards.utils.ValidatorAcceptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class ProductRowInput {

	private Text productLabel;
	private Text totalLabel;
	private Text availableLabel;
	private Spinner countSpinner;
	private Text percentText;
	private Product product;
	private double value;
	private boolean isSale;

	public ProductRowInput(Composite parent, boolean isSale) {
		this.isSale = isSale;
		productLabel = new Text(parent, SWT.BORDER);
		productLabel.setEditable(false);
		productLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		availableLabel = new Text(parent, SWT.BORDER);
		availableLabel.setEditable(false);
		availableLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		countSpinner = new Spinner(parent, SWT.BORDER);
		countSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		countSpinner.setMinimum(0);
		if (isSale) {
			countSpinner.setMaximum(0);
		} else {
			countSpinner.setMaximum(Integer.MAX_VALUE);
		}
		countSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				percentText.setEnabled(countSpinner.getSelection() > 0);
				totalLabel.setText(DataUtils
						.roundString(caclculateTotal(value)));
				selectionChanged();
			}
		});

		percentText = UIUtils.newDoubleText(parent, new ValidatorAcceptor<Double>() {

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
		percentText.setLayoutData(new GridData(100, SWT.DEFAULT));
		percentText.setEnabled(false);

		totalLabel = new Text(parent, SWT.BORDER);
		totalLabel.setEditable(false);
		totalLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));

	}

	protected void selectionChanged() {

	}

	public Product getProduct() {
		return product;
	}

	public void setData(Product product, double percent, int available) {
		this.product = product;
		productLabel.setText(product.getName());
		if (!isSale) {
			percentText.setText(String.valueOf(percent));
			totalLabel.setText(String.valueOf(caclculateTotal(value)));
			availableLabel.setText(String.valueOf(available));
		} else if (available > 0) {
			countSpinner.setMaximum(available);
			percentText.setText(String.valueOf(percent));
			availableLabel.setText(String.valueOf(available));
			totalLabel.setText(String.valueOf(caclculateTotal(value)));
		} else {
			percentText.setText(String.valueOf(percent));
			availableLabel.setText(String.valueOf(available));
			productLabel.setEnabled(false);
			percentText.setEnabled(false);
			availableLabel.setEnabled(false);
			countSpinner.setEnabled(false);
			totalLabel.setText("");
		}
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
}
