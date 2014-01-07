package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.ProviderForm;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ProviderDialog extends Dialog {

	private Provider provider;
	private ProviderForm form;

	ProviderDialog(Shell parentShell) {
		this(parentShell, null);
	}

	ProviderDialog(Shell parentShell, Provider provider) {
		super(parentShell);
		this.provider = provider;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Nouveau Fournisseur");
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(1, false));
		form = new ProviderForm(cmp);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		form.setLayoutData(gd);
		if (provider != null) {
			form.readData(provider);
		}
		form.pack();
		return cmp;
	}

	@Override
	protected void okPressed() {
		if (form.isValid()) {
			if (provider == null) {
				provider = new Provider("", "", "");
				form.writeData(provider);
				StorageManager.getStorage().addProvider(provider);
			} else {
				form.writeData(provider);
			}
			super.okPressed();
		}
	}

	public static void create(Shell parent) {
		new ProviderDialog(parent).open();
	}

	public static void edit(Shell parent, Provider provider) {
		new ProviderDialog(parent, provider).open();
	}

}
