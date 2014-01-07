package ma.mla.callcards.model;

import java.util.ArrayList;
import java.util.List;

public class Sale extends Dateable {

	public static final String TAG_NAME = "sale";
	public static final String PROP_CLIENT_ID = "clientId";
	public static final String PROP_COST = "cost";
	public static final String PROP_PAYED = "payed";

	public static final String TAG_SALE_PRODUCTS = "saleProducts";
	public static final String TAG_PURCHASE_PRODUCTS = "purchaseProducts";

	private Client client;
	private List<ProductSet> saleProducts = new ArrayList<ProductSet>();
	private List<ProductSet> purchaseProducts = new ArrayList<ProductSet>();
	private double cost;
	private double payed;

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

	public List<ProductSet> getSaleProducts() {
		return saleProducts;
	}

	public void setSaleProducts(List<ProductSet> products) {
		this.saleProducts = products;
	}

	public List<ProductSet> getPurchaseProducts() {
		return purchaseProducts;
	}

	public void setPurchaseProducts(List<ProductSet> purchaseProducts) {
		this.purchaseProducts = purchaseProducts;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getPayed() {
		return payed;
	}

	public void setPayed(double payed) {
		this.payed = payed;
	}

	public double getTotal() {
		double total = 0.;
		for (ProductSet ps : saleProducts) {
			total += ps.getCost();
		}
		return total;
	}

}
