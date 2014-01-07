package ma.mla.callcards.forms;

import java.util.Date;

import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

public class ExpenseForm extends BaseForm {

	private DateTime dateCmp;
	private double amount = 0.0;

	public ExpenseForm(Composite parent) {
		super(parent);
		dateCmp = UIUtils.newDate(this, "Date");
		UIUtils.newDoubleText(this, "Montant", 0.0, Double.NaN,
				new Acceptor<Double>() {

					@Override
					public void accept(Double e) {
						amount = e != null ? e.doubleValue() : 0.0;
					}
				});
	}

	public Date getDate() {
		return DataUtils.getDate(dateCmp);
	}

	public double getAmount() {
		return amount;
	}

}
