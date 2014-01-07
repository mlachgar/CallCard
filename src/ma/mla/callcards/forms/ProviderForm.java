package ma.mla.callcards.forms;

import ma.mla.callcards.model.Provider;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ProviderForm extends BaseForm {

	private Text nameText;
	private Text phoneNumberText;
	private Text addressText;

	public ProviderForm(Composite parent) {
		super(parent);
		nameText = UIUtils.newText(this, "Nom");
		phoneNumberText = UIUtils.newText(this, "N°.Téléphone");
		addressText = UIUtils.newTextArea(this, "Adresse", 4);
	}

	public void readData(Provider provider) {
		nameText.setText(provider.getName());
		phoneNumberText.setText(provider.getPhoneNumber());
		addressText.setText(provider.getAddress());
	}

	public void writeData(Provider provider) {
		provider.setName(nameText.getText());
		provider.setPhoneNumber(phoneNumberText.getText());
		provider.setAddress(addressText.getText());
	}

	public boolean isValid() {
		if (nameText.getText().isEmpty()) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Le nom ne doit pas être vide");
			return false;
		}
		return true;
	}

}
