package ma.mla.callcards;

import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.views.AccountsView;
import ma.mla.callcards.views.BookView;
import ma.mla.callcards.views.ClientsStatusView;
import ma.mla.callcards.views.FundView;
import ma.mla.callcards.views.ProductsView;
import ma.mla.callcards.views.PurchasesView;
import ma.mla.callcards.views.SalesView;
import ma.mla.callcards.views.StockView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		IFolderLayout left = layout.createFolder("LEFT", IPageLayout.LEFT,
				0.25f, IPageLayout.ID_EDITOR_AREA);

		IFolderLayout main = layout.createFolder("MAIN", IPageLayout.RIGHT,
				0.75f, IPageLayout.ID_EDITOR_AREA);

		if (StorageManager.getStorage() != null) {
			left.addView(ProductsView.ID);
			left.addView(BookView.ID);
			main.addView(AccountsView.ID);
			main.addView(PurchasesView.ID);
			main.addView(SalesView.ID);
			main.addView(StockView.ID);
			main.addView(FundView.ID);
			main.addView(ClientsStatusView.ID);
		} else {
			left.addPlaceholder(ProductsView.ID);
			left.addPlaceholder(BookView.ID);
			main.addPlaceholder(AccountsView.ID);
			main.addPlaceholder(PurchasesView.ID);
			main.addPlaceholder(SalesView.ID);
			main.addPlaceholder(StockView.ID);
			main.addPlaceholder(FundView.ID);
			main.addPlaceholder(ClientsStatusView.ID);
		}
	}
}
