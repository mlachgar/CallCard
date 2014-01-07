package ma.mla.callcards.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public abstract class ScrolledDialog extends Dialog {

	protected ScrolledDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected int getShellStyle() {
		return SWT.SHELL_TRIM;
	}

	@Override
	protected final Control createDialogArea(Composite parent) {
		final ScrolledForm forms = new ScrolledForm(parent);
		forms.setBackground(parent.getBackground());
		forms.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		forms.setExpandVertical(true);
		forms.setExpandHorizontal(true);
		Composite body = forms.getBody();
		body.setLayout(new FillLayout());
		Control c = createScrolledContent(body);
		c.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				forms.reflow(true);
			}
		});
		forms.reflow(true);
		return forms;
	}

	protected abstract Control createScrolledContent(Composite body);

}
