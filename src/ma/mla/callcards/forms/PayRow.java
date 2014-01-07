package ma.mla.callcards.forms;

import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.model.Person;
import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public abstract class PayRow<T extends Person> {

	private ComboViewer combo;
	private Text creditText;
	private Text amountText;
	private ToolBar toolbar;

	private T person;
	private Double amount = null;

	abstract protected void rowRemoved();

	abstract protected double getCredit(T person);

	public PayRow(final Composite parent, List<T> persons) {
		combo = UIUtils.newCombo(parent, null, persons, new Acceptor<T>() {
			@Override
			public void accept(T p) {
				person = p;
				if (creditText != null) {
					creditText.setText(DataUtils.roundString(getCredit(person)));
				}
			}
		});
		combo.getCombo().setLayoutData(gridData(140));
		creditText = FormUtils.newInfoText(parent, null);
		if (person != null) {
			creditText.setText(DataUtils.roundString(getCredit(person)));
		}
		amountText = UIUtils.newDoubleText(parent, null, 0.0, Double.NaN,
				new Acceptor<Double>() {

					@Override
					public void accept(Double d) {
						amount = d;
					}
				});
		amountText.setLayoutData(gridData(120));
		ToolBarManager m = new ToolBarManager();
		m.add(new Action("Supprimer", ResourceManager
				.getDescriptor("minus_16.png")) {
			@Override
			public void run() {
				rowRemoved();
				dispose();
				parent.layout(true, true);
			}
		});

		toolbar = m.createControl(parent);
		toolbar.setLayoutData(gridData(30));
	}

	private GridData gridData(int width) {
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd.widthHint = width;
		return gd;
	}

	public void setData(T person, double amount) {
		this.amount = Double.valueOf(amount);
		amountText.setText(String.valueOf(amount));
		this.person = person;
		combo.setSelection(new StructuredSelection(person), false);
	}

	public T getPerson() {
		return person;
	}

	public double getAmount() {
		return amount != null ? amount.doubleValue() : 0.0;
	}

	public boolean isValid() {
		if (person == null || amount == null) {
			MessageDialog.openError(amountText.getShell(),
					"Données incomplètes",
					"Veuillez compléter les donnée avant de valider");
			return false;
		}
		return true;
	}

	public void dispose() {
		combo.getControl().dispose();
		amountText.dispose();
		toolbar.dispose();
	}

}
