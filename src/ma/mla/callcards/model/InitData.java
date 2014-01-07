package ma.mla.callcards.model;

import java.util.ArrayList;
import java.util.List;

public class InitData {

	public static final String TAG_NAME = "initData";
	public static final String PROP_INITIAL_CASH = "initialCash";

	private double initialCash;
	private List<PersonAmount<Client>> clientCredits = new ArrayList<PersonAmount<Client>>();
	private List<PersonAmount<Provider>> providerCredits = new ArrayList<PersonAmount<Provider>>();

	public void clear() {
		initialCash = 0.0;
		clientCredits.clear();
		providerCredits.clear();
	}

	public double getInitialCash() {
		return initialCash;
	}

	public void setInitialCash(double initialCash) {
		this.initialCash = initialCash;
	}

	public List<PersonAmount<Client>> getClientCredits() {
		return clientCredits;
	}

	public void setClientCredits(List<PersonAmount<Client>> clientCredits) {
		this.clientCredits = clientCredits;
	}

	public List<PersonAmount<Provider>> getProviderCredits() {
		return providerCredits;
	}

	public void setProviderCredits(List<PersonAmount<Provider>> providerCredits) {
		this.providerCredits = providerCredits;
	}

}
