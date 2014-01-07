package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.ProductForm;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductType;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ProductDialog extends Dialog {

	private Product product;
	private ProductForm form;
	private boolean isCard;

	ProductDialog(Shell parentShell, boolean isCard) {
		super(parentShell);
		this.isCard = isCard;
	}

	ProductDialog(Shell parentShell, Product product) {
		super(parentShell);
		this.product = product;
		this.isCard = product.getType() == ProductType.CARD;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(product != null ? "Modifer le produit"
				: "Nouveau Produit");
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(1, false));
		form = new ProductForm(cmp, isCard);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		form.setLayoutData(gd);
		if (product != null) {
			form.readData(product);
		}
		cmp.pack();
		return cmp;
	}

	@Override
	protected void okPressed() {
		if (form.isValid()) {
			if (product == null) {
				product = new Product(isCard ? ProductType.CARD
						: ProductType.WALLET);
				form.writeData(product);
				StorageManager.getStorage().addProduct(product);
			} else {
				form.writeData(product);
				StorageManager.getStorage().modifyProduct(product);
			}
			super.okPressed();
		}
	}

	public static void create(Shell parent, boolean isCard) {
		new ProductDialog(parent, isCard).open();
	}

	public static void edit(Shell parent, Product product) {
		new ProductDialog(parent, product).open();
	}

}
