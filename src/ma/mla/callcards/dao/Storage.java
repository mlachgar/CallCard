package ma.mla.callcards.dao;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ma.mla.callcards.model.AccountsSummary;
import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.ClientSheet;
import ma.mla.callcards.model.Expense;
import ma.mla.callcards.model.InitData;
import ma.mla.callcards.model.Pay;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.model.ProviderPay;
import ma.mla.callcards.model.ProviderSheet;
import ma.mla.callcards.model.Purchase;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.model.StockState;

public interface Storage {

	public List<Product> getProducts();

	public List<Client> getClients();

	public List<Provider> getProviders();

	public List<ClientPay> getClientPays();

	public double getTotalClientPays();

	public double getTotalProviderPays();

	public List<ProviderPay> getProviderPays();

	public ClientSheet getClientSheet(String clientId, Date fromDate,
			Date toDate);

	public ProviderSheet getProviderSheet(String providerId, Date fromDate,
			Date toDate);

	public List<ProviderPay> getProviderPays(String providerId);

	public void addClient(Client client);

	void removeClients(Collection<? extends Client> clients);

	public void addProvider(Provider provider);

	public void removeProviders(Collection<? extends Provider> providers);

	public void addProduct(Product product);

	public void removeProducts(Collection<? extends Product> products);

	public void addSale(Sale sale);

	public void removeSales(Collection<? extends Sale> sales);

	public void addPurchase(Purchase purchase);

	public void removePurchases(Collection<? extends Purchase> purchases)
			throws Exception;

	public void addPay(Pay pay);

	void removePays(Collection<? extends Pay> pays);

	public StockState getStockState();

	public List<Purchase> getPurchases();

	public List<Sale> getSales();

	public List<Sale> getClientSales(String clientId);

	public void addChangeListener(PropertyChangeListener pcl);

	public void removeChangeListener(PropertyChangeListener pcl);

	void reset(String newName);

	String load(File folder) throws Exception;

	boolean isDirty();

	void save(File folder) throws IOException;

	String getName();

	Map<Product, Integer> getAvailableProducts();

	List<ClientSheet> getClientSheets();

	void modifyProduct(Product p);

	void modifyClient(Client cl);

	void modifyProvider(Provider p);

	AccountsSummary getAccountsSummary();

	List<PersonAmount<Client>> getClientInitialCredits();

	List<PersonAmount<Provider>> getProviderInitialCredits();

	void setInitData(InitData data);

	InitData getInitData();

	double getTotalCredit();

	void setStockProducts(Collection<ProductSet> products);

	List<Expense> getExpenses();

	void addExpense(Expense ex);

	void removeExpenses(Collection<? extends Expense> toRemove);

	Map<Client, Double> getClientCredits();

	Map<Provider, Double> getProviderCredits();

}