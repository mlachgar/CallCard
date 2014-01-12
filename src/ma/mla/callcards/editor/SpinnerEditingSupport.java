package ma.mla.callcards.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

public abstract class SpinnerEditingSupport extends EditingSupport {

	private CellEditor editor;
	private Spinner spinner;
	private Integer value;
	private int min;
	private int max;

	public SpinnerEditingSupport(TableViewer viewer, int min, int max) {
		super(viewer);
		this.min = min;
		this.max = max;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		if (editor == null) {
			editor = new CellEditor((Composite) getViewer().getControl()) {
				@Override
				protected Control createControl(Composite parent) {
					if (spinner == null || spinner.isDisposed()) {
						spinner = new Spinner(parent, SWT.BORDER);
						spinner.setMinimum(min);
						spinner.setMaximum(max);
						spinner.addModifyListener(new ModifyListener() {

							@Override
							public void modifyText(ModifyEvent e) {
								value = Integer.valueOf(spinner.getSelection());
							}
						});
					}
					return spinner;
				}

				@Override
				protected Object doGetValue() {
					return value;
				}

				@Override
				protected void doSetFocus() {
					spinner.setFocus();
				}

				@Override
				protected void doSetValue(Object v) {
					if (v instanceof Integer) {
						value = (Integer) v;
						if (spinner != null) {
							spinner.setSelection(value);
						}
					} else {
						value = Integer.valueOf(min);
						if (spinner != null) {
							spinner.setSelection(min);
						}
					}
				}
			};
		}
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

}
