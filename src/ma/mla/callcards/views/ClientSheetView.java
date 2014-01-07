package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.ClientSheet;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.Section;

public class ClientSheetView extends DateFilteredView {

	public static final String ID = "ma.mla.view.client.sheet";

	private Composite rootComposite;
	private TreeViewer salesTree;
	private TableViewer paysTable;
	private Text totalSalesText;
	private Text gainText;
	private Text totalPaysText;
	private Text oldCreditText;
	private Text creditText;
	private PropertyChangeListener pcl;
	private String clientId;
	private String clientName;

	@Override
	public void createContent(Composite parent) {
		resolveClientInfo();
		setPartName(clientName);
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(2, false));

		createSummary(rootComposite);
		createSalesTree(rootComposite);
		createPaysTable(rootComposite);

		updateUI();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("sales".equals(e.getPropertyName())
						|| "clientPays".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
	}

	private void createSalesTree(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("Liste des achats");
		salesTree = new TreeViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		final Tree tree = salesTree.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		section.setClient(tree);
		salesTree.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean hasChildren(Object o) {
				return !(o instanceof ProductSet);
			}

			@Override
			public Object getParent(Object arg0) {
				return null;
			}

			@Override
			public Object[] getElements(Object o) {
				if (o instanceof Collection<?>) {
					Collection<?> c = (Collection<?>) o;
					return c.toArray();
				}
				return null;
			}

			@Override
			public Object[] getChildren(Object o) {
				if (o instanceof Sale) {
					Sale s = (Sale) o;
					return s.getSaleProducts().toArray();
				}
				return null;
			}
		});

		TreeViewerColumn tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		TreeColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale s = (Sale) element;
					return DataUtils.DATE_FORMAT.format(s.getDate());
				}
				return "";
			}

		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Produit");
		col.setWidth(140);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return ps.getProduct().getName();
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Quantité");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return String.valueOf(ps.getCount());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Prix Unit");
		col.setWidth(60);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return DataUtils.roundString(ps.getUnitPrice());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Prix");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return DataUtils.roundString(ps.getCost());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Total");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale s = (Sale) element;
					return DataUtils.roundString(s.getTotal());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Payé");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getPayed());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Reste");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getTotal() - cs.getPayed());
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(salesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Bénéfice");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					return DataUtils.roundString(cs.getTotal() - cs.getCost());
				}
				return "";
			}

			@Override
			public Color getForeground(Object element) {
				if (element instanceof Sale) {
					Sale cs = (Sale) element;
					if (cs.getTotal() < cs.getCost()) {
						return tree.getDisplay().getSystemColor(SWT.COLOR_RED);
					}
				}
				return tree.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
			}
		});

	}

	private void createPaysTable(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("Liste des paiements");
		paysTable = new TableViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		Table table = paysTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		section.setClient(table);
		paysTable.setContentProvider(new ArrayContentProvider());

		TableViewerColumn tvc = new TableViewerColumn(paysTable, SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientPay) {
					ClientPay cp = (ClientPay) element;
					return DataUtils.DATE_FORMAT.format(cp.getDate());
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(paysTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Montant");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientPay) {
					ClientPay cp = (ClientPay) element;
					return DataUtils.roundString(cp.getAmount());
				}
				return "";
			}
		});

	}

	private void createSummary(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		section.setText("Synthèse des achats");
		final Composite cmp = new Composite(section, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		section.setClient(cmp);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		totalSalesText = FormUtils.newInfoText(cmp, "Total Achats");
		gainText = FormUtils.newInfoText(cmp, "Total bénéfice");
		totalPaysText = FormUtils.newInfoText(cmp, "Total payé");
		oldCreditText = FormUtils.newInfoText(cmp, "Ancien crédit");
		creditText = FormUtils.newInfoText(cmp, "Total crédit");
	}

	protected void updateUI() {
		ClientSheet sheet = StorageManager.getStorage().getClientSheet(
				clientId, fromDate, toDate);
		salesTree.setInput(sheet.getSales());
		paysTable.setInput(sheet.getPays());
		double totalSales = sheet.getTotalSales();
		double totalCost = sheet.getTotalCost();
		double credit = sheet.getCredit();
		totalSalesText.setText(DataUtils.roundString(totalSales));
		gainText.setText(DataUtils.roundString(totalSales - totalCost));
		totalPaysText.setText(DataUtils.roundString(sheet.getTotalPays()));
		oldCreditText.setText(DataUtils.roundString(sheet.getOldCredit()));
		creditText.setText(DataUtils.roundString(credit));
		if (totalSales < totalCost) {
			gainText.setForeground(gainText.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		} else {
			gainText.setForeground(gainText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}
		if (credit > 0.0) {
			creditText.setForeground(creditText.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		} else {
			creditText.setForeground(creditText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}
	}

	private void resolveClientInfo() {
		String[] data = getViewSite().getSecondaryId().split("@");
		clientName = data[0];
		clientId = data[1];
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public List<PrintableControl> getPrintableControls() {
		return Arrays.asList(
				new PrintableControl(salesTree, "Liste des achats"),
				new PrintableControl(paysTable, "Liste des paiements"));
	}

	@Override
	public String getPrintTitle() {
		return "Fiche client : " + clientName;
	}

}
