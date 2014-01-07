package ma.mla.callcards.views;

import java.util.List;

import ma.mla.callcards.dao.Storage;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.utils.DataUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ClientsFinancielComposite extends Composite {

	private Text in;
	private Text out;
	private Text oldCredit;
	private Text credit;
	private Text gain;

	public ClientsFinancielComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		out = FormUtils.newInfoText(this, "Total Ventes");
		out.setForeground(out.getDisplay().getSystemColor(SWT.COLOR_RED));
		in = FormUtils.newInfoText(this, "Total payé");
		in.setForeground(in.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
		oldCredit = FormUtils.newInfoText(this, "Ancien crédit");
		credit = FormUtils.newInfoText(this, "Total crédit");
		gain = FormUtils.newInfoText(this, "Total bénéfice");
	}

	public void updateUI() {
		Storage storage = StorageManager.getStorage();
		List<Sale> sales = storage.getSales();
		List<PersonAmount<Client>> oldCredits = storage.getClientInitialCredits();
		double totalIn = storage.getTotalClientPays();
		double totalOldCredit = 0.0;
		double totalCredit = storage.getTotalCredit();
		double totalOut = 0.;
		double totalCost = 0.0;
		for (Sale s : sales) {
			totalOut += s.getTotal();
			totalCost += s.getCost();
		}
		for (PersonAmount<Client> cc : oldCredits) {
			totalOldCredit += cc.getAmount();
		}
		in.setText(DataUtils.roundString(totalIn));
		out.setText(DataUtils.roundString(totalOut));
		oldCredit.setText(DataUtils.roundString(totalOldCredit));
		credit.setText(DataUtils.roundString(totalCredit));
		gain.setText(DataUtils.roundString(totalOut - totalCost));
		if (totalIn < totalOut) {
			credit.setForeground(in.getDisplay().getSystemColor(SWT.COLOR_RED));
		} else {
			credit.setForeground(in.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}

		if (totalOut < totalCost) {
			gain.setForeground(in.getDisplay().getSystemColor(SWT.COLOR_RED));
		} else {
			gain.setForeground(in.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}
	}

}
