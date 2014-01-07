package ma.mla.callcards.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class BaseForm extends Composite {

	public BaseForm(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
	}

}
