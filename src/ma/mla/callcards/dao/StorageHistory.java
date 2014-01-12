package ma.mla.callcards.dao;

import java.util.ArrayList;
import java.util.List;

public class StorageHistory {

	private final String name;
	private List<HistoryItem> items = new ArrayList<HistoryItem>();

	public StorageHistory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public HistoryItem getItem(int index) {
		return items.get(index);
	}

	public List<HistoryItem> getItems() {
		return items;
	}

	public void addItem(HistoryItem item) {
		this.items.add(item);
	}

	public int compraeTo(StorageHistory other) {
		if (items.isEmpty() || other.items.isEmpty()) {
			return name.compareTo(other.name);
		}
		return items.get(0).compraeTo(other.items.get(0));
	}
}
