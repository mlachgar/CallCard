package ma.mla.callcards.utils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;

public class CloseableContentProposalAdapter extends ContentProposalAdapter {

	public CloseableContentProposalAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IContentProposalProvider proposalProvider, KeyStroke keyStroke,
			char[] autoActivationCharacters) {
		super(control, controlContentAdapter, proposalProvider, keyStroke,
				autoActivationCharacters);
	}

	public void hide() {
		closeProposalPopup();
	}

}
