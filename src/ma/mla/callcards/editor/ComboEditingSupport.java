package ma.mla.callcards.editor;

import java.util.List;

import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class ComboEditingSupport<T> extends EditingSupport {

	private ComboViewer combo;
	private T value;
	private List<T> items;
	private CellEditor editor;
	private IBaseLabelProvider labelProvider;

	public ComboEditingSupport(ColumnViewer viewer, List<T> items,
			IBaseLabelProvider labelProvider) {
		super(viewer);
		this.items = items;
		this.labelProvider = labelProvider;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		if (editor == null) {
			editor = new CellEditor((Composite) getViewer().getControl()) {

				@SuppressWarnings("unchecked")
				@Override
				protected void doSetValue(Object v) {
					value = (T) v;
					if (combo != null) {
						if (value != null) {
							combo.setSelection(new StructuredSelection(v));
						} else {
							combo.setSelection(null);
						}
					}
				}

				@Override
				protected void doSetFocus() {
					combo.getControl().setFocus();
					combo.getCombo().setListVisible(true);
				}

				@Override
				protected Object doGetValue() {
					return value;
				}

				@Override
				protected Control createControl(Composite parent) {
					if (combo == null || combo.getControl().isDisposed()) {
						combo = UIUtils.newCombo(parent, null, items,
								new Acceptor<T>() {
									@Override
									public void accept(T e) {
										value = e;
									}
								});
						combo.setLabelProvider(labelProvider);
					}
					return combo.getControl();
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
