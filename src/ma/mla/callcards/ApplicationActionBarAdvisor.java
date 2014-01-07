package ma.mla.callcards;

import ma.mla.callcards.actions.AddExpenseAction;
import ma.mla.callcards.actions.AddPayAction;
import ma.mla.callcards.actions.AddPurchaseAction;
import ma.mla.callcards.actions.AddSaleAction;
import ma.mla.callcards.actions.ContinueFolderAction;
import ma.mla.callcards.actions.CreateFolderAction;
import ma.mla.callcards.actions.EditInitDataAction;
import ma.mla.callcards.actions.OpenFolderAction;
import ma.mla.callcards.actions.SaveAsFolderAction;
import ma.mla.callcards.actions.SaveFolderAction;
import ma.mla.callcards.actions.ShowViewAction;
import ma.mla.callcards.dao.Storage;
import ma.mla.callcards.dao.StorageListener;
import ma.mla.callcards.dao.StorageManager;
import ma.mla.callcards.model.Pay.PersonType;
import ma.mla.callcards.utils.UIUtils;
import ma.mla.callcards.views.AccountsView;
import ma.mla.callcards.views.BookView;
import ma.mla.callcards.views.ClientsStatusView;
import ma.mla.callcards.views.FundView;
import ma.mla.callcards.views.ProductsView;
import ma.mla.callcards.views.PurchasesView;
import ma.mla.callcards.views.SalesView;
import ma.mla.callcards.views.StockView;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchWindow window;
	private ActionMenuManager windowsMenu;
	private ActionMenuManager folderMenu;
	private ActionMenuManager inputMenu;
	private CreateFolderAction createFolderAction;
	private OpenFolderAction openFolderAction;
	private SaveFolderAction saveFolderAction;
	private ShowViewAction showBookAction;
	private ShowViewAction showProductsAction;
	private ShowViewAction showAccountsAction;
	private ShowViewAction showSalesction;
	private ShowViewAction showPurchasesAction;
	private ShowViewAction showStockAction;
	private ShowViewAction showFundAction;
	private ShowViewAction showClientsStatusAction;
	private AddSaleAction addSaleAction;
	private AddPurchaseAction addPurchaseAction;
	private AddPayAction addClientPayAction;
	private AddPayAction addProviderPayAction;
	private AddExpenseAction addExpenseAction;
	private SaveAsFolderAction saveAsFolderAction;
	private ActionToolbarManager mainToolbar;

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		window = configurer.getWindowConfigurer().getWindow();
		StorageManager.addStorageListener(new StorageListener() {

			@Override
			public void folderChanged(Storage storage, ChangeType type) {
				UIUtils.async(new Runnable() {

					@Override
					public void run() {
						mainToolbar.update(true);
						folderMenu.update(true);
						inputMenu.update(true);
						windowsMenu.update(true);
					}
				});

			}
		});

	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		super.fillMenuBar(menuBar);
		windowsMenu = new ActionMenuManager("Fenêtres");
		windowsMenu
				.add(new ShowViewAction(window, BookView.ID, "person_16.png"));
		windowsMenu.add(new ShowViewAction(window, ProductsView.ID,
				"card_16.png"));
		windowsMenu.add(new ShowViewAction(window, AccountsView.ID,
				"calculate_16.png"));
		windowsMenu.add(new ShowViewAction(window, SalesView.ID, "bag_16.png"));
		windowsMenu.add(new ShowViewAction(window, PurchasesView.ID,
				"purchase_16.png"));
		windowsMenu
				.add(new ShowViewAction(window, StockView.ID, "stock_16.png"));
		windowsMenu.add(new ShowViewAction(window, FundView.ID, "pay_16.png"));
		windowsMenu.add(new ShowViewAction(window, ClientsStatusView.ID,
				"client_status_16.png"));

		folderMenu = new ActionMenuManager("Dossier");
		folderMenu.add(new CreateFolderAction(false));
		folderMenu.add(new OpenFolderAction(false));
		folderMenu.add(new SaveFolderAction(false));
		folderMenu.add(new SaveAsFolderAction(false));
		folderMenu.add(new ContinueFolderAction());
		folderMenu.add(ActionFactory.QUIT.create(window));

		inputMenu = new ActionMenuManager("Saisie");
		inputMenu.add(new AddSaleAction(false));
		inputMenu.add(new AddPurchaseAction(false));
		inputMenu.add(new AddPayAction(PersonType.CLIENT, false));
		inputMenu.add(new AddPayAction(PersonType.PROVIDER, false));
		inputMenu.add(new AddExpenseAction(false));
		inputMenu.add(new EditInitDataAction());

		menuBar.add(folderMenu);
		menuBar.add(inputMenu);
		menuBar.add(windowsMenu);

		folderMenu.update(true);
		inputMenu.update(true);
		windowsMenu.update(true);

	}

	@Override
	protected void makeActions(IWorkbenchWindow window) {
		createFolderAction = new CreateFolderAction(true);
		openFolderAction = new OpenFolderAction(true);
		saveFolderAction = new SaveFolderAction(true);
		saveAsFolderAction = new SaveAsFolderAction(true);
		showAccountsAction = new ShowViewAction(window, AccountsView.ID,
				"calculate_48.png");
		showBookAction = new ShowViewAction(window, BookView.ID,
				"person_48.png");
		showProductsAction = new ShowViewAction(window, ProductsView.ID,
				"card_48.png");
		showSalesction = new ShowViewAction(window, SalesView.ID, "bag_48.png");
		showPurchasesAction = new ShowViewAction(window, PurchasesView.ID,
				"purchase_48.png");
		showStockAction = new ShowViewAction(window, StockView.ID,
				"stock_48.png");
		showFundAction = new ShowViewAction(window, FundView.ID, "pay_48.png");
		showClientsStatusAction = new ShowViewAction(window,
				ClientsStatusView.ID, "client_status_48.png");
		addSaleAction = new AddSaleAction(true);
		addPurchaseAction = new AddPurchaseAction(true);
		addClientPayAction = new AddPayAction(PersonType.CLIENT, true);
		addProviderPayAction = new AddPayAction(PersonType.PROVIDER, true);
		addExpenseAction = new AddExpenseAction(true);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		mainToolbar = new ActionToolbarManager(SWT.FLAT | SWT.LEFT);
		coolBar.add(new ToolBarContributionItem(mainToolbar, "main")); //$NON-NLS-1$

		mainToolbar.add(createFolderAction);
		mainToolbar.add(openFolderAction);
		mainToolbar.add(saveFolderAction);
		mainToolbar.add(saveAsFolderAction);
		mainToolbar.add(new Separator());
		mainToolbar.add(showAccountsAction);
		mainToolbar.add(showBookAction);
		mainToolbar.add(showProductsAction);
		mainToolbar.add(showSalesction);
		mainToolbar.add(showPurchasesAction);
		mainToolbar.add(showStockAction);
		mainToolbar.add(showFundAction);
		mainToolbar.add(showClientsStatusAction);
		mainToolbar.add(new Separator());
		mainToolbar.add(addSaleAction);
		mainToolbar.add(addPurchaseAction);
		mainToolbar.add(addClientPayAction);
		mainToolbar.add(addProviderPayAction);
		mainToolbar.add(addExpenseAction);

		mainToolbar.update(true);
	}

}
