package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.SaleForm;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class SaleDialog extends ScrolledDialog {

	private Sale sale;
	private SaleForm form;

	SaleDialog(Shell parentShell) {
		this(parentShell, null);
	}

	SaleDialog(Shell parentShell, Sale sale) {
		super(parentShell);
		this.sale = sale;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Nouvelle Vente");
	}

	@Override
	protected Control createScrolledContent(Composite parent) {
		form = new SaleForm(parent);
		if (sale != null) {
			form.readData(sale);
		}
		return form;
	}

	@Override
	protected void okPressed() {
		if (form.isValid()) {
			if (sale == null) {
				sale = new Sale();
				form.writeData(sale);
				StorageManager.getStorage().addSale(sale);
			} else {
				form.writeData(sale);
			}
			super.okPressed();
		}
	}

	public static void create(Shell parent) {
		new SaleDialog(parent).open();
	}

	public static void edit(Shell parent, Sale sale) {
		new SaleDialog(parent, sale).open();
	}

}
