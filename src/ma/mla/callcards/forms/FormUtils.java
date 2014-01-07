package ma.mla.callcards.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FormUtils {

	public static Text newInfoText(Composite parent, String name, int fgColor) {
		Text text = newInfoText(parent, name);
		text.setForeground(text.getDisplay().getSystemColor(fgColor));
		return text;
	}

	public static Text newInfoText(Composite parent, String name, Font font) {
		Text text = newInfoText(parent, name);
		text.setFont(font);
		return text;
	}

	public static Text newInfoText(Composite parent, String name) {
		if (name != null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(name);
			label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false,
					false));
		}
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setEditable(false);
		return text;
	}

}
