package ma.mla.callcards.model;


public class ProductSet {

	public static final String TAG_NAME = "productSet";
	public static final String PROP_PRODUCT_ID = "productId";
	public static final String PROP_COUNT = "count";
	public static final String PROP_UNIT_PRICE = "unitPrice";

	private Product product;
	private int count;
	private double unitPrice;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getCost() {
		return unitPrice * count;
	}

	public ProductSet copy() {
		ProductSet copy = new ProductSet();
		copy.setProduct(product);
		copy.setCount(count);
		copy.setUnitPrice(unitPrice);
		return copy;
	}

}
