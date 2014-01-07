package ma.mla.callcards.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.ClientSheet;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.SafePropertyListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

public class ClientsStatusView extends ViewPart {

	public static final String ID = "ma.mla.view.clients.status";

	private Composite rootComposite;
	private TableViewer clientsStateTable;
	private PropertyChangeListener pcl;

	private ClientsFinancielComposite financielComposite;

	@Override
	public void createPartControl(Composite parent) {
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout(1, false));

		createFinacialView(rootComposite);
		createTable(rootComposite);

		updateUI();

		pcl = new SafePropertyListener() {

			@Override
			public void safePropertyChange(PropertyChangeEvent e) {
				if ("sales".equals(e.getPropertyName())
						|| "clientPays".equals(e.getPropertyName())
						|| "clients".equals(e.getPropertyName())
						|| "*".equals(e.getPropertyName())) {
					updateUI();
				}
			}
		};
		StorageManager.getStorage().addChangeListener(pcl);
	}

	private void createTable(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setText("Détail par client");
		clientsStateTable = new TableViewer(section, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		final Table table = clientsStateTable.getTable();
		section.setClient(table);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		clientsStateTable.setContentProvider(new ArrayContentProvider());

		TableViewerColumn tvc = new TableViewerColumn(clientsStateTable,
				SWT.NONE);
		TableColumn col = tvc.getColumn();
		col.setText("Client");
		col.setWidth(120);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					return sheet.getClient().getName();
				}
				return "";
			}

		});

		tvc = new TableViewerColumn(clientsStateTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Total achats");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					return DataUtils.roundString(sheet.getTotalSales());
				}
				return "";
			}
		});

		tvc = new TableViewerColumn(clientsStateTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Payé");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					return DataUtils.roundString(sheet.getTotalPays());
				}
				return "";
			}
		});

		tvc = new TableViewerColumn(clientsStateTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Ancien crédit");
		col.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					return DataUtils.roundString(sheet.getOldCredit());
				}
				return "";
			}
		});

		tvc = new TableViewerColumn(clientsStateTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Total crédit");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					return DataUtils.roundString(sheet.getCredit());
				}
				return "";
			}
		});

		tvc = new TableViewerColumn(clientsStateTable, SWT.NONE);
		col = tvc.getColumn();
		col.setText("Bénéfice");
		col.setWidth(80);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					return DataUtils.roundString(sheet.getTotalSales()
							- sheet.getTotalCost());
				}
				return "";
			}

			@Override
			public Color getForeground(Object element) {
				if (element instanceof ClientSheet) {
					ClientSheet sheet = (ClientSheet) element;
					if (sheet.getTotalSales() < sheet.getTotalCost()) {
						return table.getDisplay().getSystemColor(SWT.COLOR_RED);
					}
				}
				return table.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
			}
		});

	}

	private void createFinacialView(Composite parent) {
		Section section = new Section(parent, Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		section.setText("Synthèse");
		financielComposite = new ClientsFinancielComposite(section, SWT.NONE);
		section.setClient(financielComposite);
		financielComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
	}

	private void updateUI() {
		List<ClientSheet> sheets = new ArrayList<ClientSheet>();
		for (ClientSheet sheet : StorageManager.getStorage().getClientSheets()) {
			if (sheet.getTotalSales() > 0.0 || sheet.getCredit() > 0.0) {
				sheets.add(sheet);
			}
		}
		clientsStateTable.setInput(sheets);
		financielComposite.updateUI();
	}

	@Override
	public void dispose() {
		StorageManager.getStorage().removeChangeListener(pcl);
		super.dispose();
	}

	@Override
	public void setFocus() {

	}

}
