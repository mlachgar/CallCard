package ma.mla.callcards.model;

public class Expense extends Dateable {

	public static final String TAG_NAME = "expense";
	public static final String PROP_AMOUNT = "amount";

	private double amount;

	public Expense() {

	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
