package ma.mla.callcards.editor;

import ma.mla.callcards.utils.Acceptor;
import ma.mla.callcards.utils.UIUtils;
import ma.mla.callcards.utils.Validator;
import ma.mla.callcards.utils.ValidatorAcceptor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class DoubleCellEditor extends CellEditor {

	private Text text;
	private double value;
	private Validator<Double> validator;

	public DoubleCellEditor(Composite parent, Validator<Double> validator) {
		super(parent);
		this.validator = validator;
	}

	@Override
	protected Control createControl(Composite parent) {
		if (text == null || text.isDisposed()) {
			text = UIUtils.newDoubleText(parent,
					ValidatorAcceptor.from(validator, new Acceptor<Double>() {
						@Override
						public void accept(Double e) {
							value = e != null ? e.doubleValue() : 0.0;
						}
					}));
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
	protected void doSetValue(Object value) {
		if (value instanceof Double) {
			this.value = (Double) value;
			text.setText(String.valueOf(value));
		} else {
			value = 0.0;
			text.setText("");
		}
	}

}
