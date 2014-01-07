package ma.mla.callcards.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class GenericProposalProvider<T extends NamedObject> implements
		IContentProposalProvider {

	private List<T> items;

	public GenericProposalProvider(List<T> items) {
		this.items = items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	@SuppressWarnings("unchecked")
	public T extractItem(IContentProposal cp) {
		return ((Proposal) cp).item;
	}

	@Override
	public IContentProposal[] getProposals(String content, int pos) {
		List<Proposal> proposals = new ArrayList<Proposal>();
		for (int i = 0; i < items.size(); i++) {
			T item = items.get(i);
			if (item.getName().toUpperCase().contains(content.toUpperCase())) {
				proposals.add(new Proposal(items.get(i), content, pos));
			}
		}
		IContentProposal[] array = new IContentProposal[proposals.size()];
		for (int i = 0; i < proposals.size(); i++) {
			Proposal p = proposals.get(i);
			array[i] = p;
		}
		return array;
	}

	private class Proposal implements IContentProposal {
		private T item;
		private String content;
		private int pos;

		public Proposal(T item, String content, int pos) {
			this.item = item;
			this.content = content;
			this.pos = pos;
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public int getCursorPosition() {
			return pos;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getLabel() {
			return item.getName();
		}
	}

}
