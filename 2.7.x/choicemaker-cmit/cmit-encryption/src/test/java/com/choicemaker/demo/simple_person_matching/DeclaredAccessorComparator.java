package com.choicemaker.demo.simple_person_matching;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.choicemaker.util.Precondition;

/**
 * FIXME This class is <b><i>almost</i></b> good enough for general use, but
 * first it needs to handle array-valued accessors, and then it needs test cases
 * for array-valued accessors.
 *
 * @param <T>
 *            specifies a class or interface that declares accessors with the
 *            same names as the accessors that will be compared between two
 *            instances. Note that the instances may or may not implement or
 *            extend <code>T</code>, because <code>T</code> and the instance
 *            classes are not necessarily defined by the same class loader. The
 *            class <code>T</code> is used only to get the names of accessors
 *            that should be compared between two instances.
 */
public class DeclaredAccessorComparator<T> implements Comparator<Object> {

	public static final String DEFAULT_ACCESSOR_REGEX = "(is|get)\\p{Upper}.*";

	public static Set<String> getDeclaredAccessorNames(Pattern p, Class<?> c) {
		return getDeclaredAccessorNames(p, c, false);
	}

	public static Set<String> getDeclaredAccessorNames(Pattern p, Class<?> c,
			boolean sort) {
		Precondition.assertNonNullArgument("null pattern", p);
		Precondition.assertNonNullArgument("null class", c);

		Set<String> retVal;
		if (sort) {
			retVal = new TreeSet<>();
		} else {
			retVal = new LinkedHashSet<>();
		}
		Method[] methods = c.getDeclaredMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			Matcher m = p.matcher(methodName);
			if (m.matches()) {
				retVal.add(methodName);
			}
		}
		return Collections.unmodifiableSet(retVal);
	}

	private final Class<T> clz;
	private final Pattern p;
	private final boolean areNamesSorted;
	private final Set<String> accessorNames;

	public DeclaredAccessorComparator(Class<T> c, boolean sort) {
		this(c, DEFAULT_ACCESSOR_REGEX, sort);
	}

	public DeclaredAccessorComparator(Class<T> c, String regex, boolean sort) {
		Precondition.assertNonNullArgument("null class", c);
		Precondition.assertNonEmptyString("null regex", regex);
		this.clz = c;
		this.p = Pattern.compile(regex);
		this.areNamesSorted = sort;
		this.accessorNames = getDeclaredAccessorNames(p, clz, areNamesSorted);
	}

	public Class<T> getHandledClass() {
		return clz;
	}

	public Pattern getAccesorNamePattern() {
		return p;
	}

	public Set<String> getAccessorNames() {
		return accessorNames;
	}

	public Map<String, Object> getAccessorValues(Object o)
			throws NoSuchMethodException {
		Precondition.assertNonNullArgument("null object", o);

		final Class<?> objectClass = o.getClass();

		Map<String, Object> retVal;
		if (areNamesSorted) {
			retVal = new TreeMap<>();
		} else {
			retVal = new LinkedHashMap<>();
		}
		for (String an : getAccessorNames()) {
			try {
				Method m = objectClass.getMethod(an, (Class[]) null);
				Object value = m.invoke(o, (Object[]) null);
				if (value != null) {
					retVal.put(an, value);
				}
			} catch (SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException x) {
				throw new RuntimeException(x.toString());
			}
		}

		return Collections.unmodifiableMap(retVal);
	}

	public boolean haveEqualAccessorValues(Object o1, Object o2)
			throws NoSuchMethodException {
		boolean retVal = o1 != null && o2 != null;
		if (retVal) {
			Map<String, Object> values1 = getAccessorValues(o1);
			Map<String, Object> values2 = getAccessorValues(o2);
			// Falls apart if any value of any key is an array
			retVal = values1.equals(values2);
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		DetailedComparison: try {
			final Map<String, Object> values1 = getAccessorValues(o1);
			final Map<String, Object> values2 = getAccessorValues(o2);

			// Check number of accessor values
			final int count1 = values1.size();
			final int count2 = values2.size();
			retVal = Integer.compare(count1, count2);
			if (retVal != 0)
				break DetailedComparison;

			// Check keys, then values
			Iterator<Map.Entry<String, Object>> it1 = values1.entrySet()
					.iterator();
			Iterator<Map.Entry<String, Object>> it2 = values2.entrySet()
					.iterator();
			while (it1.hasNext()) {
				Map.Entry<String, Object> e1 = it1.next();
				Map.Entry<String, Object> e2 = it2.next();
				String key1 = e1.getKey();
				String key2 = e2.getKey();
				retVal = key1.compareTo(key2);
				if (retVal != 0)
					break DetailedComparison;

				Object value1 = e1.getValue();
				assert value1 != null;
				Object value2 = e2.getValue();
				assert value2 != null;
				if (value1 instanceof Comparable) {
					@SuppressWarnings("rawtypes")
					Comparable c1 = (Comparable) value1;
					@SuppressWarnings("rawtypes")
					Comparable c2 = (Comparable) value2;
					retVal = c1.compareTo(c2);
					if (retVal != 0)
						break DetailedComparison;
				} else if (value1.getClass().isArray()) {
					throw new Error(
							"Comparison of array-valued value is not yet implemented");
				} else {
					// Ignore accessors that return non-array values that can't
					// be compared
					assert retVal == 0;
				}
			}
		} catch (Exception x) {
			throw new RuntimeException(x.toString());
		}
		return retVal;
	}
}
