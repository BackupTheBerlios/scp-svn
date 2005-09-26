package de.scp.selector.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static Logger instance;

	private Logger() {
	}

	public static synchronized Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS");

	private String prefix() {
		return format.format(new Date(System.currentTimeMillis()));
	}

	public void debug(String mes) {
		System.out.println(prefix() + " DEBUG " + mes);
	}

	public void info(String mes) {
		System.out.println(prefix() + " INFO  " + mes);
	}

	public void error(String mes) {
		System.err.println(prefix() + " ERROR " + mes);
	}

	public void error(Throwable exc) {
		System.err.println(prefix() + " ERROR " + exc.getMessage());
		exc.printStackTrace();
	}

	/**
	 * @param items
	 * @return
	 */
	public static String arrayToString(Object[] items) {
		String vals = "[";
		for (int i = 0; i < items.length; i++) {
			if (i != 0)
				vals += "', ";
			vals += "'" + items[i].toString();
		}
		vals += "']";
		return vals;
	}

}
