package ma.mla.callcards.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.model.Person;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

public class PaysComposite<T extends Person> extends BaseForm {

	private List<PayRow<T>> rows = new ArrayList<PayRow<T>>();
	private Composite rowsComposite;
	private String personLabel;
	private List<T> persons;
	private Map<T, Double> credits;

	public PaysComposite(Composite parent, List<T> persons,
			Map<T, Double> credits, String personLabel) {
		super(parent);
		this.persons = persons;
		this.credits = credits;
		this.personLabel = personLabel;
		createPaysComp();
	}

	protected void createColumns(Composite parent) {
		UIUtils.newBorderLabel(parent, personLabel, 80);
		UIUtils.newBorderLabel(parent, "Crédit", 60);
		UIUtils.newBorderLabel(parent, "Montant", 60);
		ToolBarManager m = new ToolBarManager();
		m.add(new Action("Ajouter", ResourceManager
				.getDescriptor("plus_16.png")) {
			@Override
			public void run() {
				addRow();
				layout(true, true);
			}
		});
		ToolBar toolbar = m.createControl(parent);
		toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
	}

	private void createPaysComp() {
		rowsComposite = new Composite(this, SWT.NONE);
		rowsComposite.setLayout(new GridLayout(4, false));
		rowsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 2, 1));
		createColumns(rowsComposite);
		addRow();
	}

	public void setAmounts(List<PersonAmount<T>> amounts) {
		for (PayRow<T> row : rows) {
			row.dispose();
		}
		rows.clear();
		for (PersonAmount<T> pa : amounts) {
			PayRow<T> row = addRow();
			row.setData(pa.getPerson(), pa.getAmount());
		}
	}

	public List<PersonAmount<T>> getAmounts() {
		List<PersonAmount<T>> amounts = new ArrayList<PersonAmount<T>>();
		for (PayRow<T> row : rows) {
			PersonAmount<T> pa = new PersonAmount<T>();
			pa.setAmount(row.getAmount());
			pa.setPerson(row.getPerson());
			amounts.add(pa);
		}
		return amounts;
	}

	public boolean isValid() {
		for (PayRow<T> row : rows) {
			if (!row.isValid()) {
				return false;
			}
		}
		return true;
	}

	private PayRow<T> addRow() {
		PayRow<T> row = new PayRow<T>(rowsComposite, persons) {
			@Override
			protected void rowRemoved() {
				rows.remove(this);
			}

			@Override
			protected double getCredit(T person) {
				Double d = credits.get(person);
				return d != null ? d.doubleValue() : 0.0;
			}
		};
		rows.add(row);
		return row;
	}

}
