package ma.mla.callcards.model;

import java.util.ArrayList;
import java.util.List;

public class Purchase extends Dateable {

	public static final String TAG_NAME = "purchase";
	public static final String PROP_PROVIDER_ID = "providerId";

	private Provider provider;
	private List<ProductSet> products = new ArrayList<ProductSet>();

	public List<ProductSet> getProducts() {
		return products;
	}

	public void setProducts(List<ProductSet> products) {
		this.products = products;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public double getTotal() {
		double total = 0.;
		for (ProductSet ps : products) {
			total += ps.getCost();
		}
		return total;
	}

}
