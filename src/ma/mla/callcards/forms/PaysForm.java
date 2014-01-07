package ma.mla.callcards.forms;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ma.mla.callcards.model.Person;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

public class PaysForm<T extends Person> extends BaseForm {

	private DateTime dateCmp;
	private PaysComposite<T> paysComposite;

	public PaysForm(Composite parent, List<T> persons, Map<T, Double> credits,
			String personLabel) {
		super(parent);
		dateCmp = UIUtils.newDate(this, "Date");
		paysComposite = new PaysComposite<T>(this, persons, credits,
				personLabel);
		paysComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 2, 1));
	}

	public Date getDate() {
		return DataUtils.getDate(dateCmp);
	}

	public List<PersonAmount<T>> getAmounts() {
		return paysComposite.getAmounts();
	}

	public boolean isValid() {
		return paysComposite.isValid();
	}

}
