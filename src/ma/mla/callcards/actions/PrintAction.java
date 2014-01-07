package ma.mla.callcards.actions;

import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.PrintableView;
import ma.mla.callcards.utils.Printer;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

public class PrintAction extends Action {

	private PrintableView view;

	public PrintAction(PrintableView view) {
		this.view = view;
		setText("Imprimer");
		setImageDescriptor(ResourceManager.getDescriptor("print.png"));
	}

	@Override
	public void run() {
		if (view != null) {
			try {
				List<PrintableControl> controls = view.getPrintableControls();
				if (controls != null && !controls.isEmpty()) {
					Printer.print(view.getPrintTitle(), controls);
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getShell(),
						"Impossible d'imprimer le document", e.getMessage());
			}
		}
	}

}
