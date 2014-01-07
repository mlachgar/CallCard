package ma.mla.callcards.views;

import java.util.Date;

import ma.mla.callcards.actions.DateFilterAction;
import ma.mla.callcards.actions.PdfExportAction;
import ma.mla.callcards.actions.PrintAction;
import ma.mla.callcards.model.PrintableView;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public abstract class DateFilteredView extends ViewPart implements
		PrintableView {

	protected Date fromDate;
	protected Date toDate;

	@Override
	public final void createPartControl(Composite parent) {
		createContent(parent);
		getViewSite().getActionBars().getToolBarManager()
				.add(new DateFilterAction() {

					@Override
					protected void filterPerformed(Date from, Date to) {
						fromDate = from;
						toDate = to;
						updateUI();
					}
				});
		getViewSite().getActionBars().getToolBarManager()
				.add(new PrintAction(this));
		getViewSite().getActionBars().getToolBarManager()
				.add(new PdfExportAction(this));
	}

	abstract protected void updateUI();

	abstract protected void createContent(Composite parent);

}
