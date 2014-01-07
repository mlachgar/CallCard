package ma.mla.callcards.model;

import java.util.UUID;

public abstract class NamedObject extends PersistentObject {

	public static final String PROP_NAME = "name";
	protected String name = "";

	public NamedObject() {

	}

	public NamedObject(String name) {
		this.name = name;
		id = UUID.randomUUID().toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
