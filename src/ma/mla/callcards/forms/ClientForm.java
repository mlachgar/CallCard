package ma.mla.callcards.forms;

import ma.mla.callcards.model.Client;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ClientForm extends BaseForm {

	private Text nameText;
	private Text phoneNumberText;
	private Text addressText;

	public ClientForm(Composite parent) {
		super(parent);
		nameText = UIUtils.newText(this, "Nom");
		phoneNumberText = UIUtils.newText(this, "N°.Téléphone");
		addressText = UIUtils.newTextArea(this,"Adresse", 4);
	}

	public void readData(Client client) {
		nameText.setText(client.getName());
		phoneNumberText.setText(client.getPhoneNumber());
		addressText.setText(client.getAddress());
	}

	public void writeData(Client client) {
		client.setName(nameText.getText());
		client.setPhoneNumber(phoneNumberText.getText());
		client.setAddress(addressText.getText());
	}
	
	public boolean isValid() {
		if(nameText.getText().isEmpty()) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Le nom ne doit pas être vide");
			return false;
		}
		return true;
	}

}
