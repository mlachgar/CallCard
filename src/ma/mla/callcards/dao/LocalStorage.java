package ma.mla.callcards.dao;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import ma.mla.callcards.model.AccountsSummary;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.ClientSheet;
import ma.mla.callcards.model.Expense;
import ma.mla.callcards.model.InitData;
import ma.mla.callcards.model.Operator;
import ma.mla.callcards.model.Pay;
import ma.mla.callcards.model.Pay.PersonType;
import ma.mla.callcards.model.PersistentObject;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.ProductSetEntry;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.model.ProviderPay;
import ma.mla.callcards.model.ProviderSheet;
import ma.mla.callcards.model.Purchase;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.model.StockState;
import ma.mla.callcards.utils.DataUtils;

public class LocalStorage implements Storage {

	private List<Product> products = new ArrayList<Product>();
	private List<Client> clients = new ArrayList<Client>();
	private List<Provider> providers = new ArrayList<Provider>();
	private List<Purchase> purchases = new ArrayList<Purchase>();
	private List<Sale> sales = new ArrayList<Sale>();
	private List<ClientPay> clientPays = new ArrayList<ClientPay>();
	private List<ProviderPay> providerPays = new ArrayList<ProviderPay>();
	private InitData initData = new InitData();
	private StockState stockState = new StockState();
	private List<Expense> expenses = new ArrayList<Expense>();

	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	private Properties properties = new Properties();

	private boolean dirty = false;
	private String name = "";

	public LocalStorage() {
		init();
		dirty = true;
		this.name = "Nouveau dossier";
		properties.setProperty(META_FOLDER_NAME, name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void reset(String newName) {
		clearAll();
		init();
		this.name = newName;
		properties.setProperty(META_FOLDER_NAME, newName);
		dirty = true;
		support.firePropertyChange("*", 0, 1);
	}

	public void clearAll() {
		products.clear();
		clients.clear();
		providers.clear();
		purchases.clear();
		sales.clear();
		clientPays.clear();
		providerPays.clear();
		initData.clear();
		expenses.clear();
		stockState = new StockState();
	}

	@Override
	public void modifyProduct(Product p) {
		dirty = true;
		support.firePropertyChange("products", 0, 1);
	}

	@Override
	public void modifyClient(Client cl) {
		dirty = true;
		support.firePropertyChange("clients", 0, 1);
	}

	@Override
	public void modifyProvider(Provider p) {
		dirty = true;
		support.firePropertyChange("providers", 0, 1);
	}

	public String load(File folder) throws Exception {
		clearAll();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(folder, FN_METADATA)), "UTF-8"));
		StringBuilder problems = new StringBuilder();
		try {
			properties.load(in);
			this.name = properties.getProperty(META_FOLDER_NAME);
		} finally {
			in.close();
		}
		try {
			products = XMLParser.parseProducts(new File(folder, FN_PRODUCTS));
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des produits, données invalides\n");
		}
		try {
			clients = XMLParser.parseClients(new File(folder, FN_CLIENTS));
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des clients, données invalides\n");
		}
		try {
			providers = XMLParser
					.parseProviders(new File(folder, FN_PROVIDERS));
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des fornisseurs, données invalides\n");
		}
		Map<String, Product> productsMap = map(products);
		Map<String, Provider> providersMap = map(providers);
		Map<String, Client> clientsMap = map(clients);
		try {
			sales = XMLParser.parseSales(new File(folder, FN_SALES),
					productsMap, clientsMap);
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des ventes, données invalides\n");
		}
		try {
			purchases = XMLParser.parsePurchases(
					new File(folder, FN_PURCHASES), productsMap, providersMap);
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des achats, données invalides\n");
		}

		try {
			expenses = XMLParser.parseExpenses(new File(folder, FN_EXPENSES));
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des dépenses, données invalides\n");
		}

