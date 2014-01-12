package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.SummaryComposite;
import ma.mla.callcards.model.AccountsSummary;
import ma.mla.callcards.utils.SafePropertyListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AccountsView extends ViewPart {

	public static final String ID = "ma.mla.view.accounts";

	private SummaryComposite summaryComposite;
	private PropertyChangeListener pcl;

	@Override
	public void createPartControl(Composite parent) {
		summaryComposite = new SummaryComposite(parent, SWT.NONE);

		updateUI();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				updateUI();
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
	}

	private void updateUI() {
		AccountsSummary summary = StorageManager.getStorage()
				.getAccountsSummary();
		summaryComposite.setSummary(summary);
	}

	@Override
	public void setFocus() {
		summaryComposite.setFocus();
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

}
