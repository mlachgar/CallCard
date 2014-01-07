package ma.mla.callcards.forms;

import java.util.HashMap;
import java.util.Map;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

public class SaleForm extends BaseForm {

	private DateTime dateCmp;
	private ComboViewer clientCombo;
	private Client client;
	private Text totalText;
	private Text restText;
	private ProductsInput productsComposite;
	private Map<Product, Integer> availableProducts = new HashMap<Product, Integer>();

	private double total = 0.0;
	private double payAmount = 0.0;

	public SaleForm(Composite parent) {
		super(parent);

		dateCmp = UIUtils.newDate(this, "Date");
		clientCombo = UIUtils.newCombo(this, "Client", StorageManager
				.getStorage().getClients(), new Acceptor<Client>() {

			@Override
			public void accept(Client cl) {
				client = cl;
			}
		});

		totalText = UIUtils.newText(this, "Total");
		totalText.setEditable(false);
		UIUtils.newDoubleText(this, "Avance", 0.0, Double.NaN,
				new Acceptor<Double>() {

					@Override
					public void accept(Double d) {
						payAmount = d != null ? d.doubleValue() : 0.0;
						restText.setText(DataUtils.roundString(total
								- payAmount));
					}
				});
		restText = UIUtils.newText(this, "Reste");
		restText.setEditable(false);

		createProductsComp();

		availableProducts.putAll(StorageManager.getStorage()
				.getAvailableProducts());
	}

	private void createProductsComp() {
		Section section = new Section(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		section.setText("Produits");
		productsComposite = new ProductsInput(section, true) {

			@Override
			protected void productSelectionChanged() {
				total = 0.;
				for (ProductSet ps : getSelectedProducts()) {
					total += ps.getCost();
				}
				totalText.setText(DataUtils.roundString(total));
				restText.setText(DataUtils.roundString(total - payAmount));
			}
		};
		productsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		section.setClient(productsComposite);
	}

	public void readData(Sale sale) {
		clientCombo.setSelection(new StructuredSelection(sale.getClient()));
		DataUtils.setDate(sale.getDate(), dateCmp);
	}

	public void writeData(Sale sale) {
		sale.setDate(DataUtils.getDate(dateCmp));
		sale.setClient(client);
		sale.setSaleProducts(productsComposite.getSelectedProducts());
		sale.setPayed(payAmount);
	}

	public boolean isValid() {
		if (client == null) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Veuillez saisir le client");
			return false;
		}
		if (productsComposite.getSelectedProducts().isEmpty()) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Veuillez choisir des produits");
			return false;
		}
		return true;
	}

}
