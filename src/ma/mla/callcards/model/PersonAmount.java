package ma.mla.callcards.model;

public class PersonAmount<T extends Person> {

	public static final String PROVIDER_TAG_NAME = "providerCredit";
	public static final String PROP_PROVIDER_ID = "providerId";
	public static final String CLIENT_TAG_NAME = "clientCredit";
	public static final String PROP_CLIENT_ID = "clientId";
	public static final String PROP_AMOUNT = "amount";

	private T person;
	private double amount;

	public PersonAmount() {

	}

	public PersonAmount(T person, double amount) {
		this.person = person;
		this.amount = amount;
	}

	public T getPerson() {
		return person;
	}

	public void setPerson(T person) {
		this.person = person;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
