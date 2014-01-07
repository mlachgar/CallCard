package ma.mla.callcards.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.PrintableControl;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class Printer {

	private static final Font titleFont = new Font(FontFamily.HELVETICA, 12,
			Font.BOLD, BaseColor.BLUE);
	private static final Font columnFont = new Font(FontFamily.HELVETICA, 9,
			Font.BOLD);
	private static final Font tableTitleFont = new Font(FontFamily.HELVETICA,
			10, Font.BOLD | Font.UNDERLINE);
	private static final Font plainFont = new Font(FontFamily.HELVETICA, 8,
			Font.NORMAL);
	private static final Font headerFont = new Font(FontFamily.HELVETICA, 7,
			Font.NORMAL);

	public static void print(String title, List<PrintableControl> controls)
			throws Exception {
		File file = new File(System.getProperty("user.home"), "ccb.pdf");
		export(title, controls, file);
		print(file);
	}

	private static PdfPTable createPdfTable(Table t, Set<?> selectedObjects) {
		PdfPTable table = new PdfPTable(t.getColumnCount());
		for (int i = 0; i < t.getColumnCount(); i++) {
			TableColumn col = t.getColumn(i);
			PdfPCell cell = new PdfPCell(new Phrase(col.getText(), columnFont));
			table.addCell(cell);
		}
		if (selectedObjects != null && !selectedObjects.isEmpty()) {
			for (int i = 0; i < t.getItemCount(); i++) {
				TableItem item = t.getItem(i);
				if (selectedObjects.contains(item.getData())) {
					for (int j = 0; j < t.getColumnCount(); j++) {
						table.addCell(new Phrase(item.getText(j), plainFont));
					}
				}
			}
		} else {
			for (int i = 0; i < t.getItemCount(); i++) {
				TableItem item = t.getItem(i);
				for (int j = 0; j < t.getColumnCount(); j++) {
					table.addCell(new Phrase(item.getText(j), plainFont));
				}
			}
		}
		return table;
	}

	private static PdfPTable createPdfTable(Tree t, Set<?> selectedObjects) {
		PdfPTable table = new PdfPTable(t.getColumnCount());
		for (int i = 0; i < t.getColumnCount(); i++) {
			TreeColumn col = t.getColumn(i);
			PdfPCell cell = new PdfPCell(new Phrase(col.getText(), columnFont));
			table.addCell(cell);
		}
		List<TreeItem> items = new ArrayList<TreeItem>();
		if (selectedObjects != null && !selectedObjects.isEmpty()) {
			for (int i = 0; i < t.getItemCount(); i++) {
				TreeItem item = t.getItem(i);
				if (selectedObjects.contains(item.getData())) {
					items.add(item);
				}
				for (TreeItem subItem : item.getItems()) {
					if (selectedObjects.contains(subItem.getData())) {
						items.add(subItem);
					}
				}
			}
		} else {
			for (int i = 0; i < t.getItemCount(); i++) {
				TreeItem item = t.getItem(i);
				items.add(item);
				for (TreeItem subItem : item.getItems()) {
					items.add(subItem);
				}
			}
		}
		for (TreeItem item : items) {
			for (int j = 0; j < t.getColumnCount(); j++) {
				table.addCell(new Phrase(item.getText(j), plainFont));
			}
		}
		return table;
	}

	public static void export(String title, List<PrintableControl> controls,
			File file) throws Exception {

		Document doc = new Document(PageSize.A4);
		FileOutputStream stream = new FileOutputStream(file);
		PdfWriter.getInstance(doc, stream);
		doc.open();

		PdfDiv dateHeader = new PdfDiv();
		dateHeader.addElement(new Phrase(DataUtils.DATE_FORMAT
				.format(new Date()), headerFont));
		dateHeader.setFloatType(PdfDiv.FloatType.RIGHT);
		doc.add(dateHeader);

		PdfDiv nameHeader = new PdfDiv();
		nameHeader.addElement(new Phrase(StorageManager.getStorage().getName(),
				headerFont));
		nameHeader.setFloatType(PdfDiv.FloatType.LEFT);
		doc.add(nameHeader);

		doc.add(new LineSeparator(1, 100f, BaseColor.BLACK,
				LineSeparator.ALIGN_BOTTOM, 0));

		if (title != null) {
			doc.addTitle(title);
			Paragraph p = new Paragraph(title, titleFont);
			p.setAlignment(Paragraph.ALIGN_CENTER);
			doc.add(p);
		}
		for (int i = 0; i < controls.size(); i++) {
			PrintableControl c = controls.get(i);
			PdfPTable table = null;
			// if (i > 0) {
			// doc.newPage();
			// }
			if (c.getControl() instanceof Table) {
				table = createPdfTable((Table) c.getControl(),
						c.getSelectedItems());
			} else if (c.getControl() instanceof Tree) {
				table = createPdfTable((Tree) c.getControl(),
						c.getSelectedItems());
			}
			if (table != null) {
				if (c.getTitle() != null) {
					Paragraph p = new Paragraph(c.getTitle(), tableTitleFont);
					p.setSpacingAfter(5f);
					doc.add(p);
				}
				table.setWidthPercentage(100f);
				table.setSpacingBefore(5f);
				doc.add(table);
			}
		}
		doc.close();
	}

	public static void print(File file) throws Exception {
		PrintDialog dialog = new PrintDialog(UIUtils.getShell());
		PrinterData data = dialog.open();
		if (data != null) {
			PrintService[] ps = PrintServiceLookup.lookupPrintServices(null,
					null);
			if (ps.length == 0) {
				throw new IllegalStateException("Aucune imprimante trouvée");
			}

			PrintService myService = null;
			for (PrintService printService : ps) {
				if (printService.getName().equals(data.name)) {
					myService = printService;
					break;
				}
			}

			if (myService == null) {
				throw new IllegalStateException("Aucune imprimante trouvée");
			}

			DocPrintJob job = myService.createPrintJob();
			Doc printDoc = new SimpleDoc(file.toURI().toURL(),
					DocFlavor.URL.AUTOSENSE, null);
			job.addPrintJobListener(new PrintJobAdapter() {

				@Override
				public void printJobCompleted(PrintJobEvent pje) {
					MessageDialog.openInformation(UIUtils.getShell(),
							"Impression réussie",
							"Le document a été imprimé avec succès");
				}

				@Override
				public void printJobFailed(final PrintJobEvent pje) {
					UIUtils.async(new Runnable() {

						@Override
						public void run() {
							MessageDialog.openError(UIUtils.getShell(),
									"Erreur d'impression",
									"Impossible d'imprimer le document");
						}
					});
				}
			});

			job.print(printDoc, null);

		}
	}
}