		clientPays = new ArrayList<ClientPay>();
		providerPays = new ArrayList<ProviderPay>();
		try {
			XMLParser.parsePays(new File(folder, FN_PAYS), clientsMap,
					providersMap, clientPays, providerPays);
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger la liste des paies, données invalides\n");
		}
		try {
			stockState = XMLParser.parseStock(new File(folder, FN_STOCK),
					productsMap);
			List<ProductSet> productsStock = new ArrayList<ProductSet>(
					stockState.getProductsStock());
			stockState.getProductsStock().clear();
			mergeProductSet(stockState.getProductsStock(), productsStock);
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger l'état du stock, données invalides\n");
		}
		try {
			XMLParser.parseInitData(new File(folder, FN_INIT_DATA), clientsMap,
					providersMap, initData);
		} catch (Exception e) {
			e.printStackTrace();
			problems.append("Impossible de charger les données initiales, données invalides\n");
		}
		dirty = false;
		support.firePropertyChange("*", 0, 1);
		return problems.toString();
	}

	public void save(File folder) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(folder,
				FN_METADATA));
		try {
			AccountsSummary summary = getAccountsSummary();
			properties.setProperty(META_TOTAL_STOCK,
					String.valueOf(summary.totalStock));
			properties.setProperty(META_CLIENT_CREDIT,
					String.valueOf(summary.totalClientCredit));
			properties.setProperty(META_PROVIDER_CREDIT,
					String.valueOf(summary.totalProviderCredit));
			properties.setProperty(META_TOTAL_PURCHASES,
					String.valueOf(summary.totalPurchases));
			properties.setProperty(META_TOTAL_CASH,
					String.valueOf(summary.totalCash));
			properties.setProperty(META_TOTAL_EXPENSES,
					String.valueOf(summary.totalExpenses));
			properties.setProperty(META_TOTAL_PROVIDER_PAYS,
					String.valueOf(summary.totalProviderPays));
			properties.setProperty(META_BALANCE,
					String.valueOf(summary.balance));
			properties.store(new OutputStreamWriter(out, "UTF-8"), "");
		} finally {
			out.close();
		}
		XMLBuilder.saveProducts(products, new File(folder, FN_PRODUCTS));
		XMLBuilder.saveClients(clients, new File(folder, FN_CLIENTS));
		XMLBuilder.saveProviders(providers, new File(folder, FN_PROVIDERS));
		XMLBuilder.saveSales(sales, new File(folder, FN_SALES));
		XMLBuilder.savePurchases(purchases, new File(folder, FN_PURCHASES));
		XMLBuilder
				.savePays(clientPays, providerPays, new File(folder, FN_PAYS));
		XMLBuilder.saveStock(stockState, new File(folder, FN_STOCK));
		XMLBuilder.saveInitData(initData, new File(folder, FN_INIT_DATA));
		XMLBuilder.saveExpenses(expenses, new File(folder, FN_EXPENSES));
		dirty = false;
	}

	private <T extends PersistentObject> Map<String, T> map(Collection<T> c) {
		Map<String, T> map = new HashMap<String, T>();
		for (T e : c) {
			map.put(e.getId(), e);
		}
		return map;
	}

	public boolean isDirty() {
		return dirty;
	}

	@Override
	public List<Product> getProducts() {
		if (products == null) {
			products = new ArrayList<Product>();
		}
		Collections.sort(products, new Comparator<Product>() {
			@Override
			public int compare(Product p1, Product p2) {
				return DataUtils.compare(p1, p2);
			}
		});
		return products;
	}

	@Override
	public List<Expense> getExpenses() {
		Collections.sort(expenses, DataUtils.DATEABLE_COPPARATOR);
		return expenses;
	}

	@Override
	public List<Client> getClients() {
		if (clients == null) {
			clients = new ArrayList<Client>();
		}
		return clients;
	}

	@Override
	public List<Provider> getProviders() {
		if (providers == null) {
			providers = new ArrayList<Provider>();
		}
		return providers;
	}

	@Override
	public List<ClientPay> getClientPays() {
		if (clientPays == null) {
			clientPays = new ArrayList<ClientPay>();
		}
		return clientPays;
	}

	@Override
	public double getTotalClientPays() {
		double total = 0.;
		for (ClientPay cp : getClientPays()) {
			total += cp.getAmount();
		}
		return total;
	}

	@Override
	public List<PersonAmount<Client>> getClientInitialCredits() {
		return initData.getClientCredits();
	}

	@Override
	public List<PersonAmount<Provider>> getProviderInitialCredits() {
		return initData.getProviderCredits();
	}

	@Override
	public void setInitData(InitData data) {
		this.initData = data;
		dirty = true;
		support.firePropertyChange("*", true, false);
	}

	@Override
	public void setStockProducts(Collection<ProductSet> products) {
		getStockState().setProductsStock(new ArrayList<ProductSet>(products));
		dirty = true;
		support.firePropertyChange("stock", true, false);
	}

	@Override
	public InitData getInitData() {
		return initData;
	}

	@Override
	public double getTotalProviderPays() {
		double total = 0.;
		for (ProviderPay pp : getProviderPays()) {
			total += pp.getAmount();
		}
		return total;
	}

	public double getTotalStock() {
		double total = 0.;
		for (ProductSet set : getStockState().getProductsStock()) {
			total += set.getCost();
		}
		return total;
	}

	public double getTotalSales() {
		double total = 0.;
		for (Sale s : getSales()) {
			total += s.getTotal();
		}
		return total;
	}

	public double getTotalPurchases() {
		double total = 0.;
		for (Purchase p : getPurchases()) {
			total += p.getTotal();
		}
		return total;
	}

	public double getTotalExpenses() {
		double total = 0.;
		for (Expense p : getExpenses()) {
			total += p.getAmount();
		}
		return total;
	}

	@Override
	public double getTotalCredit() {
		double credit = 0.0;
		for (ClientPay cp : getClientPays()) {
			credit -= cp.getAmount();

		}
		for (Sale s : getSales()) {
			credit += s.getTotal();
		}
		for (PersonAmount<Client> pa : getClientInitialCredits()) {
			credit += pa.getAmount();
		}
		return credit;
	}

	public double getTotalProviderCredit() {
		double credit = 0.0;
		for (ProviderPay cp : getProviderPays()) {
			credit -= cp.getAmount();

		}
		for (Purchase p : getPurchases()) {
			credit += p.getTotal();
		}
		for (PersonAmount<Provider> pa : getProviderInitialCredits()) {
			credit += pa.getAmount();
		}
		return credit;
	}

	public double getTotalGain() {
		double gain = 0.0;
		for (Sale s : getSales()) {
			gain += s.getTotal() - s.getCost();
		}
		return gain;
	}

	@Override
	public AccountsSummary getAccountsSummary() {
		AccountsSummary summary = new AccountsSummary();
		double totalProviderPays = getTotalProviderPays();
		summary.totalProviderPays = totalProviderPays;
		summary.totalStock = getTotalStock();
		summary.totalClientCredit = getTotalCredit();
		summary.totalCash = initData.getInitialCash() + getTotalClientPays()
				- getTotalExpenses() - totalProviderPays;
		summary.totalPurchases = getTotalPurchases();
		summary.totalProviderCredit = getTotalProviderCredit();
		summary.balance = -summary.totalProviderCredit + summary.totalStock
				+ summary.totalClientCredit + summary.totalCash;
		return summary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getProviderPays()
	 */
	@Override
	public List<ProviderPay> getProviderPays() {
		if (providerPays == null) {
			providerPays = new ArrayList<ProviderPay>();
		}
		return providerPays;
	}

	public List<ClientSheet> getClientSheets() {
		List<ClientSheet> sheets = new ArrayList<ClientSheet>();
		for (Client cl : getClients()) {
			sheets.add(getClientSheet(cl.getId(), null, null));
		}
		return sheets;
	}

	@Override
	public Map<Client, Double> getClientCredits() {
		Map<Client, Double> map = new HashMap<Client, Double>();
		for (ClientSheet sheet : getClientSheets()) {
			map.put(sheet.getClient(), Double.valueOf(sheet.getCredit()));
		}
		return map;
	}

	@Override
	public Map<Provider, Double> getProviderCredits() {
		Map<Provider, Double> map = new HashMap<Provider, Double>();
		for (Provider p : getProviders()) {
			ProviderSheet sheet = getProviderSheet(p.getId(), null, null);
			map.put(sheet.getProvider(), Double.valueOf(sheet.getCredit()));
		}
		return map;
	}

	public Client getClientById(String clientId) {
		for (Client cl : getClients()) {
			if (cl.getId().equals(clientId)) {
				return cl;
			}
		}
		return null;
	}

	public Provider getProviderById(String providerId) {
		for (Provider p : getProviders()) {
			if (p.getId().equals(providerId)) {
				return p;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getClientSheet(java.lang.String)
	 */
	@Override
	public ClientSheet getClientSheet(String clientId, Date fromDate,
			Date toDate) {
		ClientSheet sheet = new ClientSheet();
		sheet.setClient(getClientById(clientId));
		List<ClientPay> pays = new ArrayList<ClientPay>();
		List<Sale> sales = new ArrayList<Sale>();
		double oldCredit = 0.0;
		double credit = 0.0;
		double totalSales = 0.;
		double totalPays = 0.;
		double totalCost = 0.;
		if (clientId != null) {
			for (PersonAmount<Client> cc : initData.getClientCredits()) {
				if (clientId.equals(cc.getPerson().getId())) {
					credit += cc.getAmount();
					oldCredit += cc.getAmount();
				}
			}
			for (ClientPay cp : getClientPays()) {
				if (cp.getClient().getId().equals(clientId)) {
					credit -= cp.getAmount();
					if (DataUtils.isIncluded(cp, fromDate, toDate)) {
						pays.add(cp);
						totalPays += cp.getAmount();
					}
				}
			}
			for (Sale s : getSales()) {
				if (s.getClient().getId().equals(clientId)) {
					credit += s.getTotal();
					if (DataUtils.isIncluded(s, fromDate, toDate)) {
						sales.add(s);
						totalSales += s.getTotal();
						totalCost += s.getCost();
					}
				}
			}
		}
		sheet.setPays(pays);
		sheet.setSales(sales);
		sheet.setTotalPays(totalPays);
		sheet.setTotalSales(totalSales);
		sheet.setTotalCost(totalCost);
		sheet.setCredit(credit);
		sheet.setOldCredit(oldCredit);
		return sheet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getProviderSheet(java.lang.String)
	 */
	@Override
	public ProviderSheet getProviderSheet(String providerId, Date from, Date to) {
		ProviderSheet sheet = new ProviderSheet();
		sheet.setProvider(getProviderById(providerId));
		List<ProviderPay> pays = new ArrayList<ProviderPay>();
		List<Purchase> purchases = new ArrayList<Purchase>();
		sheet.setPays(pays);
		sheet.setPurchases(purchases);
		double totalPurchases = 0.;
		double totalPays = 0.;
		double credit = 0.;
		double oldCredit = 0.;
		if (providerId != null) {
			for (PersonAmount<Provider> pc : initData.getProviderCredits()) {
				if (providerId.equals(pc.getPerson().getId())) {
					credit += pc.getAmount();
					oldCredit += pc.getAmount();
				}
			}
			for (ProviderPay pp : getProviderPays()) {
				if (pp.getProvider().getId().equals(providerId)) {
					credit -= pp.getAmount();
					if (DataUtils.isIncluded(pp, from, to)) {
						pays.add(pp);
						totalPays += pp.getAmount();
					}
				}
			}
			for (Purchase p : getPurchases()) {
				if (p.getProvider().getId().equals(providerId)) {
					credit += p.getTotal();
					if (DataUtils.isIncluded(p, from, to)) {
						purchases.add(p);
						totalPurchases += p.getTotal();
					}
				}
			}
		}
		sheet.setTotalPays(totalPays);
		sheet.setTotalPurchases(totalPurchases);
		sheet.setCredit(credit);
		sheet.setOldCredit(oldCredit);
		return sheet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getProviderPays(java.lang.String)
	 */
	@Override
	public List<ProviderPay> getProviderPays(String providerId) {
		List<ProviderPay> result = new ArrayList<ProviderPay>();
		if (providerId != null) {
			for (ProviderPay cp : getProviderPays()) {
				if (cp.getProvider().getId().equals(providerId)) {
					result.add(cp);
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ma.mla.callcards.dao.Storage#addClient(ma.mla.callcards.model.Client)
	 */
	@Override
	public void addClient(Client client) {
		clients.add(client);
		dirty = true;
		support.firePropertyChange("clients", clients.size() - 1,
				clients.size());
	}

	@Override
	public void addExpense(Expense ex) {
		expenses.add(ex);
		dirty = true;
		support.firePropertyChange("expenses", expenses.size() - 1,
				expenses.size());
	}

	@Override
	public void removeExpenses(Collection<? extends Expense> toRemove) {
		removeAll(expenses, toRemove, "expenses");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ma.mla.callcards.dao.Storage#removeClient(ma.mla.callcards.model.Client)
	 */
	@Override
	public void removeClients(Collection<? extends Client> toRemove) {
		removeAll(clients, toRemove, "clients");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ma.mla.callcards.dao.Storage#addProvider(ma.mla.callcards.model.Provider)
	 */
	@Override
	public void addProvider(Provider provider) {
		providers.add(provider);
		dirty = true;
		support.firePropertyChange("providers", providers.size() - 1,
				providers.size());
	}

	@Override
	public void removeProviders(Collection<? extends Provider> toRemove) {
		removeAll(providers, toRemove, "providers");
		providers.remove(toRemove);
		dirty = true;
		support.firePropertyChange("providers", providers.size() + 1,
				providers.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ma.mla.callcards.dao.Storage#addProduct(ma.mla.callcards.model.Product)
	 */
	@Override
	public void addProduct(Product product) {
		products.add(product);
		dirty = true;
		support.firePropertyChange("products", products.size() - 1,
				products.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ma.mla.callcards.dao.Storage#removeProduct(ma.mla.callcards.model.Product
	 * )
	 */
	@Override
	public void removeProducts(Collection<? extends Product> toRemove) {
		removeAll(products, toRemove, "products");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#addSale(ma.mla.callcards.model.Sale)
	 */
	@Override
	public void addSale(Sale sale) {
		getSales();
		double cost = updateStockForAdd(sale);
		sale.setCost(cost);
		sales.add(sale);
		dirty = true;
		if (sale.getPayed() > 0.0) {
			ClientPay pay = new ClientPay();
			pay.setClient(sale.getClient());
			pay.setDate(sale.getDate());
			pay.setAmount(sale.getPayed());
			addPay(pay);
		}
		support.firePropertyChange("stock", true, false);
		support.firePropertyChange("sales", sales.size() - 1, sales.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ma.mla.callcards.dao.Storage#addPurchase(ma.mla.callcards.model.Purchase)
	 */
	@Override
	public void addPurchase(Purchase purchase) {
		purchases.add(purchase);
		dirty = true;
		if (updateStockForAdd(purchase)) {
			support.firePropertyChange("stock", true, false);
		}
		support.firePropertyChange("purchases", purchases.size() - 1,
				purchases.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#addPay(ma.mla.callcards.model.Pay)
	 */
	@Override
	public void addPay(Pay pay) {
		if (pay.getType() == PersonType.CLIENT) {
			getClientPays();
			clientPays.add((ClientPay) pay);
			dirty = true;
			support.firePropertyChange("clientPays", clientPays.size() - 1,
					clientPays.size());
		} else if (pay.getType() == PersonType.PROVIDER) {
			getProviderPays();
			providerPays.add((ProviderPay) pay);
			dirty = true;
			support.firePropertyChange("providerPays", providerPays.size() - 1,
					providerPays.size());
		}
	}

	@Override
	public void removePays(Collection<? extends Pay> pays) {
		removeAll(clientPays, pays, "clientPays");
		removeAll(providerPays, pays, "providerPays");
	}

	@Override
	public void removePurchases(Collection<? extends Purchase> purchases)
			throws Exception {
		if (canRemove(purchases)) {
			for (Purchase purchase : purchases) {
				updateStockForRemove(purchase);
			}
			removeAll(getPurchases(), purchases, "purchases");//
			support.firePropertyChange("stock", true, false);
		} else {
			if (purchases.size() == 1) {
				throw new Exception(
						"Impossible de supprimer l'achat car des produits ont déjà été vendus");
			} else {
				throw new Exception(
						"Impossible de supprimer les achats car des produits ont déjà été vendus");
			}
		}
	}

	@Override
	public void removeSales(Collection<? extends Sale> sales) {
		for (Sale sale : sales) {
			updateStockForRemove(sale);
		}
		removeAll(getSales(), sales, "sales");
		support.firePropertyChange("stock", true, false);
	}

	private <T> void removeAll(List<T> list, Collection<?> toRemove,
			String property) {
		int olSize = list.size();
		if (list.removeAll(toRemove)) {
			dirty = true;
			support.firePropertyChange(property, olSize, list.size());
		}
	}

	private double updateStockForAdd(Sale cs) {
		StockState state = getStockState();
		List<ProductSet> productsStock = state.getProductsStock();
		double cost = 0.0;
		for (ProductSet set : cs.getSaleProducts()) {
			Set<ProductSet> toRemove = new HashSet<ProductSet>();
			Product p = set.getProduct();
			int count = set.getCount();
			for (ProductSet ps : productsStock) {
				if (ps.getProduct().equals(p) && !toRemove.contains(ps)) {
					int available = ps.getCount();
					ProductSet purchaseSet = ps.copy();
					if (available > count) {
						cost += ps.getUnitPrice() * count;
						ps.setCount(available - count);
						purchaseSet.setCount(count);
						cs.getPurchaseProducts().add(purchaseSet);
						break;
					} else {
						cost += ps.getUnitPrice() * available;
						toRemove.add(ps);
						count -= available;
						purchaseSet.setCount(available);
						cs.getPurchaseProducts().add(purchaseSet);
					}
				}
			}
			productsStock.removeAll(toRemove);
		}
		return cost;
	}

	private boolean updateStockForAdd(Purchase purchase) {
		StockState stock = getStockState();
		mergeProductSet(stock.getProductsStock(), purchase.getProducts());
		return true;
	}

	private void mergeProductSet(List<ProductSet> list,
			Collection<ProductSet> toAdd) {
		Map<ProductSetEntry, ProductSet> map = new HashMap<ProductSetEntry, ProductSet>();
		for (ProductSet ps : list) {
			map.put(new ProductSetEntry(ps.getProduct(), ps.getUnitPrice()), ps);
		}
		for (ProductSet ps : toAdd) {
			ProductSetEntry key = new ProductSetEntry(ps.getProduct(),
					ps.getUnitPrice());
			ProductSet set = map.get(key);
			if (set != null) {
				set.setCount(set.getCount() + ps.getCount());
			} else {
				ProductSet copy = ps.copy();
				list.add(copy);
				map.put(new ProductSetEntry(ps.getProduct(), ps.getUnitPrice()),
						copy);
			}
		}
	}

	private boolean updateStockForRemove(Sale sale) {
		StockState stock = getStockState();
		mergeProductSet(stock.getProductsStock(), sale.getPurchaseProducts());
		return true;
	}

	private boolean canRemove(Collection<? extends Purchase> purchases) {
		StockState stock = getStockState();
		List<ProductSet> productsStock = stock.getProductsStock();
		List<ProductSet> purchaseSets = new ArrayList<ProductSet>();
		double totalPurchases = 0.0;
		double totalStock = 0.0;
		for (Purchase purchase : purchases) {
			purchaseSets.addAll(purchase.getProducts());
			totalPurchases += purchase.getTotal();
		}
		for (ProductSet set : purchaseSets) {
			Set<ProductSet> toRemove = new HashSet<ProductSet>();
			Product p = set.getProduct();
			int count = set.getCount();
			for (ProductSet stockSet : productsStock) {
				if (p.equals(stockSet.getProduct())
						&& set.getUnitPrice() == stockSet.getUnitPrice()) {
					int available = stockSet.getCount();
					if (available > count) {
						totalStock += stockSet.getUnitPrice() * count;
						break;
					} else {
						totalStock += stockSet.getUnitPrice() * available;
						toRemove.add(stockSet);
						count -= available;
					}
				}
			}
		}
		return totalPurchases <= totalStock;
	}

	private boolean updateStockForRemove(Purchase purchase) {
		StockState stock = getStockState();
		for (ProductSet set : purchase.getProducts()) {
			Set<ProductSet> toRemove = new HashSet<ProductSet>();
			Product p = set.getProduct();
			int count = set.getCount();
			List<ProductSet> productsStock = stock.getProductsStock();
			for (ProductSet stockSet : productsStock) {
				if (p.equals(stockSet.getProduct())
						&& set.getUnitPrice() == stockSet.getUnitPrice()) {
					int available = stockSet.getCount();
					if (available > count) {
						stockSet.setCount(available - count);
						break;
					} else {
						toRemove.add(stockSet);
						count -= available;
					}
				}
			}
			productsStock.removeAll(toRemove);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getStockState()
	 */
	@Override
	public StockState getStockState() {
		if (stockState == null) {
			stockState = new StockState();
		}
		return stockState;
	}

	public Map<Product, Integer> getAvailableProducts() {
		Map<Product, Integer> map = new HashMap<Product, Integer>();
		for (Product p : getProducts()) {
			map.put(p, Integer.valueOf(0));
		}
		List<ProductSet> stock = getStockState().getProductsStock();
		for (ProductSet ps : stock) {
			Product product = ps.getProduct();
			Integer oldCount = map.get(product);
			if (oldCount == null) {
				oldCount = Integer.valueOf(0);
			}
			map.put(product,
					Integer.valueOf(oldCount.intValue() + ps.getCount()));
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getPurchases()
	 */
	@Override
	public List<Purchase> getPurchases() {
		if (purchases == null) {
			purchases = new ArrayList<Purchase>();
		}
		return purchases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getSales()
	 */
	@Override
	public List<Sale> getSales() {
		if (sales == null) {
			sales = new ArrayList<Sale>();
		}
		return sales;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#getClientSales(java.lang.String)
	 */
	@Override
	public List<Sale> getClientSales(String clientId) {
		List<Sale> clientSales = new ArrayList<Sale>();
		for (Sale sale : getSales()) {
			if (sale.getClient().getId().equals(clientId)) {
				clientSales.add(sale);
			}
		}
		return clientSales;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#addChangeListener(java.beans.
	 * PropertyChangeListener)
	 */
	@Override
	public void addChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.mla.callcards.dao.Storage#removeChangeListener(java.beans.
	 * PropertyChangeListener)
	 */
	@Override
	public void removeChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	void init() {
		clientPays = new ArrayList<ClientPay>();
		providerPays = new ArrayList<ProviderPay>();
		clients = new ArrayList<Client>();
		// clients.add(new Client("Saad EL MEZIANI", "0612345678",
		// "Khouribga (Maroc)"));
		// clients.add(new Client("Ilyas EL MEZIANI", "0624680246",
		// "Khouribga (Maroc)"));
		clients.add(new Client("Ibrahim LACHGAR", "0636925814",
				"Melun (France)"));
		// clients.add(new Client("Oumayma LACHGAR", "0648260482",
		// "Melun (France)"));

		providers = new ArrayList<Provider>();
		providers
				.add(new Provider("Issam", "0522546589", "Casablanca (Maroc)"));
		// providers.add(new Provider("Méditel", "0523658745",
		// "Casablanca (Maroc)"));
		// providers.add(new Provider("Inwi", "0522958745",
		// "Casablanca (Maroc)"));

		stockState = new StockState();
		products = new ArrayList<Product>();
		double[] prices = { 10., 20., 50., 100., 200., 500., 1000. };
		for (Operator op : Operator.values()) {
			for (double p : prices) {
				Product product = new Product(op.getName() + " (" + (int) p
						+ ")", op, p, 7., 8.);
				products.add(product);
			}
			products.add(new Product(op.getName() + " (Pochette)", op, 250.0,
					245.0));
		}

		purchases = new ArrayList<Purchase>();
		// Calendar cal = Calendar.getInstance();
		// Random rand = new Random();
		// double[] percentes = { 6., 6.5, 7., 7.5, 8 };
		// for (int i = 0; i < 10; i++) {
		// cal.add(Calendar.DAY_OF_MONTH, -i);
		// Purchase p = new Purchase();
		// p.setDate(cal.getTime());
		// p.setProvider(providers.get(rand.nextInt(providers.size() - 1)));
		// List<ProductSet> set = new ArrayList<ProductSet>();
		// for (int j = 0; j < rand.nextInt(5) + 1; j++) {
		// ProductSet ps = new ProductSet();
		// ps.setPercent(percentes[rand.nextInt(percentes.length - 1)]);
		// ps.setCount(rand.nextInt(100) + 10);
		// ps.setProduct(products.get(rand.nextInt(products.size() - 1)));
		// set.add(ps);
		// }
		// p.setProducts(set);
		// purchases.add(p);
		// updateStock(p);
		// }

		sales = new ArrayList<Sale>();
		// cal = Calendar.getInstance();
		// for (int i = 0; i < 10; i++) {
		// cal.add(Calendar.DAY_OF_MONTH, -i);
		// for (int k = 0; k < rand.nextInt(6) + 1; k++) {
		// Sale s = new Sale();
		// s.setDate(cal.getTime());
		// s.setClient(clients.get(rand.nextInt(clients.size() - 1)));
		// List<ProductSet> set = new ArrayList<ProductSet>();
		// for (int j = 0; j < rand.nextInt(5) + 1; j++) {
		// ProductSet ps = new ProductSet();
		// Product product = products
		// .get(rand.nextInt(products.size() - 1));
		// ps.setPercent(percentes[rand.nextInt(percentes.length - 1)]);
		// ps.setCount(rand.nextInt(10) + 1);
		// ps.setProduct(product);
		// set.add(ps);
		// }
		//
		// s.setProducts(set);
		//
		// updateStock(s);
		// sales.add(s);
		// }
		// }
	}

	void initAll() {
		clientPays = new ArrayList<ClientPay>();
		providerPays = new ArrayList<ProviderPay>();
		clients = new ArrayList<Client>();
		clients.add(new Client("Saad EL MEZIANI", "0612345678",
				"Khouribga (Maroc)"));
		clients.add(new Client("Ilyas EL MEZIANI", "0624680246",
				"Khouribga (Maroc)"));
		clients.add(new Client("Ibrahim LACHGAR", "0636925814",
				"Melun (France)"));
		clients.add(new Client("Oumayma LACHGAR", "0648260482",
				"Melun (France)"));

		providers = new ArrayList<Provider>();
		providers.add(new Provider("Maroc Télécom", "0522546589",
				"Casablanca (Maroc)"));
		providers.add(new Provider("Méditel", "0523658745",
				"Casablanca (Maroc)"));
		providers.add(new Provider("Inwi", "0522958745", "Casablanca (Maroc)"));

		stockState = new StockState();
		products = new ArrayList<Product>();
		double[] prices = { 10., 20., 50., 100., 200., 500., 1000. };
		for (Operator op : Operator.values()) {
			for (double p : prices) {
				Product product = new Product(op.getName() + " (" + (int) p
						+ ")", op, p, 7., 8.);
				products.add(product);
			}
			products.add(new Product(op.getName() + " (Pochette)", op, 250.0,
					245.0));
		}

		purchases = new ArrayList<Purchase>();
		Calendar cal = Calendar.getInstance();
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			cal.add(Calendar.DAY_OF_MONTH, -i);
			Purchase p = new Purchase();
			p.setDate(cal.getTime());
			p.setProvider(providers.get(rand.nextInt(providers.size())));
			List<ProductSet> set = new ArrayList<ProductSet>();
			for (int j = 0; j < rand.nextInt(5) + 1; j++) {
				ProductSet ps = new ProductSet();
				Product product = products.get(rand.nextInt(products.size()));
				double percent = product.getDefaultPurchaseValue();
				ps.setCount(rand.nextInt(500) + 10);
				ps.setUnitPrice(product.getPrice() * (1.0 - (percent / 100.0)));
				ps.setProduct(product);
				set.add(ps);
			}
			p.setProducts(set);
			purchases.add(p);
			updateStockForAdd(p);
		}

		sales = new ArrayList<Sale>();
		cal = Calendar.getInstance();
		for (int i = 0; i < 10; i++) {
			cal.add(Calendar.DAY_OF_MONTH, -i);
			for (int k = 0; k < rand.nextInt(6) + 1; k++) {
				Sale s = new Sale();
				s.setDate(cal.getTime());
				s.setClient(clients.get(rand.nextInt(clients.size())));
				for (int j = 0; j < rand.nextInt(5) + 1; j++) {
					List<ProductSet> productsStock = new ArrayList<ProductSet>(
							stockState.getProductsStock());
					Collections.shuffle(productsStock);
					for (ProductSet prodSet : productsStock) {
						if (prodSet.getCount() > 1) {
							Product product = prodSet.getProduct();
							ProductSet saleSet = prodSet.copy();
							saleSet.setCount(rand.nextInt(prodSet.getCount()) + 1);
							saleSet.setUnitPrice(product.getPrice()
									* (1.0 - (product.getDefaultSaleValue() / 100.0)));
							s.getSaleProducts().add(saleSet);
							break;
						}
					}
				}
				if (s.getTotal() > 0) {
					double cost = updateStockForAdd(s);
					s.setCost(cost);
					sales.add(s);
				}
			}
		}
		cal = Calendar.getInstance();
		for (Client client : clients) {
			double totalPays = 0.0;
			for (int j = 0; j < 3; j++) {
				cal.add(Calendar.DAY_OF_MONTH, -rand.nextInt(10));
				ClientPay pay = new ClientPay();
				pay.setDate(cal.getTime());
				ClientSheet sheet = getClientSheet(client.getId(), null, null);
				double rest = sheet.getTotalSales();
				if (rest > totalPays) {
					pay.setClient(client);
					double amount = rand.nextDouble() * (rest - totalPays);
					pay.setAmount(amount);
					totalPays += amount;
					clientPays.add(pay);
				}
			}
		}
		cal = Calendar.getInstance();
		for (Provider provider : providers) {
			double totalPays = 0.0;
			for (int i = 0; i < 3; i++) {
				cal.add(Calendar.DAY_OF_MONTH, -rand.nextInt(10));
				ProviderPay pay = new ProviderPay();
				pay.setDate(cal.getTime());
				ProviderSheet sheet = getProviderSheet(provider.getId(), null,
						null);
				double rest = sheet.getTotalPurchases();
				if (rest > totalPays) {
					pay.setProvider(provider);
					double amount = rand.nextDouble() * (rest - totalPays);
					pay.setAmount(amount);
					totalPays += amount;
					providerPays.add(pay);
				}
			}
		}
	}

	public void continueInNewFolder(String name) {
		this.name = name;

		initData.clear();
		for (ClientSheet sheet : getClientSheets()) {
			if (sheet.getCredit() > 0.0) {
				initData.getClientCredits().add(
						new PersonAmount<Client>(sheet.getClient(), sheet
								.getCredit()));
			}
		}
		for (Provider p : providers) {
			ProviderSheet sheet = getProviderSheet(p.getId(), null, null);
			if (sheet.getCredit() > 0.0) {
				initData.getProviderCredits().add(
						new PersonAmount<Provider>(sheet.getProvider(), sheet
								.getCredit()));
			}
		}
		initData.setInitialCash(getAccountsSummary().totalCash);
		sales.clear();
		purchases.clear();
		clientPays.clear();
		providerPays.clear();
		expenses.clear();
		dirty = true;
		support.firePropertyChange("*", true, false);
	}
}
