package ma.mla.callcards.actions;

import java.util.Date;

import ma.mla.callcards.views.DateFilterComposite;
import ma.mla.callcards.views.DateFilterListener;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class DateFilterContribution extends ControlContribution {

	private DateFilterComposite filterComposite;

	public DateFilterContribution(String id) {
		super(id);
	}

	@Override
	protected Control createControl(Composite parent) {
		if (filterComposite == null) {
			filterComposite = new DateFilterComposite(parent, SWT.NONE);
			filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
					true, true));
			filterComposite.addFilterListener(new DateFilterListener() {

				@Override
				public void filterChanged(Date from, Date to) {
					filterPerformed(from, to);
				}
			});
		}
		return filterComposite;
	}

	abstract protected void filterPerformed(Date from, Date to);

}
