package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.ExpenseForm;
import ma.mla.callcards.model.Expense;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ExpenseDialog extends Dialog {

	private ExpenseForm form;

	public ExpenseDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Nouvelle dépense");
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		form = new ExpenseForm(parent);
		form.setLayoutData(new GridData(260, SWT.DEFAULT));
		return form;
	}

	@Override
	protected void okPressed() {
		if (form.getAmount() > 0.0) {
			Expense ex = new Expense();
			ex.setDate(form.getDate());
			ex.setAmount(form.getAmount());
			StorageManager.getStorage().addExpense(ex);
			super.okPressed();
		} else {
			MessageDialog.openError(getShell(), "Dnnées incomplètes",
					"Veuillez saisir un montant non null");
		}
	}

}
