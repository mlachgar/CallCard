package ma.mla.callcards.dao;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ma.mla.callcards.dao.StorageListener.ChangeType;
import ma.mla.callcards.model.AccountsSummary;
import ma.mla.callcards.utils.DataUtils;
import ma.mla.callcards.utils.TaskUtils;

public class StorageManager {

	public static final String LAST_FOLDER_KEY = "ccb.last.folder";
	public static final String PREF_FILE = ".pref";
	public static final int LOCK_PORT = 25255;
	public static final String RESTORE_DIR = "restore";
	private static LocalStorage storage;
	private static File zipFile;
	private static final List<StorageListener> storageListeners = new ArrayList<StorageListener>();
	private static File ccbDir;
	private static File defaultDir;
	private static File restoreDir;
	private static boolean editable;
	private static ServerSocket socket;

	public static Storage getStorage() {
		return storage;
	}

	public static void addStorageListener(StorageListener sl) {
		storageListeners.add(sl);
	}

	public static void removeStorageListener(StorageListener sl) {
		storageListeners.remove(sl);
	}

	public static void fireStorageChange(Storage storage, ChangeType type) {
		for (StorageListener sl : storageListeners) {
			sl.folderChanged(storage, type);
		}
	}

	private static void checkStorage() {
		if (storage == null) {
			storage = new LocalStorage();
			storage.addChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					fireStorageChange(storage, ChangeType.MODIFY);
				}
			});
		}
	}

	public static void newFolder(String name) {
		checkStorage();
		storage.reset(name);
		zipFile = null;
		fireStorageChange(storage, ChangeType.OPEN);
	}

	public static String restoreFolder(File zipFile) throws Exception {
		checkStorage();
		File home = getHomeDir(zipFile);
		ZipUtils.unzipFolder(zipFile.getAbsolutePath(), home.getAbsolutePath());
		String problems = storage.load(home);
		zipFile = null;
		fireStorageChange(storage, ChangeType.OPEN);
		return problems;
	}

	public static String openFolder(String path) throws Exception {
		checkStorage();
		zipFile = new File(path);
		File home = getHomeDir(zipFile);
		ZipUtils.unzipFolder(path, home.getAbsolutePath());
		String problems = storage.load(home);
		fireStorageChange(storage, ChangeType.OPEN);
		return problems;
	}

	public static void continueInNewFolder(String name) {
		checkStorage();
		storage.continueInNewFolder(name);
		zipFile = null;
		fireStorageChange(storage, ChangeType.OPEN);
	}

	public static boolean isNewFolder() {
		return storage != null && zipFile == null;
	}

	public static boolean isEditable() {
		return editable;
	}

	public static void saveFolder() throws Exception {
		if (storage != null && storage.isDirty()) {
			File home = getHomeDir(zipFile);
			storage.save(home);
			ZipUtils.zipFolder(home, zipFile);
			saveRestore(zipFile);
			fireStorageChange(storage, ChangeType.MODIFY);
		}
	}

	public static void saveFolderAs(File file) throws Exception {
		if (storage != null) {
			zipFile = file;
			File home = getHomeDir(zipFile);
			storage.save(home);
			ZipUtils.zipFolder(home, zipFile);
			saveRestore(zipFile);
			fireStorageChange(storage, ChangeType.MODIFY);
		}
	}

	private static void saveRestore(File zipFile) {
		try {
			File dir = getRestoreDir();
			File file = new File(dir, zipFile.getName().replace(".ccb",
					"." + System.currentTimeMillis() + ".ccb"));
			FileOutputStream out = new FileOutputStream(file);
			FileInputStream in = new FileInputStream(zipFile);
			byte[] buffer = new byte[8192];
			int size;
			while ((size = in.read(buffer)) != -1) {
				out.write(buffer, 0, size);
			}
			out.close();
			in.close();
		} catch (Exception ex) {
			System.err.println("Save retsore error : " + ex);
		}
	}

	public static void cleanRestore() {
		File dir = getRestoreDir();
		long oneMonth = 30L * 24L * 60L * 60L * 1000L;
		long now = System.currentTimeMillis();
		for (File f : dir.listFiles()) {
			if (now - f.lastModified() > oneMonth) {
				f.delete();
			}
		}
	}

	public static File getHomeDir(File zipFile) {
		File dir = new File(getCcbDir(), zipFile.getName().replace(".ccb", ""));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static File getCcbDir() {
		if (ccbDir == null) {
			ccbDir = new File(System.getProperty("user.home"), "ccb");
			if (!ccbDir.exists()) {
				ccbDir.mkdirs();
			}
		}
		return ccbDir;
	}

	public static File getDefaultDir() {
		if (defaultDir == null) {
			defaultDir = new File(getCcbDir(), "default");
			if (!defaultDir.exists()) {
				defaultDir.mkdirs();
			}
		}
		return defaultDir;
	}

	public static File getRestoreDir() {
		if (restoreDir == null) {
			restoreDir = new File(getCcbDir(), RESTORE_DIR);
			if (!restoreDir.exists()) {
				restoreDir.mkdirs();
			}
		}
		return restoreDir;
	}

	private static void delete(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				delete(f);
			}
			file.delete();
		}
	}

	public static void start() throws IOException {
		try {
			socket = new ServerSocket(LOCK_PORT);
			editable = true;
		} catch (Exception ex) {
			editable = false;
		}
	}

	public static void stop() {
		if (editable) {
			File dir = getCcbDir();
			for (File f : dir.listFiles()) {
				if (!PREF_FILE.equals(f.getName())
						&& !RESTORE_DIR.equals(f.getName())) {
					delete(f);
				}
			}
			saveCurrentFolder();
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Enable to close the socket : "
						+ e.getMessage());
			}
		}
	}

	public static void saveCurrentFolder() {
		try {
			if (zipFile != null) {
				File f = new File(getCcbDir(), PREF_FILE);
				FileOutputStream out = new FileOutputStream(f);
				Properties props = new Properties();
				props.setProperty(LAST_FOLDER_KEY, zipFile.getAbsolutePath());
				props.store(out, "");
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void restoreLastFolder() {
		try {
			String path = null;
			String cmd = System.getProperty("eclipse.commands");
			if (cmd != null) {
				String[] args = cmd.split("\n");
				for (int i = 0; i < args.length; i++) {
					// System.err.println("arg : " + args[i]);
					if (i > 0 && args[i].equals("-vm")
							&& args[i - 1].endsWith(".ccb")) {
						path = args[i - 1];
						System.err.println("Found argument file : " + path);
						break;
					}
				}
			}
			if (path == null) {
				File prefFile = new File(getCcbDir(), PREF_FILE);
				if (prefFile.exists()) {
					FileInputStream in = new FileInputStream(prefFile);
					Properties props = new Properties();
					props.load(in);
					in.close();
					path = props.getProperty(LAST_FOLDER_KEY);
				}
			}
			if (path != null && !path.isEmpty()) {
				File folder = new File(path);
				if (folder.exists()) {
					TaskUtils.openFolder(path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<StorageHistory> getHistory() {
		List<StorageHistory> history = new ArrayList<StorageHistory>();
		try {
			Map<String, StorageHistory> map = new HashMap<String, StorageHistory>();
			File dir = getRestoreDir();
			for (File f : dir.listFiles()) {
				Properties meta = ZipUtils.extractMetadata(f,
						Storage.FN_METADATA);
				String name = meta.getProperty(Storage.META_FOLDER_NAME);
				if (name != null) {
					StorageHistory h = map.get(name);
					if (h == null) {
						h = new StorageHistory(name);
						map.put(name, h);
						history.add(h);
					}
					HistoryItem item = new HistoryItem(f);
					AccountsSummary content = new AccountsSummary();
					content.totalStock = DataUtils.getDoubleProperty(meta,
							Storage.META_TOTAL_STOCK);
					content.totalClientCredit = DataUtils.getDoubleProperty(
							meta, Storage.META_CLIENT_CREDIT);
					content.totalProviderCredit = DataUtils.getDoubleProperty(
							meta, Storage.META_PROVIDER_CREDIT);
					content.totalCash = DataUtils.getDoubleProperty(meta,
							Storage.META_TOTAL_CASH);
					content.totalPurchases = DataUtils.getDoubleProperty(meta,
							Storage.META_TOTAL_PURCHASES);
					content.totalExpenses = DataUtils.getDoubleProperty(meta,
							Storage.META_TOTAL_EXPENSES);
					content.totalProviderPays = DataUtils.getDoubleProperty(
							meta, Storage.META_TOTAL_PROVIDER_PAYS);
					content.balance = DataUtils.getDoubleProperty(meta,
							Storage.META_BALANCE);
					item.setContent(content);
					h.addItem(item);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		for (StorageHistory h : history) {
			Collections.sort(h.getItems(), new Comparator<HistoryItem>() {
				@Override
				public int compare(HistoryItem i1, HistoryItem i2) {
					return i2.compraeTo(i1);
				}
			});
		}
		Collections.sort(history, new Comparator<StorageHistory>() {
			@Override
			public int compare(StorageHistory h1, StorageHistory h2) {
				return h2.compraeTo(h1);
			}
		});
		return history;
	}

}
