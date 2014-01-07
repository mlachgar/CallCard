package ma.mla.callcards.dialogs;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.ClientForm;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ClientDialog extends Dialog {

	private Client client;
	private ClientForm form;

	ClientDialog(Shell parentShell) {
		this(parentShell, null);
	}

	ClientDialog(Shell parentShell, Client client) {
		super(parentShell);
		this.client = client;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Nouveau Client");
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return UIUtils.getCenterLocation(initialSize);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cmp = (Composite) super.createDialogArea(parent);
		cmp.setLayout(new GridLayout(1, false));
		form = new ClientForm(cmp);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		form.setLayoutData(gd);
		if (client != null) {
			form.readData(client);
		}
		cmp.pack();
		return cmp;
	}

	@Override
	protected void okPressed() {
		if (form.isValid()) {
			if (client == null) {
				client = new Client("", "", "");
				form.writeData(client);
				StorageManager.getStorage().addClient(client);
			} else {
				form.writeData(client);
			}
			super.okPressed();
		}
	}

	public static void create(Shell parent) {
		new ClientDialog(parent).open();
	}

	public static void edit(Shell parent, Client client) {
		new ClientDialog(parent, client).open();
	}

}
