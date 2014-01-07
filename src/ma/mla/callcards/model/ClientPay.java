package ma.mla.callcards.model;

public class ClientPay extends Pay {

	public static final String TAG_NAME = "clientPay";
	public static final String PROP_CLIENT_ID = "clientId";

	private Client client;

	public ClientPay() {
		super(PersonType.CLIENT);
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
