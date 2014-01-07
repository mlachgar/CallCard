package ma.mla.callcards.dao;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.Expense;
import ma.mla.callcards.model.InitData;
import ma.mla.callcards.model.PersonAmount;
import ma.mla.callcards.model.Product;
import ma.mla.callcards.model.ProductSet;
import ma.mla.callcards.model.ProductType;
import ma.mla.callcards.model.Provider;
import ma.mla.callcards.model.ProviderPay;
import ma.mla.callcards.model.Purchase;
import ma.mla.callcards.model.Sale;
import ma.mla.callcards.model.StockState;
import ma.mla.callcards.utils.DataUtils;

public class XMLBuilder {

	public static final String TAG_CLIENTS = "clients";
	public static final String TAG_PROVIDERS = "providers";
	public static final String TAG_PRODUCTS = "products";
	public static final String TAG_SALES = "sales";
	public static final String TAG_PURCHASES = "purchases";
	public static final String TAG_PAYS = "pays";
	public static final String TAG_EXPENSES = "expenses";

	private StringBuilder sb;

	public XMLBuilder() {
		sb = new StringBuilder();
	}

	public void attr(String name, String value) {
		sb.append(" ").append(name).append("=\"");
		sb.append(value).append("\"");
	}

	public void attr(String name, double value) {
		sb.append(" ").append(name).append("=\"");
		sb.append(value).append("\"");
	}

	public void attr(String name, int value) {
		sb.append(" ").append(name).append("=\"");
		sb.append(value).append("\"");
	}

	public void attr(String name, long value) {
		sb.append(" ").append(name).append("=\"");
		sb.append(value).append("\"");
	}

	public void attr(String name, Date value) {
		sb.append(" ").append(name).append("=\"");
		sb.append(DataUtils.DATE_FORMAT.format(value)).append("\"");
	}

	public void endTag(String tagName) {
		sb.append("</").append(tagName).append(">");
	}

	public void startTag(String tagName) {
		sb.append("<").append(tagName).append(">");
	}

	public void openTag(String tagName) {
		sb.append("<").append(tagName);
	}

	public void closeTag() {
		sb.append(">");
	}

	public void endTag() {
		sb.append("/>");
	}

	public void productSet(ProductSet ps) {
		openTag(ProductSet.TAG_NAME);
		attr(ProductSet.PROP_PRODUCT_ID, ps.getProduct().getId());
		attr(ProductSet.PROP_COUNT, ps.getCount());
		attr(ProductSet.PROP_UNIT_PRICE, ps.getUnitPrice());
		endTag();
	}

	public String build() {
		return sb.toString();
	}

	public void writeTo(File file) throws IOException {
		PrintStream stream = new PrintStream(file, "UTF-8");
		stream.print(sb.toString());
		stream.close();
	}

