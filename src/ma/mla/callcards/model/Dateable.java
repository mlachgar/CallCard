package ma.mla.callcards.model;

import java.util.Date;

public class Dateable {

	public static final String PROP_DATE = "date";

	private Date date;

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
}
