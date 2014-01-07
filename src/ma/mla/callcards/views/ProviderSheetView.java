package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.forms.FormUtils;
import ma.mla.callcards.model.PrintableControl;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.ProviderPay;
import ma.mla.callcards.model.ProviderSheet;
import ma.mla.callcards.model.Purchase;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.Section;

public class ProviderSheetView extends DateFilteredView {

	public static final String ID = "ma.mla.view.provider.sheet";

	private Composite rootComposite;
	private TreeViewer purchasesTree;
	private TableViewer paysTable;
	private Text purchasesText;
	private Text paysText;
	private Text creditText;
	private Text oldCreditText;
	private PropertyChangeListener pcl;
	private String providerId;
	private String providerName;

	@Override
	public void createContent(Composite parent) {
		resolveClientInfo();
		setPartName(providerName);
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(2, false));

		createFinacialView(rootComposite);
		createPurchasesTree(rootComposite);
		createPaysTable(rootComposite);

		updateUI();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("purchases".equals(e.getPropertyName())
						|| "providerPays".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
	}

	private void createPurchasesTree(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("Liste des achats");
		purchasesTree = new TreeViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		Tree tree = purchasesTree.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		section.setClient(tree);
		purchasesTree.setContentProvider(new ITreeContentProvider() {

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
				if (o instanceof Purchase) {
					Purchase s = (Purchase) o;
					return s.getProducts().toArray();
				}
				return null;
			}
		});

		TreeViewerColumn tvc = new TreeViewerColumn(purchasesTree, SWT.NONE);
		TreeColumn col = tvc.getColumn();
		col.setText("Date");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Purchase) {
					Purchase s = (Purchase) element;
					return DataUtils.DATE_FORMAT.format(s.getDate());
				}
				return "";
			}

		});

		tvc = new TreeViewerColumn(purchasesTree, SWT.NONE);
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

		tvc = new TreeViewerColumn(purchasesTree, SWT.NONE);
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

		tvc = new TreeViewerColumn(purchasesTree, SWT.NONE);
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

		tvc = new TreeViewerColumn(purchasesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Prix");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProductSet) {
					ProductSet ps = (ProductSet) element;
					return String.valueOf(DataUtils.roundString(ps.getCost()));
				}
				return "";
			}
		});

		tvc = new TreeViewerColumn(purchasesTree, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Total");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Purchase) {
					Purchase s = (Purchase) element;
					return String.valueOf(s.getTotal());
				}
				return "";
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
				if (element instanceof ProviderPay) {
					ProviderPay cp = (ProviderPay) element;
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
				if (element instanceof ProviderPay) {
					ProviderPay cp = (ProviderPay) element;
					return String.valueOf(cp.getAmount());
				}
				return "";
			}
		});

	}

	private void createFinacialView(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		section.setText("Synthèse");
		final Composite cmp = new Composite(section, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		section.setClient(cmp);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		purchasesText = FormUtils.newInfoText(cmp, "Total Achats");
		purchasesText.setForeground(purchasesText.getDisplay().getSystemColor(
				SWT.COLOR_DARK_GREEN));
		paysText = FormUtils.newInfoText(cmp, "Total payé");
		paysText.setForeground(paysText.getDisplay().getSystemColor(
				SWT.COLOR_RED));
		oldCreditText = FormUtils.newInfoText(cmp, "Ancien crédit");
		creditText = FormUtils.newInfoText(cmp, "Total crédit");
	}

	protected void updateUI() {
		ProviderSheet sheet = StorageManager.getStorage().getProviderSheet(
				providerId, fromDate, toDate);
		purchasesTree.setInput(sheet.getPurchases());
		paysTable.setInput(sheet.getPays());
		double credit = sheet.getCredit();
		purchasesText.setText(DataUtils.roundString(sheet.getTotalPurchases()));
		paysText.setText(DataUtils.roundString(sheet.getTotalPays()));
		oldCreditText.setText(DataUtils.roundString(sheet.getOldCredit()));
		creditText.setText(DataUtils.roundString(credit));
		if (credit > 0) {
			creditText.setForeground(purchasesText.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		} else {
			creditText.setForeground(purchasesText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GREEN));
		}
	}

	private void resolveClientInfo() {
		String[] data = getViewSite().getSecondaryId().split("@");
		providerName = data[0];
		providerId = data[1];
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
		return Arrays.asList(new PrintableControl(purchasesTree,
				"Liste des ventes"), new PrintableControl(paysTable,
				"Liste des paiements"));
	}

	@Override
	public String getPrintTitle() {
		return "Fiche fournisseur : " + providerName;
	}
}
