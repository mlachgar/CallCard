package ma.mla.callcards.actions;

import java.util.Date;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.dialogs.DateDialog;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;
import ma.mla.callcards.views.DateFilter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public abstract class DateFilterAction extends Action {

	private MenuManager mm;

	abstract protected void filterPerformed(Date from, Date to);

	public DateFilterAction() {
		super("Filtrer par date", Action.AS_DROP_DOWN_MENU);
		setImageDescriptor(ResourceManager.getDescriptor("calendar_16.png"));
		mm = new MenuManager();
		Action action = new Action(DateFilter.ALL.label, Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				setFilter(DateFilter.ALL);
				filterPerformed(null, null);
			}
		};
		action.setChecked(true);
		mm.add(action);

		mm.add(new Action(DateFilter.TODAY.label, Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				setFilter(DateFilter.TODAY);
				filterPerformed(new Date(), null);
			}
		});

		mm.add(new Action(DateFilter.THIS_WEEK.label, Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				setFilter(DateFilter.THIS_WEEK);
				filterPerformed(DataUtils.startOfWeek(), null);
			}
		});

		mm.add(new Action(DateFilter.THIS_MONTH.label, Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				setFilter(DateFilter.THIS_MONTH);
				filterPerformed(DataUtils.startOfMonth(), null);
			}
		});

		mm.add(new Action(DateFilter.DATES.label, Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				if (isChecked()) {
					setFilter(DateFilter.DATES);
					DateDialog dlg = new DateDialog(UIUtils.getShell());
					if (dlg.open() == Dialog.OK) {
						filterPerformed(dlg.getFrom(), dlg.getTo());
					}
				}
			}
		});

		setMenuCreator(new IMenuCreator() {

			Menu menu;

			@Override
			public Menu getMenu(Menu parent) {
				return null;
			}

			@Override
			public Menu getMenu(Control parent) {
				if (menu == null) {
					menu = mm.createContextMenu(parent);
				}
				return menu;
			}

			@Override
			public void dispose() {
				if (menu != null && !menu.isDisposed()) {
					menu.dispose();
				}
			}
		});
	}

	private void setFilter(DateFilter filter) {
		// setText(filter.label);
		// mm.update(true);
	}
}
