package ma.mla.callcards.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class UIUtils {

	public static void error(final String title, final String msg) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openError(getShell(), title, msg);
			}
		});

	}

	public static void warning(final String title, final String msg) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openWarning(getShell(), title, msg);
			}
		});

	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getDisplay().getActiveShell();
	}

	public static void async(Runnable r) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(r);
	}

	public static Text newDoubleText(Composite parent, final double min,
			final double max, final Acceptor<Double> acceptor) {
		final Text input = new Text(parent, SWT.BORDER);
		input.addModifyListener(new ModifyListener() {

			String previous = "";
			private boolean busy = false;

			@Override
			public void modifyText(ModifyEvent e) {
				if (!busy) {
					String text = input.getText();
					if (!text.isEmpty()) {
						try {
							double v = Double.parseDouble(text);
							if ((min != Double.NaN && v < min)
									|| (max != Double.NaN && v > max)) {
								reject();
							} else {
								previous = text;
								accept(Double.valueOf(v));
							}
						} catch (NumberFormatException ex) {
							reject();
						}
					} else {
						previous = "";
						accept(null);
					}
				}
			}

			private void accept(Double v) {
				if (acceptor != null) {
					acceptor.accept(v);
				}
			}

			private void reject() {
				busy = true;
				input.setText(previous);
				input.setSelection(previous.length());
				busy = false;
			}
		});
		return input;
	}

	public static Text newDoubleText(Composite parent,
			final Validator<Double> validator) {
		final Text input = new Text(parent, SWT.BORDER);
		input.addModifyListener(new ModifyListener() {

			String previous = "";
			private boolean busy = false;

			@Override
			public void modifyText(ModifyEvent e) {
				if (!busy) {
					String text = input.getText();
					if (!text.isEmpty()) {
						try {
							double v = Double.parseDouble(text);
							if (!validator.isValid(v)) {
								reject();
							} else {
								previous = text;
								accept(Double.valueOf(v));
							}
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
							reject();
						}
					} else {
						previous = "";
						accept(null);
					}
				}
			}

			private void accept(Double v) {
				if (validator != null) {
					validator.accept(v);
				}
			}

			private void reject() {
				busy = true;
				input.setText(previous);
				input.setSelection(previous.length());
				busy = false;
			}
		});
		return input;
	}

	public static Label newLabel(Composite parent, String text, int width) {
		Label label = new Label(parent, SWT.CENTER);
		label.setAlignment(SWT.CENTER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd.widthHint = width;
		label.setLayoutData(gd);
		label.setText(text);
		return label;
	}

	public static Text newBorderLabel(Composite parent, String text, int width) {
		Text label = new Text(parent, SWT.BORDER);
		label.setEditable(false);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd.widthHint = width;
		label.setLayoutData(gd);
		label.setText(text);
		return label;
	}

	public static Text newText(Composite parent, String name) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false, false));
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return text;
	}

	public static Text newDoubleText(Composite parent, String name,
			Acceptor<Double> acceptor) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false, false));
		Text text = UIUtils.newDoubleText(parent, Double.NaN, Double.NaN,
				acceptor);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return text;
	}

	public static Text newDoubleText(Composite parent, String name, double min,
			double max, Acceptor<Double> acceptor) {
		if (name != null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(name);
			label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false,
					false));
		}
		Text text = UIUtils.newDoubleText(parent, min, max, acceptor);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return text;
	}

	public static Text newTextArea(Composite parent, String name, int nbLine) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false, false));
		Text text = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = text.getLineHeight() * nbLine;
		text.setLayoutData(gd);
		return text;
	}

	// public static <T extends NamedObject> ComboViewer newProposalCombo(
	// Composite parent, String name, final List<T> items,
	// final Acceptor<T> call) {
	// final ComboViewer combo = newCombo(parent, name, SWT.BORDER);
	// ComboContentAdapter comboAdapt = new ComboContentAdapter();
	// final GenericProposalProvider<T> provider = new
	// GenericProposalProvider<T>(
	// items);
	// final CloseableContentProposalAdapter adapt = new
	// CloseableContentProposalAdapter(
	// combo.getControl(), comboAdapt, provider, null, null);
	// adapt.addContentProposalListener(new IContentProposalListener() {
	//
	// @Override
	// public void proposalAccepted(IContentProposal p) {
	// T cl = provider.extractItem(p);
	// combo.setSelection(new StructuredSelection(cl), false);
	// call.accept(cl);
	// }
	// });
	//
	// combo.addPostSelectionChangedListener(new ISelectionChangedListener() {
	//
	// @Override
	// public void selectionChanged(SelectionChangedEvent event) {
	// adapt.hide();
	// call.accept((T) UIUtils.getFirstSelected(event.getSelection(),
	// Object.class));
	// }
	// });
	// combo.setContentProvider(new ArrayContentProvider());
	// combo.setInput(items);
	// return combo;
	// }

	@SuppressWarnings("unchecked")
	public static <T> ComboViewer newCombo(Composite parent, String name,
			final List<T> items, final Acceptor<T> call) {
		if (name != null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(name);
			label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false,
					false));
		}
		ComboViewer cv = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		Combo combo = cv.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		cv.setContentProvider(new ArrayContentProvider());
		cv.setInput(items);
		cv.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					IStructuredSelection ss = (IStructuredSelection) event
							.getSelection();
					call.accept((T) ss.getFirstElement());
				}
			}
		});
		if (items.size() == 1) {
			cv.setSelection(new StructuredSelection(items.get(0)));
		}
		return cv;
	}

	public static ComboViewer newCombo(Composite parent, String name, int style) {
		if (name != null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(name);
			label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false,
					false));
		}
		ComboViewer cv = new ComboViewer(parent, style);
		Combo combo = cv.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		cv.setContentProvider(new ArrayContentProvider());
		return cv;
	}

	public static DateTime newDate(Composite parent, String name) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false, false));
		DateTime dt = new DateTime(parent, SWT.DATE | SWT.DROP_DOWN);
		dt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		return dt;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFirstSelected(ISelection s, Class<T> clazz) {
		if (!s.isEmpty()) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object firstElement = ss.getFirstElement();
			if (clazz.isAssignableFrom(firstElement.getClass())) {
				return (T) firstElement;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getSelectedObjects(ISelection s, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		if (!s.isEmpty()) {
			IStructuredSelection ss = (IStructuredSelection) s;
			for (Object o : ss.toArray()) {
				if (clazz.isAssignableFrom(o.getClass())) {
					list.add((T) o);
				}
			}
		}
		return list;
	}

	public static Set<?> getSelectionSet(ISelection s) {
		Set<Object> set = new HashSet<Object>();
		if (!s.isEmpty()) {
			IStructuredSelection ss = (IStructuredSelection) s;
			set.addAll(Arrays.asList(ss.toArray()));
		}
		return set;
	}

	public static void print(Control c) {
		// On récupère les informations d'impression:
		PrintDialog dialog = new PrintDialog(c.getShell());
		PrinterData data = dialog.open();

		// On définit une image du controle à imprimer:
		Point size = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Image printImage = new Image(c.getDisplay(), size.x, size.y);
		GC gcControl = new GC(c);
		gcControl.copyArea(printImage, 0, 0);

		Printer printer = new Printer(data);
		ImageData imageData = printImage.getImageData();

		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point printerDPI = printer.getDPI();
		Point displayDPI = c.getDisplay().getDPI();

		// On calcule le facteur d'agrandissement à appliquer à l'impression:
		int scaleFactor = printerDPI.x / displayDPI.x;

		printer.startJob("Print");
		printer.startPage();

		// On va "peindre" l'image retaillée dans notre objet Printer:
		Image printedImage = new Image(printer, imageData);
		GC gcPrinter = new GC(printer);
		gcPrinter.drawImage(printedImage, 0, 0, imageData.width,
				imageData.height, -trim.x, -trim.y, scaleFactor
						* imageData.width, scaleFactor * imageData.height);
		printer.endPage();
		printer.endJob();

		// On libère les ressources utilisées:
		printer.dispose();
		gcPrinter.dispose();
		gcControl.dispose();
		printImage.dispose();
		printedImage.dispose();
	}

	public static Point getCenterLocation(Point size) {
		Rectangle bounds = PlatformUI.getWorkbench().getDisplay()
				.getPrimaryMonitor().getBounds();
		int x = bounds.x + (bounds.width - size.x) / 2;
		int y = bounds.y + (bounds.height - size.y) / 2;
		return new Point(x, y);
	}

	public static void center(Shell shell) {
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

}
