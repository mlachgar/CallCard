package ma.mla.callcards.model;

import java.util.List;

public interface PrintableView {

	public String getPrintTitle();

	public List<PrintableControl> getPrintableControls();

}
