package ma.mla.callcards.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ma.mla.callcards.model.Dateable;
import ma.mla.callcards.model.NamedObject;
import ma.mla.callcards.model.Product;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.DateTime;

public class DataUtils {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd-MM-yyyy");

	public static final ViewerSorter DATEABLE_SORTER = new ViewerSorter() {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			Dateable d1 = (Dateable) e1;
			Dateable d2 = (Dateable) e2;
			return d2.getDate().compareTo(d1.getDate());
		}
	};

	public static final ViewerSorter NAME_SORTER = new ViewerSorter() {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			NamedObject d1 = (NamedObject) e1;
			NamedObject d2 = (NamedObject) e2;
			return d1.getName().compareToIgnoreCase(d2.getName());
		}
	};

	public static final Comparator<Dateable> DATEABLE_COPPARATOR = new Comparator<Dateable>() {
		@Override
		public int compare(Dateable d1, Dateable d2) {
			return d2.getDate().compareTo(d1.getDate());
		}
	};

	public static int compare(Product p1, Product p2) {
		if (p1.getOperator().equals(p2.getOperator())) {
			String l1 = extractLabel(p1.getName());
			String l2 = extractLabel(p2.getName());
			int c = l1.compareTo(l2);
			if (c != 0) {
				return c;
			}
			return (int) (p1.getPrice() - p2.getPrice());
		}
		return p1.getOperator().ordinal() - p2.getOperator().ordinal();
	}

	public static String extractLabel(String name) {
		int index = name.indexOf('(');
		if (index != -1) {
			return name.substring(0, index);
		}
		return name;
	}

	public static boolean equalsDay(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		int[] fields = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH };
		for (int field : fields) {
			if (cal1.get(field) != cal2.get(field)) {
				return false;
			}
		}
		return true;
	}

	public static int compareDays(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		int[] fields = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH };
		for (int field : fields) {
			int f1 = cal1.get(field);
			int f2 = cal2.get(field);
			if (f1 != f2) {
				return f1 - f2;
			}
		}
		return 0;
	}

	public static double round(double d) {
		d = d * 100.;
		long l = (long) d;
		return l * 1. / 100.;
	}

	public static String roundString(double d) {
		return String.format(Locale.US, "%.2f", d);
	}

	public static void setDate(Date date, DateTime dateCmp) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			dateCmp.setYear(cal.get(Calendar.YEAR));
			dateCmp.setMonth(cal.get(Calendar.MONTH));
			dateCmp.setDay(cal.get(Calendar.DAY_OF_MONTH));
		}
	}

	public static Date getDate(DateTime dateCmp) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, dateCmp.getYear());
		cal.set(Calendar.MONTH, dateCmp.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, dateCmp.getDay());
		return cal.getTime();
	}

	public static Date startOfWeek() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	public static Date startOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	public static <T extends Dateable> boolean isIncluded(T item, Date from,
			Date to) {
		Date date = item.getDate();
		if (from != null && compareDays(date, from) < 0) {
			return false;
		}
		if (to != null && compareDays(date, to) > 0) {
			return false;
		}
		return true;
	}

	public static <T extends Dateable> List<T> filter(List<T> list, Date from,
			Date to) {
		if (from != null || to != null) {
			List<T> filtered = new ArrayList<T>();
			for (T d : list) {
				Date date = d.getDate();
				if ((from == null || compareDays(date, from) >= 0)
						&& (to == null || compareDays(date, to) <= 0)) {
					filtered.add(d);
				}
			}
			return filtered;
		} else {
			return list;
		}

	}
}
