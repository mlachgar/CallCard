package ma.mla.callcards.model;

public class ProviderPay extends Pay {

	public static final String TAG_NAME = "providerPay";
	public static final String PROP_PROVIDER_ID = "providerId";

	private Provider provider;

	public ProviderPay() {
		super(PersonType.PROVIDER);
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
