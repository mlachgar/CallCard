package ma.mla.callcards.views;

public enum DateFilter {
	ALL("Tout"), TODAY("Aujourd'huit"), THIS_WEEK("Cette semaine"), THIS_MONTH(
			"Ce mois"), DATES("Dates");

	private DateFilter(String label) {
		this.label = label;
	}

	public final String label;

	@Override
	public String toString() {
		return label;
	}

}