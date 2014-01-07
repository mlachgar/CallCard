package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ma.mla.callcards.actions.PdfExportAction;
import ma.mla.callcards.actions.PrintAction;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.PrintableView;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("unchecked")
public class StockView extends ViewPart implements PrintableView {

	public static final String ID = "ma.mla.view.stock";
	private Composite rootComposite;
	private Text totalText;
	private TableViewer productsTable;
	private PropertyChangeListener pcl;

	@Override
	public void createPartControl(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));

		createTotalText(rootComposite);
		createProductsTable(rootComposite);

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("stock".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);

		updateUI();

		getViewSite().getActionBars().getToolBarManager()
				.add(new PrintAction(this));
		getViewSite().getActionBars().getToolBarManager()
				.add(new PdfExportAction(this));
	}

	private void createTotalText(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		section.setText("Synthése");
		section.setExpanded(true);
		final Composite cmp = new Composite(section, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		section.setClient(cmp);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		totalText = FormUtils.newInfoText(cmp, "Valeur totale");
	}

	private void createProductsTable(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("État du stock");
		productsTable = new TableViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		final Table table = productsTable.getTable();
		section.setClient(table);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		productsTable.setContentProvider(new ArrayContentProvider());
		productsTable.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				Map.Entry<Product, Integer> entry1 = (Map.Entry<Product, Integer>) e1;
				Map.Entry<Product, Integer> entry2 = (Map.Entry<Product, Integer>) e2;
				return DataUtils.compare(entry1.getKey(), entry2.getKey());
			}
		});

		TableViewerColumn tvc = new TableViewerColumn(productsTable, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Produit");
		col.setWidth(180);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Map.Entry<Product, Integer> entry = (Map.Entry<Product, Integer>) element;
				return entry.getKey().getName();
			}

			@Override
			public Color getForeground(Object element) {
				Map.Entry<Product, Integer> entry = (Map.Entry<Product, Integer>) element;
				return entry.getValue() < 0 ? table.getDisplay()
						.getSystemColor(SWT.COLOR_RED) : null;
			}
		});

		tvc = new TableViewerColumn(productsTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Quantité");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Map.Entry<Product, Integer> entry = (Map.Entry<Product, Integer>) element;
				return entry.getValue().toString();
			}

			@Override
			public Color getForeground(Object element) {
				Map.Entry<Product, Integer> entry = (Map.Entry<Product, Integer>) element;
				return entry.getValue() < 0 ? table.getDisplay()
						.getSystemColor(SWT.COLOR_RED) : null;
			}
		});

	}

	private void updateUI() {
		double total = 0.0;
		List<ProductSet> products = StorageManager.getStorage().getStockState()
				.getProductsStock();
		for (ProductSet set : products) {
			total += set.getCost();
		}
		totalText.setText(DataUtils.roundString(total));
		productsTable.setInput(StorageManager.getStorage()
				.getAvailableProducts().entrySet());
	}

	@Override
	public void setFocus() {
		productsTable.getTable().setFocus();
	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		return Arrays.asList(new PrintableControl(productsTable,
				getPrintTitle()));
	}

	@Override
	public String getPrintTitle() {
		return "État du Stock";
	}

}
