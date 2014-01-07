package ma.mla.callcards.dao;

public interface StorageListener {

	public static enum ChangeType {
		OPEN, CLOSE, MODIFY
	}

	public void folderChanged(Storage storage, ChangeType type);

}
