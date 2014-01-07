package ma.mla.callcards.model;

import java.util.List;

public class ProviderSheet {

	private Provider provider;
	private List<ProviderPay> pays;
	private List<Purchase> purchases;
	private double totalPurchases;
	private double totalPays;
	private double credit;
	private double oldCredit;

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public List<ProviderPay> getPays() {
		return pays;
	}

	public void setPays(List<ProviderPay> pays) {
		this.pays = pays;
	}

	public List<Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<Purchase> purchases) {
		this.purchases = purchases;
	}

	public double getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(double totalPurchases) {
		this.totalPurchases = totalPurchases;
	}

	public double getTotalPays() {
		return totalPays;
	}

	public void setTotalPays(double totalPays) {
		this.totalPays = totalPays;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getOldCredit() {
		return oldCredit;
	}

	public void setOldCredit(double oldCredit) {
		this.oldCredit = oldCredit;
	}

}
