package ma.mla.callcards.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ma.mla.callcards.model.Client;
import ma.mla.callcards.model.ClientPay;
import ma.mla.callcards.model.Expense;
import ma.mla.callcards.model.InitData;
import ma.mla.callcards.model.Operator;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public abstract class XMLParser {

	private static interface DocumentHandler {
		void handleDocument(Document doc) throws ParseException;
	}

	public static void parse(File file, DocumentHandler handler)
			throws Exception {
		if (file.exists()) {
			InputStreamReader inStream = new InputStreamReader(
					new FileInputStream(file), "UTF-8");
			InputSource source = new InputSource(inStream);
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(source);
				handler.handleDocument(doc);
			} finally {
				inStream.close();
			}
		}
	}

	public static List<Client> parseClients(File file) throws Exception {
		final List<Client> clients = new ArrayList<Client>();
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) {
				NodeList list = doc.getElementsByTagName(Client.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					Client cl = new Client();
					cl.setId(e.getAttribute(Client.PROP_ID));
					cl.setName(e.getAttribute(Client.PROP_NAME));
					cl.setPhoneNumber(e.getAttribute(Client.PROP_PHONE_NUMBER));
					cl.setAddress(e.getAttribute(Client.PROP_ADDRESS));
					clients.add(cl);
				}
			}
		});

		return clients;
	}

	public static List<Provider> parseProviders(File file) throws Exception {
		final List<Provider> providers = new ArrayList<Provider>();
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) {
				NodeList list = doc.getElementsByTagName(Provider.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					Provider pr = new Provider();
					pr.setId(e.getAttribute(Provider.PROP_ID));
					pr.setName(e.getAttribute(Provider.PROP_NAME));
					pr.setPhoneNumber(e
							.getAttribute(Provider.PROP_PHONE_NUMBER));
					pr.setAddress(e.getAttribute(Provider.PROP_ADDRESS));
					providers.add(pr);
				}
			}
		});
		return providers;
	}

	public static List<Product> parseProducts(File file) throws Exception {
		final List<Product> products = new ArrayList<Product>();

		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) {
				NodeList list = doc.getElementsByTagName(Product.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					Product pr = new Product();
					String typeAtttr = e.getAttribute(Product.PROP_TYPE);
					if (typeAtttr != null && !typeAtttr.isEmpty()) {
						pr.setType(ProductType.valueOf(typeAtttr));
					} else {
						pr.setType(ProductType.CARD);
					}
					pr.setId(e.getAttribute(Product.PROP_ID));
					pr.setName(e.getAttribute(Product.PROP_NAME));
					pr.setOperator(Operator.valueOf(e
							.getAttribute(Product.PROP_OPEARTOR)));
					pr.setDefaultSaleValue(parseDouble(e,
							Product.PROP_SALE_PERCENT));
					pr.setDefaultPurchaseValue(parseDouble(e,
							Product.PROP_PURCHASE_PERCENT));
					if (pr.getType() == ProductType.CARD) {
						pr.setPrice(parseDouble(e, Product.PROP_PRICE));
					}
					products.add(pr);
				}
			}
		});

		return products;
	}

	public static StockState parseStock(File file,
			final Map<String, Product> productsMap) throws Exception {

		final StockState stock = new StockState();
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) throws ParseException {
				NodeList list = doc.getElementsByTagName(StockState.TAG_NAME);
				if (list.getLength() > 0) {
					Element root = (Element) list.item(0);
					list = root.getElementsByTagName(ProductSet.TAG_NAME);
					List<ProductSet> productsStock = new ArrayList<ProductSet>();
					stock.setProductsStock(productsStock);
					for (int i = 0; i < list.getLength(); i++) {
						Element e = (Element) list.item(i);
						productsStock.add(parseProductSet(e, productsMap));
					}

				}
			}
		});

		return stock;
	}

	public static List<Purchase> parsePurchases(File file,
			final Map<String, Product> productsMap,
			final Map<String, Provider> providersMap) throws Exception {
		final List<Purchase> purchases = new ArrayList<Purchase>();
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) throws ParseException {
				NodeList list = doc.getElementsByTagName(Purchase.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					purchases.add(parsePurchase(e, providersMap, productsMap));
				}
			}
		});

		return purchases;
	}

	private static Purchase parsePurchase(Element e,
			Map<String, Provider> providersMap, Map<String, Product> productsMap)
			throws ParseException {
		Purchase purchase = new Purchase();
		purchase.setDate(parseDate(e, Purchase.PROP_DATE));
		purchase.setProvider(providersMap.get(e
				.getAttribute(Purchase.PROP_PROVIDER_ID)));
		NodeList list = e.getElementsByTagName(ProductSet.TAG_NAME);
		List<ProductSet> products = new ArrayList<ProductSet>();
		for (int i = 0; i < list.getLength(); i++) {
			Element el = (Element) list.item(i);
			products.add(parseProductSet(el, productsMap));
		}
		purchase.setProducts(products);
		return purchase;
	}

	public static List<Sale> parseSales(File file,
			final Map<String, Product> productsMap,
			final Map<String, Client> clientsMap) throws Exception {
		final List<Sale> sales = new ArrayList<Sale>();
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) throws ParseException {
				NodeList list = doc.getElementsByTagName(Sale.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					sales.add(parseSale(e, clientsMap, productsMap));
				}
			}
		});

		return sales;
	}

	private static Sale parseSale(Element e, Map<String, Client> clientsMap,
			Map<String, Product> productsMap) throws ParseException {
		Sale sale = new Sale();
		sale.setDate(parseDate(e, Sale.PROP_DATE));
		sale.setClient(clientsMap.get(e.getAttribute(Sale.PROP_CLIENT_ID)));
		sale.setCost(parseDouble(e, Sale.PROP_COST));
		sale.setPayed(parseDouble(e, Sale.PROP_PAYED));
		Element saleRoot = (Element) e.getElementsByTagName(
				Sale.TAG_SALE_PRODUCTS).item(0);
		NodeList list = saleRoot.getElementsByTagName(ProductSet.TAG_NAME);
		List<ProductSet> products = new ArrayList<ProductSet>();
		for (int i = 0; i < list.getLength(); i++) {
			Element el = (Element) list.item(i);
			products.add(parseProductSet(el, productsMap));
		}
		sale.setSaleProducts(products);

		Element purchaseRoot = (Element) e.getElementsByTagName(
				Sale.TAG_PURCHASE_PRODUCTS).item(0);
		list = purchaseRoot.getElementsByTagName(ProductSet.TAG_NAME);
		products = new ArrayList<ProductSet>();
		for (int i = 0; i < list.getLength(); i++) {
			Element el = (Element) list.item(i);
			products.add(parseProductSet(el, productsMap));
		}
		sale.setPurchaseProducts(products);

		return sale;
	}

	private static ProductSet parseProductSet(Element e,
			Map<String, Product> productsMap) throws ParseException {
		ProductSet prodSet = new ProductSet();
		prodSet.setCount(parseInt(e, ProductSet.PROP_COUNT));
		prodSet.setUnitPrice(parseDouble(e, ProductSet.PROP_UNIT_PRICE));
		prodSet.setProduct(productsMap.get(e
				.getAttribute(ProductSet.PROP_PRODUCT_ID)));
		return prodSet;
	}

	public static void parsePays(File file,
			final Map<String, Client> clientsMap,
			final Map<String, Provider> providersMap,
			final List<ClientPay> clientPays,
			final List<ProviderPay> providerPays) throws Exception {
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) throws ParseException {
				NodeList list = doc.getElementsByTagName(ClientPay.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					clientPays.add(parseClientPay(e, clientsMap));
				}
				list = doc.getElementsByTagName(ProviderPay.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					providerPays.add(parseProviderPay(e, providersMap));
				}
			}
		});

	}

	public static void parseInitData(File file,
			final Map<String, Client> clientsMap,
			final Map<String, Provider> providersMap, final InitData data)
			throws Exception {
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) throws ParseException {
				NodeList rootList = doc.getElementsByTagName(InitData.TAG_NAME);
				if (rootList.getLength() > 0) {
					Element root = (Element) rootList.item(0);
					data.setInitialCash(parseDouble(root,
							InitData.PROP_INITIAL_CASH));
					NodeList list = root
							.getElementsByTagName(PersonAmount.CLIENT_TAG_NAME);
					for (int i = 0; i < list.getLength(); i++) {
						Element e = (Element) list.item(i);
						data.getClientCredits().add(
								parseClientCredit(e, clientsMap));
					}
					list = doc
							.getElementsByTagName(PersonAmount.PROVIDER_TAG_NAME);
					for (int i = 0; i < list.getLength(); i++) {
						Element e = (Element) list.item(i);
						data.getProviderCredits().add(
								parseProviderCredit(e, providersMap));
					}
				}
			}
		});

	}

	private static ClientPay parseClientPay(Element e,
			Map<String, Client> clientsMap) throws ParseException {
		ClientPay pay = new ClientPay();
		pay.setDate(parseDate(e, ClientPay.PROP_DATE));
		pay.setClient(clientsMap.get(e.getAttribute(ClientPay.PROP_CLIENT_ID)));
		pay.setAmount(parseDouble(e, ClientPay.PROP_AMOUNT));
		return pay;
	}

	private static PersonAmount<Client> parseClientCredit(Element e,
			Map<String, Client> clientsMap) throws ParseException {
		PersonAmount<Client> credit = new PersonAmount<Client>();
		credit.setPerson(clientsMap.get(e
				.getAttribute(PersonAmount.PROP_CLIENT_ID)));
		credit.setAmount(parseDouble(e, PersonAmount.PROP_AMOUNT));
		return credit;
	}

	private static PersonAmount<Provider> parseProviderCredit(Element e,
			Map<String, Provider> providersMap) throws ParseException {
		PersonAmount<Provider> credit = new PersonAmount<Provider>();
		credit.setPerson(providersMap.get(e
				.getAttribute(PersonAmount.PROP_PROVIDER_ID)));
		credit.setAmount(parseDouble(e, PersonAmount.PROP_AMOUNT));
		return credit;
	}

	private static ProviderPay parseProviderPay(Element e,
			Map<String, Provider> providersMap) throws ParseException {
		ProviderPay pay = new ProviderPay();
		pay.setDate(parseDate(e, ProviderPay.PROP_DATE));
		pay.setProvider(providersMap.get(e
				.getAttribute(ProviderPay.PROP_PROVIDER_ID)));
		pay.setAmount(parseDouble(e, ProviderPay.PROP_AMOUNT));
		return pay;
	}

	private static Date parseDate(Element e, String name) throws ParseException {
		try {
			return DataUtils.DATE_FORMAT.parse(e.getAttribute("date"));
		} catch (Exception ex) {
			return null;
		}
	}

	private static double parseDouble(Element e, String name) {
		try {
			return Double.parseDouble(e.getAttribute(name));
		} catch (Exception ex) {
			return 0.0;
		}
	}

	private static int parseInt(Element e, String name) {
		try {
			return Integer.parseInt(e.getAttribute(name));
		} catch (Exception ex) {
			return 0;
		}
	}

	public static List<Expense> parseExpenses(File file) throws Exception {
		final List<Expense> expenses = new ArrayList<Expense>();
		parse(file, new DocumentHandler() {

			@Override
			public void handleDocument(Document doc) throws ParseException {
				NodeList list = doc.getElementsByTagName(Expense.TAG_NAME);
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element) list.item(i);
					Expense ex = new Expense();
					ex.setDate(parseDate(e, Expense.PROP_DATE));
					ex.setAmount(parseDouble(e, Expense.PROP_AMOUNT));
					expenses.add(ex);
				}
			}
		});

		return expenses;
	}
}
