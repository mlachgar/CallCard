package ma.mla.callcards.model;

public class Product extends NamedObject {

	public static final String TAG_NAME = "product";
	public static final String PROP_TYPE = "type";
	public static final String PROP_OPEARTOR = "operator";
	public static final String PROP_PRICE = "price";
	public static final String PROP_SALE_PERCENT = "salePercent";
	public static final String PROP_PURCHASE_PERCENT = "purchaePercent";

	private ProductType type;
	private Operator operator;
	private double price;
	private double defaultSaleValue;
	private double defaultPurchaseValue;

	public Product() {

	}

	public Product(ProductType type) {
		this.type = type;
	}

	public Product(String name, Operator operator, double defaultSalePrice,
			double defaultPurchasePrice) {
		super(name);
		this.type = ProductType.WALLET;
		this.operator = operator;
		this.defaultSaleValue = defaultSalePrice;
		this.defaultPurchaseValue = defaultPurchasePrice;
	}

	public Product(String name, Operator operator, double price,
			double defaultSalePrecent, double defaultPurchasePercent) {
		super(name);
		this.type = ProductType.CARD;
		this.operator = operator;
		this.price = price;
		this.defaultSaleValue = defaultSalePrecent;
		this.defaultPurchaseValue = defaultPurchasePercent;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getDefaultSaleValue() {
		return defaultSaleValue;
	}

	public void setDefaultSaleValue(double defaultSalePercent) {
		this.defaultSaleValue = defaultSalePercent;
	}

	public double getDefaultPurchaseValue() {
		return defaultPurchaseValue;
	}

	public void setDefaultPurchaseValue(double defaultPurchasePercent) {
		this.defaultPurchaseValue = defaultPurchasePercent;
	}

	public double getUnitPrice(double value) {
		if (type == ProductType.CARD) {
			return price * (1. - (value / 100.));
		} else {
			return value;
		}
	}

	public double getValue(double unitPrice) {
		if (type == ProductType.CARD) {
			return (1 - (unitPrice / price)) * 100.0;
		} else {
			return unitPrice;
		}
	}

}
