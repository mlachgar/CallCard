package ma.mla.callcards.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

public class DateFilterComposite extends Composite {

	private ComboViewer filterCombo;
	private DateTime fromDate;
	private DateTime toDate;
	private Button applyButton;
	private Composite dateComposite;
	private DateFilter currentFilter = DateFilter.ALL;

	private List<DateFilterListener> listeners = new ArrayList<DateFilterListener>();

	public DateFilterComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		filterCombo = UIUtils.newCombo(this, null, SWT.BORDER | SWT.READ_ONLY);
		filterCombo.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, false, false));
		filterCombo.setContentProvider(new ArrayContentProvider());
		filterCombo.setInput(DateFilter.values());
		filterCombo.setSelection(new StructuredSelection(currentFilter));

		filterCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						currentFilter = UIUtils.getFirstSelected(
								event.getSelection(), DateFilter.class);
						if (currentFilter == DateFilter.DATES) {
							dateComposite.setVisible(true);
						} else {
							dateComposite.setVisible(false);
							applyCurrentFilter();
						}
					}
				});

		dateComposite = new Composite(this, SWT.NONE);
		dateComposite.setLayout(new GridLayout(5, false));
		fromDate = UIUtils.newDate(dateComposite, "De");
		toDate = UIUtils.newDate(dateComposite, "À");
		applyButton = new Button(dateComposite, SWT.PUSH | SWT.FLAT);
		applyButton.setText("Filtrer");
		applyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyCurrentFilter();
			}
		});

		dateComposite.setVisible(currentFilter == DateFilter.DATES);

	}

	private void applyCurrentFilter() {
		switch (currentFilter) {
		case ALL:
			fireFilterChange(null, null);
			break;
		case TODAY:
			fireFilterChange(new Date(), null);
			break;
		case THIS_WEEK:
			fireFilterChange(DataUtils.startOfWeek(), null);
			break;
		case THIS_MONTH:
			fireFilterChange(DataUtils.startOfMonth(), null);
			break;
		case DATES:
			fireFilterChange(DataUtils.getDate(fromDate),
					DataUtils.getDate(toDate));
			break;
		}
	}

	public void addFilterListener(DateFilterListener l) {
		listeners.add(l);
	}

	public void removeFilterListener(DateFilterListener l) {
		listeners.remove(l);
	}

	protected void fireFilterChange(Date from, Date to) {
		for (DateFilterListener l : listeners) {
			l.filterChanged(from, to);
		}
	}

}
