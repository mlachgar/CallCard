package ma.mla.callcards.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;

public class SectionRelayouter extends ExpansionAdapter {

	@Override
	public void expansionStateChanged(ExpansionEvent e) {
		Section section = (Section) e.getSource();
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, e
				.getState()));
		section.getParent().layout(true, true);
	}
}
