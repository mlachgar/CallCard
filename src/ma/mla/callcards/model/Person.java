package ma.mla.callcards.model;

public class Person extends NamedObject {

	public static final String PROP_PHONE_NUMBER = "phoneNumber";
	public static final String PROP_ADDRESS = "address";

	protected String phoneNumber = "";
	protected String address = "";

	public Person() {

	}

	public Person(String name, String phoneNumber, String address) {
		super(name);
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

}
