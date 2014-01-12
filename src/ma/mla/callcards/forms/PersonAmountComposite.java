package ma.mla.callcards.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.editor.ComboEditingSupport;
import ma.mla.callcards.editor.DoubleEditingSupport;
import ma.mla.callcards.model.Person;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;

@SuppressWarnings("unchecked")
public class PersonAmountComposite<T extends Person> extends Composite {

	private TableViewer table;
	private String personLabel;
	private List<T> persons;
	private List<PersonAmount<T>> amounts = new ArrayList<PersonAmount<T>>();
	private Set<Person> choosenPersons = new HashSet<Person>();
	private Action removeAction;

	public PersonAmountComposite(Composite parent, List<T> persons,
			String personLabel) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		this.persons = persons;
		this.personLabel = personLabel;
		setUpToolbar();
		createPaysTable();
		table.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				removeAction.setEnabled(!table.getSelection().isEmpty());
			}
		});
	}

	private PersonAmount<T> getPersonAmount(Object element) {
		if (element instanceof PersonAmount) {
			return (PersonAmount<T>) element;
		}
		return null;
	}

	private void createPaysTable() {
		table = new TableViewer(this, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		Table t = table.getTable();
		t.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		t.setLinesVisible(true);
		t.setHeaderVisible(true);
		table.setContentProvider(new ArrayContentProvider());
		TableViewerColumn tvc = new TableViewerColumn(table, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText(personLabel);
		col.setWidth(140);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null && pa.getPerson() != null) {
					return pa.getPerson().getName();
				}
				return "";
			}
		});
		tvc.setEditingSupport(new ComboEditingSupport<T>(table, persons,
				new LabelProvider() {
					public String getText(Object element) {
						if (element instanceof Person) {
							Person p = (Person) element;
							return p.getName();
						}
						return "";
					}
				}) {
			@Override
			protected Object getValue(Object element) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null) {
					return pa.getPerson();
				}
				return null;
			}

			@Override
			protected void setValue(Object element, Object value) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null) {
					T p = (T) value;
					pa.setPerson(p);
					table.update(pa, null);
				}
			}

		});

		tvc = new TableViewerColumn(table, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Montant");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null) {
					return DataUtils.roundString(pa.getAmount());
				}
				return "";
			}
		});
		tvc.setEditingSupport(new DoubleEditingSupport(table) {

			@Override
			public boolean isValid(Object element, Double value) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null) {
					return value >= 0.0;
				}
				return false;
			}

			@Override
			protected Object getValue(Object element) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null) {
					return Double.valueOf(pa.getAmount());
				}
				return null;
			}

			@Override
			protected void setValue(Object element, Object value) {
				PersonAmount<T> pa = getPersonAmount(element);
				if (pa != null) {
					pa.setAmount((Double) value);
					table.update(pa, null);
				}
			}

		});
		UIUtils.customizeTableEditing(table);
		table.setInput(amounts);
	}

	private void setUpToolbar() {
		ToolBarManager m = new ToolBarManager();
		Action addAction = new Action("Ajouter",
				ResourceManager.getDescriptor("plus_16.png")) {
			@Override
			public void run() {
				amounts.add(newAmount());
				table.refresh();
			}
		};
		m.add(addAction);
		removeAction = new Action("Supprimer",
				ResourceManager.getDescriptor("minus_16.png")) {
			@Override
			public void run() {
				List<?> selectedAmounts = UIUtils.getSelectedObjects(table
						.getSelection());
				amounts.removeAll(selectedAmounts);
				for (Object o : selectedAmounts) {
					choosenPersons.remove(((PersonAmount<?>) o).getPerson());
				}
				table.refresh();
			}
		};
		removeAction.setEnabled(false);
		m.add(removeAction);

		ToolBar toolbar = m.createControl(this);
		toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
	}

	private PersonAmount<T> newAmount() {
		T person = null;
		for (T p : persons) {
			person = p;
			if (!choosenPersons.contains(p)) {
				choosenPersons.add(p);
				break;
			}
		}
		return new PersonAmount<T>(person, 0.0);
	}

	public void setAmounts(List<PersonAmount<T>> amounts) {
		this.amounts.clear();
		this.amounts.addAll(amounts);
		table.refresh();
	}

	public List<PersonAmount<T>> getAmounts() {
		return amounts;
	}

	public boolean isValid() {
		for (PersonAmount<T> a : amounts) {
			if (!isValid(a)) {
				return false;
			}
		}
		return true;
	}

	public boolean isValid(PersonAmount<T> a) {
		if (a.getPerson() == null || a.getAmount() == 0.0) {
			MessageDialog.openError(getShell(), "Données incomplètes",
					"Veuillez compléter les donnée avant de valider");
			return false;
		}
		return true;
	}

}
