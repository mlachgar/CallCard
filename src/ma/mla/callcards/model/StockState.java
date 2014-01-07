package ma.mla.callcards.model;

import java.util.ArrayList;
import java.util.List;

public class StockState {

	public static final String TAG_NAME = "stock";
	public static final String PROP_INVEST = "invest";
	public static final String PROP_INPUT = "input";
	public static final String PROP_OUTPUT = "output";

	private List<ProductSet> productsStock = new ArrayList<ProductSet>();

	public List<ProductSet> getProductsStock() {
		return productsStock;
	}

	public void setProductsStock(List<ProductSet> productsStock) {
		this.productsStock = productsStock;
	}

}
