package ma.mla.callcards.model;

public class Client extends Person {

	public static final String TAG_NAME = "client";

	public Client() {

	}

	public Client(String name, String phoneNumber, String address) {
		super(name, phoneNumber, address);
	}

}
