package ma.mla.callcards.dialogs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ma.mla.callcards.dao.Storage;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.PaysForm;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.Person;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.model.ProviderPay;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class PayDialog<T extends Person> extends ScrolledDialog {

	private PaysForm<T> form;
	private List<T> persons;
	private String personLabel;
	private String title;

	private List<PersonAmount<T>> amounts;
	private Date date;
	private Map<T, Double> credits;

	PayDialog(Shell parentShell, String title, List<T> persons,
			Map<T, Double> credits, String personLabel) {
		super(parentShell);
		this.title = title;
		this.persons = persons;
		this.credits = credits;
		this.personLabel = personLabel;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 680);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
		UIUtils.center(newShell);
	}

	@Override
	protected Control createScrolledContent(Composite parent) {
		form = new PaysForm<T>(parent, persons, credits, personLabel);
		return form;
	}

	@Override
	protected void okPressed() {
		if (form.isValid()) {
			amounts = form.getAmounts();
			date = form.getDate();
			super.okPressed();
		}
	}

	public static void addProviderPays(Shell parent) {
		Storage storage = StorageManager.getStorage();
		PayDialog<Provider> dialog = new PayDialog<Provider>(parent,
				"Nouvelles paies fournisseurs", storage.getProviders(),
				storage.getProviderCredits(), "Fournisseur");
		if (dialog.open() == OK) {
			for (PersonAmount<Provider> pa : dialog.amounts) {
				ProviderPay pay = new ProviderPay();
				pay.setDate(dialog.date);
				pay.setProvider(pa.getPerson());
				pay.setAmount(pa.getAmount());
				storage.addPay(pay);
			}
		}
	}

	public static void addClientPays(Shell parent) {
		Storage storage = StorageManager.getStorage();
		PayDialog<Client> dialog = new PayDialog<Client>(parent,
				"Nouvelles paies clients", storage.getClients(),
				storage.getClientCredits(), "Client");
		if (dialog.open() == OK) {
			for (PersonAmount<Client> pa : dialog.amounts) {
				ClientPay pay = new ClientPay();
				pay.setDate(dialog.date);
				pay.setClient(pa.getPerson());
				pay.setAmount(pa.getAmount());
				storage.addPay(pay);
			}
		}
	}

}
