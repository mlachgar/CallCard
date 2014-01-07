package ma.mla.callcards.dialogs;

import java.util.Date;

import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

public class DateDialog extends Dialog {

	private DateTime fromDate;
	private DateTime toDate;
	private Date from;
	private Date to;

	public DateDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Filtrer par date");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(4, false));
		fromDate = UIUtils.newDate(cmp, "De");
		toDate = UIUtils.newDate(cmp, "À");
		cmp.pack();
		return cmp;
	}

	@Override
	protected void okPressed() {
		from = DataUtils.getDate(fromDate);
		to = DataUtils.getDate(toDate);
		super.okPressed();
	}

	public Date getFrom() {
		return from;
	}

	public Date getTo() {
		return to;
	}

}
