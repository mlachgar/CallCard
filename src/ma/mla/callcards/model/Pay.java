package ma.mla.callcards.model;

public class Pay extends Dateable {

	public static final String PROP_AMOUNT = "amount";
	public static final String PROP_TYPE = "type";

	public static enum PersonType {
		CLIENT, PROVIDER
	};

	private double amount;
	private final PersonType type;

	public Pay(PersonType type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public PersonType getType() {
		return type;
	}

}
