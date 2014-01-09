package ma.mla.callcards.editor;

import ma.mla.callcards.utils.Validator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

public abstract class DoubleEditingSupoort extends EditingSupport {

	DoubleCellEditor editor;

	public DoubleEditingSupoort(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		if (editor == null) {
			editor = new DoubleCellEditor(getViewer().getControl().getParent(),
					new Validator<Double>() {

						@Override
						public boolean isValid(Double value) {
							return DoubleEditingSupoort.this.isValid(element,
									value);
						}
					});
		}
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	abstract protected boolean isValid(Object element, Double value);

}
