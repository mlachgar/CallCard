package ma.mla.callcards.model;

import java.util.List;

public class ClientSheet extends NamedObject {

	private Client client;
	private List<ClientPay> pays;
	private List<Sale> sales;
	private double totalSales;
	private double totalPays;
	private double totalCost;
	private double credit;
	private double oldCredit;

	@Override
	public String getName() {
		if (client != null) {
			return client.getName();
		}
		return null;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<ClientPay> getPays() {
		return pays;
	}

	public void setPays(List<ClientPay> pays) {
		this.pays = pays;
	}

	public List<Sale> getSales() {
		return sales;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	public double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public double getTotalPays() {
		return totalPays;
	}

	public void setTotalPays(double totalPays) {
		this.totalPays = totalPays;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getCredit() {
		return credit;
	}

	public void setOldCredit(double oldCredit) {
		this.oldCredit = oldCredit;
	}

	public double getOldCredit() {
		return oldCredit;
	}

}
