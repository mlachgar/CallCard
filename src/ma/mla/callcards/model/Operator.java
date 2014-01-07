package ma.mla.callcards.model;

public enum Operator {

	IAM("IAM"), JAWAL("Jawal"), MEDITEL("Meditel"), INWI("Inwi"), BAYN("Bayn");

	private final String name;

	private Operator(String label) {
		this.name = label;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
