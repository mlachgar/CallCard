package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.Storage;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.PersonAmountComposite;
import ma.mla.callcards.forms.ProductSetForm;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.InitData;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InitDataDialog extends ScrolledDialog {

	private PersonAmountComposite<Client> clientCreditComp;
	private PersonAmountComposite<Provider> providerCreditComp;
	private ProductSetForm stockForm;
	private Text initialCashText;

	private InitData data = new InitData();

	InitDataDialog(Shell parentShell, InitData data) {
		super(parentShell);
		if (data != null) {
			this.data = data;
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 700);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Report année précédente");
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return super.createButton(parent, id, label, false);
	}

	@Override
	protected Control createScrolledContent(Composite parent) {
		Storage storage = StorageManager.getStorage();
		InitData initData = storage.getInitData();
		data.setInitialCash(initData.getInitialCash());
		parent.setLayout(new GridLayout(2, false));
		initialCashText = UIUtils.newDoubleText(parent, "Total liquide",
				new Acceptor<Double>() {
					@Override
					public void accept(Double e) {
						data.setInitialCash(e != null ? e.doubleValue() : 0.0);
					}
				});
		CTabFolder folder = new CTabFolder(parent, SWT.FLAT);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		CTabItem stockItem = new CTabItem(folder, SWT.NONE);
		stockItem.setText("Stock");
		stockForm = new ProductSetForm(folder);
		stockForm.setProducts(storage.getStockState().getProductsStock());
		folder.setSelection(stockItem);

		CTabItem clientItem = new CTabItem(folder, SWT.NONE);
		clientItem.setText("Crédit clients");
		clientCreditComp = new PersonAmountComposite<Client>(folder,
				storage.getClients(), "Crédits");
		clientItem.setControl(clientCreditComp);
		clientCreditComp.setAmounts(initData.getClientCredits());

		CTabItem proiderItem = new CTabItem(folder, SWT.NONE);
		proiderItem.setText("Crédit fournisseurs");
		providerCreditComp = new PersonAmountComposite<Provider>(folder,
				storage.getProviders(), "Crédits");
		proiderItem.setControl(providerCreditComp);
		providerCreditComp.setAmounts(initData.getProviderCredits());

		initialCashText
				.setText(DataUtils.roundString(initData.getInitialCash()));
		stockItem.setControl(stockForm);
		return folder;
	}

	@Override
	protected void okPressed() {
		if (clientCreditComp.isValid() && providerCreditComp.isValid()) {
			data.setClientCredits(clientCreditComp.getAmounts());
			data.setProviderCredits(providerCreditComp.getAmounts());
			StorageManager.getStorage().setInitData(data);
			StorageManager.getStorage().setStockProducts(
					stockForm.getProducts());
			super.okPressed();
		}
	}

	public static int open(Shell parent, InitData data) {
		InitDataDialog dlg = new InitDataDialog(parent, data);
		return dlg.open();
	}

}
