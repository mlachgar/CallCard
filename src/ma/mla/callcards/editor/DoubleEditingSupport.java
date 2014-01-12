package ma.mla.callcards.editor;

import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.UIUtils;
import ma.mla.callcards.utils.ValidatorAcceptor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public abstract class DoubleEditingSupport extends EditingSupport {

	private CellEditor editor;
	private Text text;
	private double value;

	public DoubleEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		if (editor == null) {
			editor = new CellEditor((Composite) getViewer().getControl()) {
				@Override
				protected Control createControl(Composite parent) {
					if (text == null || text.isDisposed()) {
						text = UIUtils.newDoubleText(parent,
								new ValidatorAcceptor<Double>() {
									@Override
									public void accept(Double e) {
										value = e != null ? e.doubleValue()
												: 0.0;
									}

									@Override
									public boolean isValid(Double e) {
										return DoubleEditingSupport.this
												.isValid(element, value);
									}
								});
					}
					return text;
				}

				@Override
				protected Object doGetValue() {
					return Double.valueOf(value);
				}

				@Override
				protected void doSetFocus() {
					text.setFocus();
				}

				@Override
				protected void doSetValue(Object v) {
					if (v instanceof Double) {
						value = (Double) v;
						if (text != null) {
							text.setText(DataUtils.roundString(value));
						}
					} else {
						value = 0.0;
						if (text != null) {
							text.setText("");
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

	abstract protected boolean isValid(Object element, Double value);

}
