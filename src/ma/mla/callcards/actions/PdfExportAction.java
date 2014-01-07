package ma.mla.callcards.actions;

import java.io.File;
import java.util.List;

import ma.mla.callcards.ResourceManager;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.PrintableView;
import ma.mla.callcards.utils.Printer;
import ma.mla.callcards.utils.UIUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;

public class PdfExportAction extends Action {

	private PrintableView view;

	public PdfExportAction(PrintableView view) {
		this.view = view;
		setText("Export PDF");
		setImageDescriptor(ResourceManager.getDescriptor("pdf.png"));
	}

	@Override
	public void run() {
		if (view != null) {
			try {
				List<PrintableControl> controls = view.getPrintableControls();
				if (controls != null && !controls.isEmpty()) {
					FileDialog dlg = new FileDialog(UIUtils.getShell());
					dlg.setFilterExtensions(new String[] { "*.pdf" });
					dlg.setFilterNames(new String[] { "Fichiers PDF" });
					String path = dlg.open();
					if (path != null) {
						if (!path.endsWith(".pdf")) {
							path = path + ".pdf";
						}
						File file = new File(path);
						Printer.export(view.getPrintTitle(), controls, file);
						Program prog = Program.findProgram("pdf");
						if (prog != null) {
							prog.execute(path);
						}
					}
				}
			} catch (Exception e) {
				MessageDialog.openError(UIUtils.getShell(),
						"Impossible d'exporter le document", e.getMessage());
			}
		}
	}

}
