package ma.mla.callcards.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;

public abstract class ComboEditingSupoort extends EditingSupport {

	private ComboBoxViewerCellEditor editor;

	public ComboEditingSupoort(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		if (editor == null) {
			editor = new ComboBoxViewerCellEditor(null);
		}
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	abstract protected boolean isValid(Object element, Double value);

}
