package ma.mla.callcards.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public abstract class TextEditingSupport extends EditingSupport {

	public TextEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor();
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

}