	public static void saveClients(List<Client> clients, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_CLIENTS);
		if (clients != null) {
			for (Client client : clients) {
				b.openTag(Client.TAG_NAME);
				b.attr(Client.PROP_ID, client.getId());
				b.attr(Client.PROP_NAME, client.getName());
				b.attr(Client.PROP_PHONE_NUMBER, client.getPhoneNumber());
				b.attr(Client.PROP_ADDRESS, client.getAddress());
				b.endTag();
			}
		}
		b.endTag(TAG_CLIENTS);
		b.writeTo(file);
	}

	public static void saveProviders(List<Provider> providers, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_PROVIDERS);
		if (providers != null) {
			for (Provider provider : providers) {
				b.openTag(Provider.TAG_NAME);
				b.attr(Provider.PROP_ID, provider.getId());
				b.attr(Provider.PROP_NAME, provider.getName());
				b.attr(Provider.PROP_PHONE_NUMBER, provider.getPhoneNumber());
				b.attr(Provider.PROP_ADDRESS, provider.getAddress());
				b.endTag();
			}
		}
		b.endTag(TAG_PROVIDERS);
		b.writeTo(file);
	}

	public static void saveProducts(List<Product> products, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_PRODUCTS);
		if (products != null) {
			for (Product product : products) {
				b.openTag(Product.TAG_NAME);
				b.attr(Product.PROP_TYPE, product.getType().name());
				b.attr(Product.PROP_ID, product.getId());
				b.attr(Product.PROP_NAME, product.getName());
				b.attr(Product.PROP_OPEARTOR, product.getOperator().name());
				b.attr(Product.PROP_PURCHASE_PERCENT,
						product.getDefaultPurchaseValue());
				b.attr(Product.PROP_SALE_PERCENT, product.getDefaultSaleValue());
				if (product.getType() == ProductType.CARD) {
					b.attr(Product.PROP_PRICE, product.getPrice());
				}
				b.endTag();
			}
		}
		b.endTag(TAG_PRODUCTS);
		b.writeTo(file);
	}

	public static void saveStock(StockState stock, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.openTag(StockState.TAG_NAME);
		if (stock == null) {
			stock = new StockState();
		}
		b.closeTag();
		for (ProductSet ps : stock.getProductsStock()) {
			b.productSet(ps);
		}
		b.endTag(StockState.TAG_NAME);
		b.writeTo(file);
	}

	public static void saveInitData(InitData data, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.openTag(InitData.TAG_NAME);
		if (data == null) {
			data = new InitData();
		}
		b.attr(InitData.PROP_INITIAL_CASH, data.getInitialCash());
		b.closeTag();
		for (PersonAmount<Client> credit : data.getClientCredits()) {
			b.openTag(PersonAmount.CLIENT_TAG_NAME);
			b.attr(PersonAmount.PROP_CLIENT_ID, credit.getPerson().getId());
			b.attr(PersonAmount.PROP_AMOUNT, credit.getAmount());
			b.endTag();
		}
		for (PersonAmount<Provider> credit : data.getProviderCredits()) {
			b.openTag(PersonAmount.PROVIDER_TAG_NAME);
			b.attr(PersonAmount.PROP_PROVIDER_ID, credit.getPerson().getId());
			b.attr(PersonAmount.PROP_AMOUNT, credit.getAmount());
			b.endTag();
		}
		b.endTag(InitData.TAG_NAME);
		b.writeTo(file);
	}

	public static void saveSales(List<Sale> sales, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_SALES);
		if (sales != null) {
			for (Sale sale : sales) {
				b.openTag(Sale.TAG_NAME);
				b.attr(Sale.PROP_DATE, sale.getDate());
				b.attr(Sale.PROP_CLIENT_ID, sale.getClient().getId());
				b.attr(Sale.PROP_COST, sale.getCost());
				b.attr(Sale.PROP_PAYED, sale.getPayed());
				b.closeTag();
				b.startTag(Sale.TAG_SALE_PRODUCTS);
				for (ProductSet set : sale.getSaleProducts()) {
					b.productSet(set);
				}
				b.endTag(Sale.TAG_SALE_PRODUCTS);
				b.startTag(Sale.TAG_PURCHASE_PRODUCTS);
				for (ProductSet set : sale.getPurchaseProducts()) {
					b.productSet(set);
				}
				b.endTag(Sale.TAG_PURCHASE_PRODUCTS);
				b.endTag(Sale.TAG_NAME);
			}
		}
		b.endTag(TAG_SALES);
		b.writeTo(file);
	}

	public static void savePurchases(List<Purchase> purchases, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_PURCHASES);
		if (purchases != null) {
			for (Purchase purchase : purchases) {
				b.openTag(Purchase.TAG_NAME);
				b.attr(Purchase.PROP_DATE, purchase.getDate());
				b.attr(Purchase.PROP_PROVIDER_ID, purchase.getProvider()
						.getId());
				b.closeTag();
				for (ProductSet set : purchase.getProducts()) {
					b.productSet(set);
				}
				b.endTag(Purchase.TAG_NAME);
			}
		}
		b.endTag(TAG_PURCHASES);
		b.writeTo(file);
	}

	public static void savePays(List<ClientPay> clientPays,
			List<ProviderPay> providerPays, File file) throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_PAYS);
		if (clientPays != null) {
			for (ClientPay pay : clientPays) {
				b.openTag(ClientPay.TAG_NAME);
				b.attr(ClientPay.PROP_DATE, pay.getDate());
				b.attr(ClientPay.PROP_CLIENT_ID, pay.getClient().getId());
				b.attr(ClientPay.PROP_AMOUNT, pay.getAmount());
				b.endTag();
			}
		}
		if (providerPays != null) {
			for (ProviderPay pay : providerPays) {
				b.openTag(ProviderPay.TAG_NAME);
				b.attr(ProviderPay.PROP_DATE, pay.getDate());
				b.attr(ProviderPay.PROP_PROVIDER_ID, pay.getProvider().getId());
				b.attr(ProviderPay.PROP_AMOUNT, pay.getAmount());
				b.endTag();
			}
		}
		b.endTag(TAG_PAYS);
		b.writeTo(file);
	}

	public static void saveExpenses(List<Expense> expenses, File file)
			throws IOException {
		XMLBuilder b = new XMLBuilder();
		b.startTag(TAG_EXPENSES);
		if (expenses != null) {
			for (Expense ex : expenses) {
				b.openTag(Expense.TAG_NAME);
				b.attr(Sale.PROP_DATE, ex.getDate());
				b.attr(Expense.PROP_AMOUNT, ex.getAmount());
				b.endTag();
			}
		}
		b.endTag(TAG_EXPENSES);
		b.writeTo(file);
	}

}
