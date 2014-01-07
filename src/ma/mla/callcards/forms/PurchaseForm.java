package ma.mla.callcards.forms;

import java.util.HashMap;
import java.util.Map;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.model.Purchase;
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

public class PurchaseForm extends BaseForm {

	private DateTime dateCmp;
	private ComboViewer providerCombo;
	private Provider provider;
	private Text totalText;
	private ProductsInput productsComposite;
	private Map<Product, Integer> availableProducts = new HashMap<Product, Integer>();

	public PurchaseForm(Composite parent) {
		super(parent);

		dateCmp = UIUtils.newDate(this, "Date");
		providerCombo = UIUtils.newCombo(this, "Fournisseur", StorageManager
				.getStorage().getProviders(), new Acceptor<Provider>() {

			@Override
			public void accept(Provider p) {
				provider = p;
			}
		});

		totalText = UIUtils.newText(this, "Total");
		totalText.setEditable(false);
		createProductsComp();

		availableProducts.putAll(StorageManager.getStorage()
				.getAvailableProducts());
	}

	private void createProductsComp() {
		Section section = new Section(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		section.setText("Produits");
		productsComposite = new ProductsInput(section, false) {
			@Override
			protected void productSelectionChanged() {
				double total = 0.;
				for (ProductSet ps : getSelectedProducts()) {
					total += ps.getCost();
				}
				totalText.setText(DataUtils.roundString(total));
			}
		};
		productsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		section.setClient(productsComposite);
	}

	public void readData(Purchase purchase) {
		providerCombo.setSelection(new StructuredSelection(purchase
				.getProvider()));
		DataUtils.setDate(purchase.getDate(), dateCmp);
	}

	public void writeData(Purchase p) {
		p.setDate(DataUtils.getDate(dateCmp));
		p.setProvider(provider);
		p.setProducts(productsComposite.getSelectedProducts());
	}

	public boolean isValid() {
		if (provider == null) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Veuillez saisir le fournisseur");
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
