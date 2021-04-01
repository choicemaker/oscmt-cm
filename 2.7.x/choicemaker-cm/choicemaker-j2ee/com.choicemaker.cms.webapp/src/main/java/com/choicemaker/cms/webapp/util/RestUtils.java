package com.choicemaker.cms.webapp.util;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

public class RestUtils {

	private static String BOL = "  ==> ";
	private static String EOL = System.lineSeparator();
	private static String SEP = ": ";
	private static String NULL = "null";

	public static void logMultivaluedStringMap(Logger logger, Level p,
			String contextMessage, MultivaluedMap<String, String> map) {

		boolean createMessage = false;
		if (logger != null && p != null && logger.isLoggable(p)) {
			// Logger exists, level is loggable
			createMessage = true;
		} else if (logger == null || p == null) {
			// Something is wrong, so log to standard error instead
			createMessage = true;
		} else {
			// The only time a message isn't created is if a level is
			// is not loggable
			assert logger != null && p != null && !logger.isLoggable(p) ;
		}

		if (createMessage) {
			StringBuffer sb = new StringBuffer();
			if (contextMessage != null) {
				sb.append(contextMessage).append(EOL);
			}
			if (map != null) {
				for (String key : map.keySet()) {
					List<String> values = map.get(key);
					String s;
					if (values != null) {
						s = Arrays.deepToString(values.toArray());
					} else {
						s = NULL;
					}
					sb.append(BOL).append(key).append(SEP).append(s)
							.append(EOL);
				}
			}
			if (logger != null) {
				assert logger.isLoggable(p);
				logger.log(p, sb.toString());
			} else {
				System.err.println(sb.toString());
			}
		}
	}

	private RestUtils() {
	}

}
