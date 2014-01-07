package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.PurchaseForm;
import ma.mla.callcards.model.Purchase;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class PurchaseDialog extends ScrolledDialog {

	private Purchase purchase;
	private PurchaseForm form;

	PurchaseDialog(Shell parentShell) {
		this(parentShell, null);
	}

	PurchaseDialog(Shell parentShell, Purchase purchase) {
		super(parentShell);
		this.purchase = purchase;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 400);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Nouvel Achat");
	}

	@Override
	protected Control createScrolledContent(Composite parent) {
		form = new PurchaseForm(parent);
		if (purchase != null) {
			form.readData(purchase);
		}
		return form;
	}

	@Override
	protected void okPressed() {
		if (form.isValid()) {
			if (purchase == null) {
				purchase = new Purchase();
				form.writeData(purchase);
				StorageManager.getStorage().addPurchase(purchase);
			} else {
				form.writeData(purchase);
			}
			super.okPressed();
		}
	}

	public static void create(Shell parent) {
		new PurchaseDialog(parent).open();
	}

	public static void edit(Shell parent, Purchase p) {
		new PurchaseDialog(parent, p).open();
	}

}
