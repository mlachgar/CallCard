package ma.mla.callcards.dao;

import java.io.File;

import ma.mla.callcards.model.AccountsSummary;

public class HistoryItem {

	private final File file;
	private AccountsSummary content = null;

	public HistoryItem(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setContent(AccountsSummary content) {
		this.content = content;
	}

	public AccountsSummary getContent() {
		return content;
	}

	public int compraeTo(HistoryItem other) {
		long diff = getFile().lastModified() - other.getFile().lastModified();
		if (diff == 0) {
			return 0;
		}
		return diff < 0 ? -1 : 1;
	}
}
