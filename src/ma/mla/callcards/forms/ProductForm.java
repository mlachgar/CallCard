package ma.mla.callcards.forms;

import ma.mla.callcards.model.Operator;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ProductForm extends BaseForm {

	private Text nameText;
	private ComboViewer operatorCombo;
	private Text priceText;
	private Text saleText;
	private Text purchaseText;

	private double price;
	private double purchaseValue;
	private double saleValue;
	private boolean isCard;

	public ProductForm(Composite parent, final boolean isCard) {
		super(parent);
		this.isCard = isCard;
		operatorCombo = UIUtils.newCombo(this, "Opérateur", SWT.BORDER
				| SWT.READ_ONLY);
		operatorCombo.setInput(Operator.values());
		operatorCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {

					}
				});
		nameText = UIUtils.newText(this, "Nom");
		if (isCard) {
			priceText = UIUtils.newDoubleText(this, "Valeur",
					new Acceptor<Double>() {

						@Override
						public void accept(Double e) {
							price = e != null ? e.doubleValue() : 0.0;
						}
					});
			purchaseText = UIUtils.newDoubleText(this, "% Achat", 0.0, 100.0,
					new Acceptor<Double>() {

						@Override
						public void accept(Double e) {
							purchaseValue = e != null ? e.doubleValue() : 0.0;
						}
					});
			saleText = UIUtils.newDoubleText(this, "% Vente", 0.0, 100.0,
					new Acceptor<Double>() {

						@Override
						public void accept(Double e) {
							saleValue = e != null ? e.doubleValue() : 0.0;
						}
					});
		} else {
			purchaseText = UIUtils.newDoubleText(this, "Prix Achat", 0.0,
					Double.NaN, new Acceptor<Double>() {

						@Override
						public void accept(Double e) {
							purchaseValue = e != null ? e.doubleValue() : 0.0;
						}
					});
			saleText = UIUtils.newDoubleText(this, "Prix Vente", 0.0,
					Double.NaN, new Acceptor<Double>() {

						@Override
						public void accept(Double e) {
							saleValue = e != null ? e.doubleValue() : 0.0;
						}
					});
		}
	}

	public void readData(Product product) {
		Operator op = product.getOperator();
		operatorCombo.setSelection(op != null ? new StructuredSelection(product
				.getOperator()) : null);
		nameText.setText(product.getName());
		if (isCard) {
			priceText.setText(String.valueOf(product.getPrice()));
		}
		purchaseText.setText(String.valueOf(product.getDefaultPurchaseValue()));
		saleText.setText(String.valueOf(product.getDefaultSaleValue()));
	}

	public void writeData(Product product) {
		product.setOperator(UIUtils.getFirstSelected(
				operatorCombo.getSelection(), Operator.class));
		product.setName(nameText.getText());
		if (isCard) {
			product.setPrice(price);
		}
		product.setDefaultPurchaseValue(purchaseValue);
		product.setDefaultSaleValue(saleValue);
	}

	private String getEmptyFields() {
		StringBuilder sb = new StringBuilder();
		if (operatorCombo.getSelection().isEmpty()) {
			sb.append("Opérateur\n");
		}
		if (nameText.getText().isEmpty()) {
			sb.append("Nom\n");
		}
		if (isCard) {
			if (priceText.getText().isEmpty()) {
				sb.append("Valeur\n");
			}
			if (purchaseText.getText().isEmpty()) {
				sb.append("% Achat\n");
			}
			if (saleText.getText().isEmpty()) {
				sb.append("% Vente\n");
			}
		} else {
			if (purchaseText.getText().isEmpty()) {
				sb.append("Prix Achat\n");
			}
			if (saleText.getText().isEmpty()) {
				sb.append("Prix Vente\n");
			}
		}
		return sb.toString();
	}

	public boolean isValid() {
		String fields = getEmptyFields();
		if (!fields.isEmpty()) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Veulliez saisir les champs suivant :\n" + fields);
			return false;
		}
		return true;
	}

}
