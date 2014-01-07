package ma.mla.callcards.model;

import java.util.Set;

import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;

public class PrintableControl {
	private final Control control;
	private final String title;
	private Set<?> selectedItems;

	public PrintableControl(StructuredViewer viewer, String title) {
		this(viewer.getControl(), title, UIUtils.getSelectionSet(viewer
				.getSelection()));
	}

	public PrintableControl(Control control, String title, Set<?> selectedItems) {
		this.control = control;
		this.title = title;
		this.selectedItems = selectedItems;
	}

	public Control getControl() {
		return control;
	}

	public String getTitle() {
		return title;
	}

	public Set<?> getSelectedItems() {
		return selectedItems;
	}

}
